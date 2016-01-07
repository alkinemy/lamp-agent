package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.exception.UnsupportedProcessTypeException;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.impl.AppImpl;
import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.context.AppContext;
import lamp.agent.genie.core.context.AppRegistry;
import lamp.agent.genie.core.context.LampContext;
import lamp.agent.genie.core.deploy.InstallManifest;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.base.impl.DaemonAppContext;
import lamp.agent.genie.spring.boot.base.impl.DefaultAppContext;
import lamp.agent.genie.spring.boot.management.form.AppUpdateForm;
import lombok.extern.slf4j.Slf4j;
import lamp.agent.genie.spring.boot.management.form.AppRegisterForm;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Slf4j
@Service
public class AppManagementService {

	@Autowired
	private LampContext lampContext;

	@Autowired
	private SmartAssembler smartAssembler;

	@Autowired
	private AppManifestService appManifestService;

	@Autowired
	private AppInstallService appInstallService;

	@Autowired
	private AppRegistry appRegistry;

	@PostConstruct
	public void init() {
		List<AppManifest> appManifests = appManifestService.getAppManifests();
		for (AppManifest appManifest : appManifests) {
			try {
				App app = newAppInstance(appManifest);
				appRegistry.bind(app.getId(), app);
				log.info("[App] '{}' registered", app.getId());
			} catch (Exception e) {
				log.warn("[App] " + appManifest.getId() + " Registration fail error", e);
			}
		}

		List<App> apps = appRegistry.list();
		for (App app : apps) {
			try {
				if (app.getManifest().isAutoStart()
						&& AppStatus.NOT_RUNNING.equals(app.getStatus())) {
					log.info("[App] '{}' staring", app.getId());
					app.start();
					log.info("[App] '{}' started", app.getId());
				}
			} catch (Exception e) {
				log.warn("[App] " + app.getId() + " Start failed", e);
			}
		}

	}

	protected App newAppInstance(AppManifest appManifest) {
		AppContext appContext = newAppContextInstance(appManifest);
		return new AppImpl(appContext);
	}

	protected AppContext newAppContextInstance(AppManifest appManifest) {
		AppProcessType appProcessType = appManifest.getProcessType();
		if (AppProcessType.DAEMON.equals(appProcessType)) {
			return new DaemonAppContext(lampContext, appManifest);
		} else if (AppProcessType.DEFAULT.equals(appProcessType)) {
			return new DefaultAppContext(lampContext, appManifest);
		} else {
			throw new UnsupportedProcessTypeException(appProcessType);
		}
	}

	@PreDestroy
	public void close() {
		List<App> apps = appRegistry.list();
		for (App app : apps) {
			// TODO STARTING 처리 필요
			if (app.getManifest().isAutoStop()
					&& app.isRunning()) {
				try {
					app.stop();
				} catch(Exception e) {
					log.warn("App closing error", e);
				}
			}
		}
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

		AppManifest appManifest = smartAssembler.assemble(form, AppManifest.class);
		if (!appManifest.isPreInstalled()) {
			InstallManifest installManifest = smartAssembler.assemble(form, InstallManifest.class);
			appInstallService.install(installManifest, appManifest, form.getInstallFile());
		}

		App app = newAppInstance(appManifest);
		appRegistry.bind(app.getId(), app);

		appManifestService.save(appManifest);
	}

	public synchronized void update(AppUpdateForm form) {
		String id = form.getId();
		App app = appRegistry.lookup(id);
		AppManifest appManifest = app.getManifest();

		if (form.getInstallFile() != null && !form.getInstallFile().isEmpty()) {
			Exceptions.throwsException(app.isRunning(), ErrorCode.APP_IS_RUNNING);

			appInstallService.uninstall(appManifest);

			InstallManifest installManifest = smartAssembler.assemble(form, InstallManifest.class);
			appInstallService.install(installManifest, appManifest, form.getInstallFile());
		}

		BeanUtils.copyProperties(form, appManifest, "id");

		appManifestService.save(appManifest);
	}

	public synchronized void deregister(String id, boolean forceStop) {
		App app = appRegistry.lookup(id);
		AppManifest appManifest = app.getManifest();
		if (!appManifest.isPreInstalled()) {
			Exceptions.throwsException(app.isRunning() && !forceStop, ErrorCode.APP_IS_RUNNING, id);
			if (app.isRunning() && forceStop) {
				app.stop();
			}
			appInstallService.uninstall(appManifest);
		}

		appRegistry.unbind(app.getId());

		appManifestService.delete(appManifest);
	}

	public synchronized void start(String id) {
		App app = appRegistry.lookup(id);
		app.start();

//		AppConfig appConfig = base.getConfig();
//		appConfig.setStatus(AppStatus.RUNNING);
//		agentDefinitionService.save(appConfig);
	}

	public synchronized void stop(String id) {
		App app = appRegistry.lookup(id);
		app.stop();

//		AppConfig appConfig = base.getConfig();
//		appConfig.setStatus(AppStatus.NOT_RUNNING);
//		agentDefinitionService.save(appConfig);
	}

	public synchronized AppStatus status(String id) {
		App app = appRegistry.lookup(id);
		return app.getStatus();
	}

	//
//	public File getSystemLogFile(String agentId) {
//		App base = appRegistry.lookup(agentId);
//		File file = base.getContext().getSystemLogFile();
//		Exceptions.throwsException(file == null || !file.canRead(), ErrorCode.AGENT_SYSTEM_LOG_FILE_NOT_FOUND);
//		return file;
//	}

}
