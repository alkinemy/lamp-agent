package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.*;
import lamp.agent.genie.core.exception.UnsupportedProcessTypeException;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.base.impl.AppImpl;
import lamp.agent.genie.spring.boot.base.impl.AppSpecImpl;
import lamp.agent.genie.spring.boot.base.impl.DaemonAppContext;
import lamp.agent.genie.spring.boot.base.impl.DefaultAppContext;
import lamp.agent.genie.spring.boot.management.model.*;
import lamp.agent.genie.spring.boot.register.model.AgentEvent;
import lamp.agent.genie.spring.boot.register.model.AgentEventName;
import lamp.agent.genie.utils.FilenameUtils;
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
		List<AppSpec> appSpecs = appSpecService.getAppManifests();
		for (AppSpec appSpec : appSpecs) {
			try {
				App app = newAppInstance(appSpec);
				appRegistry.bind(app.getId(), app);
				log.info("[App:{}] registered", app.getId());
			} catch (Exception e) {
				log.warn("[App: " + appSpec.getId() + "] Registration fail error", e);
			}
		}

		// AUTO START
		List<App> apps = appRegistry.list();
		for (App app : apps) {
			try {
				if (AppStatus.RUNNING.equals(app.getCorrectStatus())
					&& AppStatus.NOT_RUNNING.equals(app.getStatus())) {
					log.info("[App:{}] staring", app.getId());
					app.start();
					log.info("[App:{}] started", app.getId());
				}
			} catch (Exception e) {
				log.warn("[App:" + app.getId() + "] Start failed", e);
			}
		}

	}

	protected App newAppInstance(AppSpec appSpec) {
		AppContext appContext = newAppContextInstance(appSpec);
		return new AppImpl(appContext, appCorrectStatusService.getCorrectStatus(appSpec.getId()));
	}

	protected AppContext newAppContextInstance(AppSpec appSpec, InstallSpec installSpec) {
		AppProcessType appProcessType = appSpec.getProcessType();
		if (AppProcessType.DAEMON.equals(appProcessType)) {
			return new DaemonAppContext(lampContext, appSpec, installSpec);
		} else if (AppProcessType.DEFAULT.equals(appProcessType)) {
			return new DefaultAppContext(lampContext, appSpec, installSpec);
		} else {
			throw new UnsupportedProcessTypeException(appProcessType);
		}
	}

	protected AppContext newAppContextInstance(AppSpec appSpec) {
		InstallSpec installSpec = null;
		if (!appSpec.isPreInstalled()) {
			installSpec = installSpecService.getInstallConfig(appSpec.getId());
		}
		return newAppContextInstance(appSpec, installSpec);
	}

	@PreDestroy
	public void close() {
	}

	public synchronized List<App> getApps() {
		return appRegistry.list();
	}

	public synchronized App getApp(String id) {
		return appRegistry.lookup(id);
	}

	public synchronized void register(AppRegisterForm form) {
		String id = form.getId();
		Exceptions.throwsException(appRegistry.exists(id), ErrorCode.APP_ALWAYS_EXIST);

		AppSpec appSpec = smartAssembler.assemble(form, AppSpec.class);
		if (!appSpec.isPreInstalled()) {
			InstallSpec installSpec = smartAssembler.assemble(form, InstallSpec.class);
			AppContext appContext = newAppContextInstance(appSpec, installSpec);
			appInstallService.install(appContext, form.getInstallFile());

			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_INSTALLED, id));
		}

		App app = newAppInstance(appSpec);
		appRegistry.bind(app.getId(), app);

		appSpecService.save(appSpec);

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_REGISTERED, id));
	}

	public synchronized void update(String id, AppUpdateForm form) {
		form.setId(id);

		deregister(id, form.isForceStop());

		register(form);
	}

	public synchronized void updateFile(String id, AppFileUpdateForm form) {
		App app = appRegistry.lookup(id);

		boolean isRunning = app.isRunning();
		Exceptions.throwsException(isRunning && !form.isForceStop(), ErrorCode.APP_IS_RUNNING);

		if (isRunning) {
			app.stop();
			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STOPPED, id));
		}

		AppContext appContext = app.getContext();


		appInstallService.update(appContext, form.getInstallFile());
		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_FILE_UPDATED, id));

		AppSpecImpl appSpec = (AppSpecImpl) app.getSpec();
		appSpec.setVersion(form.getVersion());
		appSpecService.save(appSpec);

		if (isRunning) {
			app.start();
		}
	}

	public synchronized void updateSpec(String id, AppUpdateSpecForm form) {
		App app = appRegistry.lookup(id);
		AppSpec appSpec = app.getSpec();

		BeanUtils.copyProperties(form, appSpec);

		appSpecService.save(appSpec);
	}

	public synchronized void deregister(String id, boolean forceStop) {
		App app = appRegistry.lookup(id);
		AppSpec appSpec = app.getSpec();
		AppProcessType processType = appSpec.getProcessType();

		Exceptions.throwsException(AppProcessType.DEFAULT.equals(processType) && app.isRunning() && !forceStop, ErrorCode.APP_IS_RUNNING, id);

		if (app.isRunning() && forceStop) {
			stop(id);
		}

		if (!appSpec.isPreInstalled()) {
			Exceptions.throwsException(app.isRunning(), ErrorCode.APP_IS_RUNNING, id);

			InstallSpec installSpec = installSpecService.getInstallConfig(id);
			appInstallService.uninstall(newAppContextInstance(appSpec, installSpec));
			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_UNINSTALLED, id));
		}

		appRegistry.unbind(app.getId());

		appSpecService.delete(appSpec);
		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_UNREGISTERED, id));
	}

	public synchronized void start(String id) {
		App app = appRegistry.lookup(id);
		app.start();

		appCorrectStatusService.updateCorrectStatus(app.getId(), AppStatus.RUNNING);

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STARTED, id));
	}

	public synchronized void stop(String id) {
		appCorrectStatusService.updateCorrectStatus(id, AppStatus.NOT_RUNNING);

		App app = appRegistry.lookup(id);
		app.stop();

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STOPPED, id));
	}

	public synchronized AppStatus status(String id) {
		App app = appRegistry.lookup(id);
		return app.getStatus();
	}

	public List<LogFile> getLogFiles(String id) {
		App app = appRegistry.lookup(id);
		String logDirectory = app.getContext().getParsedAppSpec().getLogDirectory();
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
		App app = appRegistry.lookup(id);
		String logDirectory = app.getContext().getParsedAppSpec().getLogDirectory();
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
		App app = appRegistry.lookup(id);
		File file = app.getStdOutFile();
		if (file.exists()) {
			return new FileSystemResource(file);
		}
		return null;
	}

	public Resource getStdErrFileResource(String id) {
		App app = appRegistry.lookup(id);
		File file = app.getContext().getStdErrFile();
		if (file.exists()) {
			return new FileSystemResource(file);
		}
		return null;
	}




}
