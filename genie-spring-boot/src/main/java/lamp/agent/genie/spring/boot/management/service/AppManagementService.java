package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.*;
import lamp.agent.genie.core.exception.UnsupportedProcessTypeException;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.impl.AppImpl;
import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.base.impl.DaemonAppContext;
import lamp.agent.genie.spring.boot.base.impl.DefaultAppContext;
import lamp.agent.genie.spring.boot.management.model.AppUpdateForm;
import lamp.agent.genie.spring.boot.register.model.AgentEvent;
import lamp.agent.genie.spring.boot.register.model.AgentEventName;
import lombok.extern.slf4j.Slf4j;
import lamp.agent.genie.spring.boot.management.model.AppRegisterForm;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.List;

@Slf4j
@Service
public class AppManagementService {

	@Autowired
	private LampContext lampContext;

	@Autowired
	private SmartAssembler smartAssembler;

	@Autowired
	private AppConfigService appConfigService;

	@Autowired
	private InstallConfigService installConfigService;

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
		List<AppSpec> appSpecs = appConfigService.getAppManifests();
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
			installSpec = installConfigService.getInstallConfig(appSpec.getId());
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

		appConfigService.save(appSpec);

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_REGISTERED, id));
	}

	public synchronized void update(AppUpdateForm form) {
		// FIXME 제대로 구현해야함
		String id = form.getId();
		App app = appRegistry.lookup(id);
		AppSpec appSpec = app.getConfig();

		if (form.getInstallFile() != null && !form.getInstallFile().isEmpty()) {
			Exceptions.throwsException(app.isRunning(), ErrorCode.APP_IS_RUNNING);

			appInstallService.uninstall(app.getContext());
			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_UNINSTALLED, id));

			InstallSpec installSpec = smartAssembler.assemble(form, InstallSpec.class);
			AppContext newAppContext = newAppContextInstance(appSpec, installSpec);
			appInstallService.install(newAppContext, form.getInstallFile());
			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_INSTALLED, id));
		}

		BeanUtils.copyProperties(form, appSpec, "id");

		appConfigService.save(appSpec);
	}

	public synchronized void deregister(String id, boolean forceStop) {
		App app = appRegistry.lookup(id);
		AppSpec appSpec = app.getConfig();
		if (!appSpec.isPreInstalled()) {
			InstallSpec installSpec = installConfigService.getInstallConfig(id);
			Exceptions.throwsException(app.isRunning() && !forceStop, ErrorCode.APP_IS_RUNNING, id);
			if (app.isRunning() && forceStop) {
				app.stop();
			}
			appInstallService.uninstall(newAppContextInstance(appSpec, installSpec));
			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_UNINSTALLED, id));
		}

		appRegistry.unbind(app.getId());

		appConfigService.delete(appSpec);
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

	public Resource getLogFileResource(String id) {
		App app = appRegistry.lookup(id);
		File logFile = app.getLogFile();
		return logFile != null ? new FileSystemResource(logFile) : null;
	}

	public Resource getSystemLogFileResource(String id) {
		App app = appRegistry.lookup(id);
		File logFile = app.getContext().getSystemLogFile();
		return logFile != null ? new FileSystemResource(logFile) : null;
	}

	//
//	public File getSystemLogFile(String agentId) {
//		App base = appRegistry.lookup(agentId);
//		File file = base.getContext().getSystemLogFile();
//		Exceptions.throwsException(file == null || !file.canRead(), ErrorCode.AGENT_SYSTEM_LOG_FILE_NOT_FOUND);
//		return file;
//	}

}
