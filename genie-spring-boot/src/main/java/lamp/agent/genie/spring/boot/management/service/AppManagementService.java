package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.*;
import lamp.agent.genie.core.exception.UnsupportedProcessTypeException;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.base.impl.AppInstanceImpl;
import lamp.agent.genie.spring.boot.base.impl.AppInstanceSpecImpl;
import lamp.agent.genie.spring.boot.base.impl.DaemonAppInstanceContext;
import lamp.agent.genie.spring.boot.base.impl.DefaultAppInstanceContext;
import lamp.agent.genie.spring.boot.management.model.*;
import lamp.agent.genie.spring.boot.register.model.AgentEvent;
import lamp.agent.genie.spring.boot.register.model.AgentEventName;
import lamp.agent.genie.utils.FilenameUtils;
import lamp.agent.genie.utils.JsonUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AppManagementService {

	@Autowired
	private LampContext lampContext;

	@Autowired
	private SmartAssembler smartAssembler;

	@Autowired
	private AppSpecService appSpecService;

	@Autowired
	private InstallSpecService installSpecService;

	@Autowired
	private AppInstallService appInstallService;

	@Autowired
	private AppRegistry appRegistry;

	@Autowired
	private AppCorrectStatusService appCorrectStatusService;

	@Autowired
	private AgentEventPublishService agentEventPublishService;

	@PostConstruct
	public void init() {
		List<AppInstanceSpec> appInstanceSpecs = appSpecService.getAppManifests();
		for (AppInstanceSpec appInstanceSpec : appInstanceSpecs) {
			try {
				AppInstance appInstance = newAppInstance(appInstanceSpec);
				appRegistry.bind(appInstance.getId(), appInstance);
				log.info("[App:{}] registered", appInstance.getId());
			} catch (Exception e) {
				log.warn("[App: " + appInstanceSpec.getId() + "] Registration fail error", e);
			}
		}

		// AUTO START
		List<AppInstance> appInstances = appRegistry.list();
		for (AppInstance appInstance : appInstances) {
			try {
				if (AppInstanceStatus.RUNNING.equals(appInstance.getCorrectStatus())
					&& AppInstanceStatus.STOPPED.equals(appInstance.getStatus())) {
					log.info("[App:{}] staring", appInstance.getId());
					appInstance.start();
					log.info("[App:{}] started", appInstance.getId());
				}
			} catch (Exception e) {
				log.warn("[App:" + appInstance.getId() + "] Start failed", e);
			}
		}

	}

	protected AppInstance newAppInstance(AppInstanceSpec appInstanceSpec) {
		AppInstanceContext appInstanceContext = newAppContextInstance(appInstanceSpec);
		return new AppInstanceImpl(appInstanceContext, appCorrectStatusService.getCorrectStatus(appInstanceSpec.getId()));
	}

	protected AppInstanceContext newAppContextInstance(AppInstanceSpec appInstanceSpec, InstallSpec installSpec) {
		AppProcessType appProcessType = appInstanceSpec.getProcessType();
		if (AppProcessType.DAEMON.equals(appProcessType)) {
			return new DaemonAppInstanceContext(lampContext, appInstanceSpec, installSpec);
		} else if (AppProcessType.DEFAULT.equals(appProcessType)) {
			return new DefaultAppInstanceContext(lampContext, appInstanceSpec, installSpec);
		} else {
			throw new UnsupportedProcessTypeException(appProcessType);
		}
	}

	protected AppInstanceContext newAppContextInstance(AppInstanceSpec appInstanceSpec) {
		InstallSpec installSpec = null;
		if (!appInstanceSpec.isPreInstalled()) {
			installSpec = installSpecService.getInstallConfig(appInstanceSpec.getId());
		}
		return newAppContextInstance(appInstanceSpec, installSpec);
	}

	@PreDestroy
	public void close() {
	}

	public synchronized List<AppInstance> getApps() {
		return appRegistry.list();
	}

	public synchronized AppInstance getApp(String id) {
		return appRegistry.lookup(id);
	}

	public synchronized void register(AppDeployForm form) {
		String id = form.getId();
		Exceptions.throwsException(appRegistry.exists(id), ErrorCode.APP_ALWAYS_EXIST);

		AppContainer appContainer = JsonUtils.parse(form.getAppContainer(), AppContainer.class);

		AppInstanceSpec appInstanceSpec = smartAssembler.assemble(appContainer, AppInstanceSpec.class);
		if (appContainer instanceof SimpleAppContainer
			&& !appInstanceSpec.isPreInstalled()) {
			InstallSpec installSpec = newInstallSpec((SimpleAppContainer) appContainer);
			AppInstanceContext appInstanceContext = newAppContextInstance(appInstanceSpec, installSpec);
			appInstallService.install(appInstanceContext, form.getResource());

			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_INSTALLED, id));
		}

		AppInstance appInstance = newAppInstance(appInstanceSpec);
		appRegistry.bind(appInstance.getId(), appInstance);

		appSpecService.save(appInstanceSpec);

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_REGISTERED, id));
	}

	protected InstallSpec newInstallSpec(SimpleAppContainer appContainer) {
		InstallSpec installSpec = new InstallSpec();
		installSpec.setId(appContainer.getId());
		installSpec.setDirectory(null);
		installSpec.setFilename(appContainer.getInstallFilename());
		installSpec.setScriptCommands(appContainer.getScriptCommands());

		return installSpec;
	}

