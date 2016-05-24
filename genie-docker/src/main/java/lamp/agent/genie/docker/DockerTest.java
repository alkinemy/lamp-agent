package lamp.agent.genie.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Created by kangwoo on 2016. 5. 20..
 */
public class DockerTest {

	public static void main(String[] args) throws Exception {

//		bash -c "clear && DOCKER_HOST=tcp://192.168.99.100:2376 DOCKER_CERT_PATH=/Users/kangwoo/.docker/machine/machines/default DOCKER_TLS_VERIFY=1 /usr/local/bin/zsh"

		DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder()
			.withDockerHost("tcp://192.168.99.100:2376")
//			.withDockerTlsVerify(true)
			.withDockerCertPath("/Users/kangwoo/.docker/machine/machines/default")
//			.withDockerConfig("/Users/kangwoo/.docker")
			.withApiVersion("1.21")
//			.withRegistryUrl("docker-hub.coupang.net")
//			.withRegistryUsername("dockeruser")
//			.withRegistryPassword("ilovedocker")
//			.withRegistryEmail("kangwoo@coupang.com")
			.build();

		// use jaxrs/jersey implementation here (netty impl is also available)
//		DockerCmdExecFactoryImpl dockerCmdExecFactory = new DockerCmdExecFactoryImpl()
//			.withReadTimeout(1000)
//			.withConnectTimeout(1000)
//			.withMaxTotalConnections(100)
//			.withMaxPerRouteConnections(10);
//
//		DockerClient dockerClient = DockerClientBuilder.getInstance(config)
//			.withDockerCmdExecFactory(dockerCmdExecFactory)
//			.build();

		DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

		Info info = dockerClient.infoCmd().exec();
		System.out.println(info);

		String testImage = "docker-hub.coupang.net/mercay/gs-spring-boot-docker";
		List<SearchItem> dockerSearch = dockerClient.searchImagesCmd("mercay").exec();
		System.out.println("Search returned" + dockerSearch.toString());
		dockerClient.pullImageCmd(testImage).exec(new PullImageResultCallback()).awaitSuccess();

		InspectImageResponse inspectImageResponse = dockerClient.inspectImageCmd(testImage).exec();
		System.out.println("Image Inspect: " +  inspectImageResponse.toString());

//		CreateContainerResponse container = dockerClient.createContainerCmd(testImage).withName("test-docker").withCmd("env").exec();
//		System.out.println("Created container " + container.toString());
//
//		final String containerId = container.getId();
		String containerId = "21efaf3998d9";

		List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
		for (Container container : containers) {
			System.out.println(container);
		}

//		dockerClient.startContainerCmd(containerId).exec();

//		dockerClient.stopContainerCmd(containerId).exec();

		dockerClient.close();

	}
}
