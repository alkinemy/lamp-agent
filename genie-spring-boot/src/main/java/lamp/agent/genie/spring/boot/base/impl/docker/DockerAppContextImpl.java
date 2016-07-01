package lamp.agent.genie.spring.boot.base.impl.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.app.docker.DockerAppContainer;
import lamp.agent.genie.core.app.docker.DockerAppContext;
import lamp.agent.genie.utils.CollectionUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DockerAppContextImpl implements DockerAppContext {

	private String id;
	private DockerAppContainer appContainer;
	private DockerClient dockerClient;

	private String imageId;
	private String containerId;

	public DockerAppContextImpl(LampContext lampContext, DockerAppContainer appContainer, DockerClient dockerClient) {
		this.appContainer = appContainer;
		this.id = appContainer.getId();
		this.dockerClient = dockerClient;
	}

	@Override public String getId() {
		return id;
	}

	@Override public void startProcess() {
		log.info("Docker Container Start : id = {}, forcePullImage = {}", getId(), appContainer.isForcePullImage());

		String imageId = getImageId();

		if (StringUtils.isNotBlank(imageId)) {
			InspectImageResponse imageResponse = dockerClient.inspectImageCmd(imageId).exec();
			if (appContainer.isForcePullImage()) {
				log.info("Docker Image Remove : id = {}", imageResponse.getId());
				dockerClient.removeImageCmd(imageResponse.getId()).exec();

				// 1. Pull Image
				log.info("Docker Image Pull : image = {}", appContainer.getImage());
				PullImageCmd pullImageCmd = dockerClient.pullImageCmd(appContainer.getImage());
				pullImageCmd.exec(new PullImageResultCallback()).awaitSuccess();
			}
		} else {
			// 1. Pull Image
			log.info("Docker Image Pull : image = {}", appContainer.getImage());
			PullImageCmd pullImageCmd = dockerClient.pullImageCmd(appContainer.getImage());
			pullImageCmd.exec(new PullImageResultCallback()).awaitSuccess();
		}


		// 2. CreateContainer
		CreateContainerResponse containerResponse = createContainer();
		String containerId = containerResponse.getId();
		setContainerId(containerId);

		// 3. StartContainer
		dockerClient.startContainerCmd(containerId).exec();

//		int exitCode = dockerClient.waitContainerCmd(containerId).exec(new WaitContainerResultCallback())
//			.awaitStatusCode();

		log.info("Docker Container Started : containerId = {}", containerId);
//		return dockerClient.inspectContainerCmd(containerId).exec();
	}

	protected CreateContainerResponse createContainer() {
		CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(appContainer.getImage());
		createContainerCmd.withName(getId());

		if (StringUtils.isNotEmpty(appContainer.getNetwork())) {
			createContainerCmd.withNetworkMode(appContainer.getNetwork());
		}

		if (CollectionUtils.isNotEmpty(appContainer.getPortMappings())) {
			List<ExposedPort> exposedPorts = new ArrayList<>();
			List<PortBinding> portBindings = new ArrayList<>();
			for (String port : appContainer.getPortMappings()) {
				PortBinding portBinding = PortBinding.parse(port);
				exposedPorts.add(portBinding.getExposedPort());
				portBindings.add(portBinding);
			}
			createContainerCmd.withExposedPorts(exposedPorts);
			createContainerCmd.withPortBindings(portBindings);
		}

		if (CollectionUtils.isNotEmpty(appContainer.getVolumes())) {
			List<Bind> binds = new ArrayList<>();
			for (String volume : appContainer.getVolumes()) {
				binds.add(Bind.parse(volume));
			}
			createContainerCmd.withBinds(binds);
		}

		if (CollectionUtils.isNotEmpty(appContainer.getEnv())) {
			createContainerCmd.withEnv(appContainer.getEnv());
		}

		//		if (CollectionUtils.isNotEmpty(container.getVolumesFroms())) {
		//			List<VolumesFrom> volumesFroms = new ArrayList<>();
		//			for (String volumeFromStr : container.getVolumesFroms()) {
		//				VolumesFrom volumesFrom = VolumesFrom.parse(volumeFromStr);
		//				volumesFroms.add(volumesFrom);
		//			}
		//			createContainerCmd.withVolumesFrom(volumesFroms);
		//		}

		CreateContainerResponse containerResponse = createContainerCmd.exec();
		return containerResponse;
	}

	@Override public void stopProcess() {
		log.info("Docker Container Stop : id = {}", getId());
		String containerId = getContainerId();
		if (StringUtils.isNotBlank(containerId)) {
			dockerClient.stopContainerCmd(containerId).exec();

			int exitCode = dockerClient.waitContainerCmd(containerId).exec(new WaitContainerResultCallback())
				.awaitStatusCode();

			dockerClient.removeContainerCmd(containerId).exec();
			log.info("Docker Container Remove : id = {}, containerId = {}", getId(), containerId);

			setContainerId(null);
		}
		log.info("Docker Container Stopped : id = {}, containerId = {}", getId(), containerId);
	}

	@Override public AppStatus getStatus() {
		String containerId = getContainerId();
		log.debug("containerId = {}", containerId);
		if (StringUtils.isNotBlank(containerId)) {
			return getStatus(containerId);
		}
		return AppStatus.STOPPED;
	}

	protected AppStatus getStatus(String containerId) {
		try {
			InspectContainerResponse response = dockerClient.inspectContainerCmd(containerId).exec();
			InspectContainerResponse.ContainerState state = response.getState();
			log.debug("ContainerState = {}", state);
			// status=(created|restarting|running|paused|exited|dead)
			return state.getRunning() ? AppStatus.RUNNING : AppStatus.STOPPED;
		} catch (NotFoundException e) {
			setContainerId(null);
			log.warn("Docker Container not found ", e);
		}
		return AppStatus.STOPPED;
	}

	public String getContainerId() {
		String containerId = this.containerId;
		if (StringUtils.isBlank(containerId)) {
			String containerName = "/" + getId();
			List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
			for (Container container : containers) {
				for (String name : container.getNames()) {
					if (containerName.equals(name)) {
						containerId = container.getId();
						setContainerId(containerId);
						setImageId(container.getImageId());
						return containerId;
					}
				}
			}
		}
		return containerId;
	}

	protected void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getImageId() {
		return this.imageId;
	}

	protected void setImageId(String imageId) {
		this.imageId = imageId;
	}

	@Override public boolean isProcessRunning() {
		return AppStatus.RUNNING.equals(getStatus());
	}

	@Override public InputStream getStdOutInputStream() throws IOException {
		LogContainerStreamCallback streamCallback = new LogContainerStreamCallback();
		dockerClient.logContainerCmd(getContainerId()).withStdOut(true).exec(streamCallback);
		return streamCallback.getInputStream();
	}

	@Override public InputStream getStdErrInputStream() throws IOException {
		LogContainerStreamCallback streamCallback = new LogContainerStreamCallback();
		dockerClient.logContainerCmd(getContainerId()).withStdErr(true).exec(streamCallback);
		return streamCallback.getInputStream();
	}


	public static class LogContainerStreamCallback extends LogContainerResultCallback {

		private final PipedOutputStream outputStream = new PipedOutputStream();
		@Getter
		private final PipedInputStream inputStream = new PipedInputStream();


		public LogContainerStreamCallback() throws IOException {
			inputStream.connect(outputStream);
		}


		@Override
		public void onNext(Frame frame) {
			try {
				outputStream.write(frame.getPayload());
			} catch (IOException e) {
				log.error("LogContainerStreamCallback write failed", e);
			}
		}

	}
}