//	public synchronized void update(String id, AppUpdateForm form) {
//		form.setId(id);
//
//		deregister(id, form.isForceStop());
//
//		register(form);
//	}

	public synchronized void updateFile(String id, AppFileUpdateForm form) {
		AppInstance appInstance = appRegistry.lookup(id);

		boolean isRunning = appInstance.isRunning();
		Exceptions.throwsException(isRunning && !form.isForceStop(), ErrorCode.APP_IS_RUNNING);

		if (isRunning) {
			appInstance.stop();
			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STOPPED, id));
		}

		AppInstanceContext appInstanceContext = appInstance.getContext();

		appInstallService.update(appInstanceContext, form.getInstallFile());
		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_FILE_UPDATED, id));

		AppInstanceSpecImpl appSpec = (AppInstanceSpecImpl) appInstance.getSpec();
		appSpec.setVersion(form.getVersion());
		appSpecService.save(appSpec);

		if (isRunning) {
			appInstance.start();
		}
	}

	public synchronized void updateSpec(String id, AppUpdateSpecForm form) {
		AppInstance appInstance = appRegistry.lookup(id);
		AppInstanceSpec appInstanceSpec = appInstance.getSpec();

		BeanUtils.copyProperties(form, appInstanceSpec);

		appSpecService.save(appInstanceSpec);
	}

	public synchronized void deregister(String id, boolean forceStop) {
		AppInstance appInstance = appRegistry.lookup(id);
		AppInstanceSpec appInstanceSpec = appInstance.getSpec();
		AppProcessType processType = appInstanceSpec.getProcessType();

		Exceptions.throwsException(AppProcessType.DEFAULT.equals(processType) && appInstance.isRunning() && !forceStop, ErrorCode.APP_IS_RUNNING, id);

		if (appInstance.isRunning() && forceStop) {
			stop(id);
		}

		if (!appInstanceSpec.isPreInstalled()) {
			Exceptions.throwsException(appInstance.isRunning(), ErrorCode.APP_IS_RUNNING, id);

			InstallSpec installSpec = installSpecService.getInstallConfig(id);
			appInstallService.uninstall(newAppContextInstance(appInstanceSpec, installSpec));
			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_UNINSTALLED, id));
		}

		appRegistry.unbind(appInstance.getId());

		appSpecService.deleteAppMetaInfoDirectory(appInstanceSpec);
		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_UNREGISTERED, id));
	}

	public synchronized void start(String id) {
		AppInstance appInstance = appRegistry.lookup(id);
		appInstance.start();

		appCorrectStatusService.updateCorrectStatus(appInstance.getId(), AppInstanceStatus.RUNNING);

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STARTED, id));
	}

	public synchronized void stop(String id) {
		appCorrectStatusService.updateCorrectStatus(id, AppInstanceStatus.STOPPED);

		AppInstance appInstance = appRegistry.lookup(id);
		appInstance.stop();

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STOPPED, id));
	}

	public synchronized AppInstanceStatus status(String id) {
		AppInstance appInstance = appRegistry.lookup(id);
		return appInstance.getStatus();
	}

	public List<LogFile> getLogFiles(String id) {
		AppInstance appInstance = appRegistry.lookup(id);
		String logDirectory = appInstance.getContext().getParsedAppInstanceSpec().getLogDirectory();
		if (StringUtils.isNotBlank(logDirectory)) {
			List<LogFile> logFiles = new ArrayList<>();
			File dir = new File(logDirectory);
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					LogFile logFile = new LogFile();
					logFile.setName(file.getName());
					logFile.setSize(file.length());
					logFile.setLastModified(new Date(file.lastModified()));

					logFiles.add(logFile);
				}
			}
			return logFiles;
		}
		return Collections.emptyList();
	}

	public Resource getLogFileResource(String id, String filename) {
		AppInstance appInstance = appRegistry.lookup(id);
		String logDirectory = appInstance.getContext().getParsedAppInstanceSpec().getLogDirectory();
		if (StringUtils.isNotBlank(logDirectory)) {
			filename = FilenameUtils.getName(filename);
			File file = new File(logDirectory, filename);
			if (file.exists()) {
				return new FileSystemResource(file);
			}
		}
		return null;
	}

	public Resource getStdOutFileResource(String id) {
		AppInstance appInstance = appRegistry.lookup(id);
		File file = appInstance.getStdOutFile();
		if (file.exists()) {
			return new FileSystemResource(file);
		}
		return null;
	}

	public Resource getStdErrFileResource(String id) {
		AppInstance appInstance = appRegistry.lookup(id);
		File file = appInstance.getContext().getStdErrFile();
		if (file.exists()) {
			return new FileSystemResource(file);
		}
		return null;
	}




}
