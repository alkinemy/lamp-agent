package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.exception.UnsupportedProcessTypeException;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.impl.AppImpl;
import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.AppRegistry;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.base.impl.DaemonAppContext;
import lamp.agent.genie.spring.boot.base.impl.DefaultAppContext;
import lamp.agent.genie.spring.boot.management.form.AppUpdateForm;
import lamp.agent.genie.spring.boot.management.repository.AppCorrectStatusRepository;
import lombok.extern.slf4j.Slf4j;
import lamp.agent.genie.spring.boot.management.form.AppRegisterForm;

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

	@PostConstruct
	public void init() {
		List<AppConfig> appConfigs = appConfigService.getAppManifests();
		for (AppConfig appConfig : appConfigs) {
			try {
				App app = newAppInstance(appConfig);
				appRegistry.bind(app.getId(), app);
				log.info("[App:{}] registered", app.getId());
			} catch (Exception e) {
				log.warn("[App: " + appConfig.getId() + "] Registration fail error", e);
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

	protected App newAppInstance(AppConfig appConfig) {
		AppContext appContext = newAppContextInstance(appConfig);
		return new AppImpl(appContext, appCorrectStatusService.getCorrectStatus(appConfig.getId()));
	}

	protected AppContext newAppContextInstance(AppConfig appConfig, InstallConfig installConfig) {
		AppProcessType appProcessType = appConfig.getProcessType();
		if (AppProcessType.DAEMON.equals(appProcessType)) {
			return new DaemonAppContext(lampContext, appConfig, installConfig);
		} else if (AppProcessType.DEFAULT.equals(appProcessType)) {
			return new DefaultAppContext(lampContext, appConfig, installConfig);
		} else {
			throw new UnsupportedProcessTypeException(appProcessType);
		}
	}

	protected AppContext newAppContextInstance(AppConfig appConfig) {
		InstallConfig installConfig = null;
		if (!appConfig.isPreInstalled()) {
			installConfig = installConfigService.getInstallConfig(appConfig.getId());
		}
		return newAppContextInstance(appConfig, installConfig);
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

		AppConfig appConfig = smartAssembler.assemble(form, AppConfig.class);
		if (!appConfig.isPreInstalled()) {
			InstallConfig installConfig = smartAssembler.assemble(form, InstallConfig.class);
			AppContext appContext = newAppContextInstance(appConfig, installConfig);
			appInstallService.install(appContext, form.getInstallFile());
		}

		App app = newAppInstance(appConfig);
		appRegistry.bind(app.getId(), app);

		appConfigService.save(appConfig);
	}

	public synchronized void update(AppUpdateForm form) {
		String id = form.getId();
		App app = appRegistry.lookup(id);
		AppConfig appConfig = app.getManifest();

		if (form.getInstallFile() != null && !form.getInstallFile().isEmpty()) {
			Exceptions.throwsException(app.isRunning(), ErrorCode.APP_IS_RUNNING);

			appInstallService.uninstall(app.getContext());

			InstallConfig installConfig = smartAssembler.assemble(form, InstallConfig.class);
			AppContext newAppContext = newAppContextInstance(appConfig, installConfig);
			appInstallService.install(newAppContext, form.getInstallFile());
		}

		BeanUtils.copyProperties(form, appConfig, "id");

		appConfigService.save(appConfig);
	}

	public synchronized void deregister(String id, boolean forceStop) {
		App app = appRegistry.lookup(id);
		AppConfig appConfig = app.getManifest();
		if (!appConfig.isPreInstalled()) {
			InstallConfig installConfig = installConfigService.getInstallConfig(id);
			Exceptions.throwsException(app.isRunning() && !forceStop, ErrorCode.APP_IS_RUNNING, id);
			if (app.isRunning() && forceStop) {
				app.stop();
			}
			appInstallService.uninstall(newAppContextInstance(appConfig, installConfig));
		}

		appRegistry.unbind(app.getId());

		appConfigService.delete(appConfig);
	}

	public synchronized void start(String id) {
		App app = appRegistry.lookup(id);
		app.start();

		appCorrectStatusService.updateCorrectStatus(app.getId(), AppStatus.RUNNING);
	}

	public synchronized void stop(String id) {
		appCorrectStatusService.updateCorrectStatus(id, AppStatus.NOT_RUNNING);

		App app = appRegistry.lookup(id);
		app.stop();
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
