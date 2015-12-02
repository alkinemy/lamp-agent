package lamp.client.genie.spring.boot.management.service;

import lamp.client.genie.core.App;
import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.AppStatus;
import lamp.client.genie.core.context.AppContext;
import lamp.client.genie.core.context.AppRegistry;
import lamp.client.genie.core.context.LampContext;
import lamp.client.genie.core.deploy.DeployManifest;
import lamp.client.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.client.genie.spring.boot.base.exception.ErrorCode;
import lamp.client.genie.spring.boot.base.exception.Exceptions;
import lamp.client.genie.spring.boot.base.impl.AppContextImpl;
import lamp.client.genie.spring.boot.base.impl.AppImpl;
import lombok.extern.slf4j.Slf4j;
import lamp.client.genie.spring.boot.management.controller.AppRegistrationForm;

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
	private AppDeployService appDeployService;

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
				if (app.getManifest().getAutoStart()
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
		AppContext appContext = new AppContextImpl(lampContext, appManifest);
		return new AppImpl(appContext);
	}

	@PreDestroy
	public void close() {
		List<App> apps = appRegistry.list();
		for (App app : apps) {
			// TODO STARTING 처리 필요
			if (app.getManifest().getAutoStop()
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

	public synchronized void register(AppRegistrationForm form) {
		AppManifest appManifest = smartAssembler.assemble(form, AppManifest.class);
		if (appManifest.getDeploy()) {
			DeployManifest deployManifest = smartAssembler.assemble(form, DeployManifest.class);

			boolean exists = appRegistry.exists(appManifest.getId());
			if (!deployManifest.isOverwrite()) {
				Exceptions.throwsException(exists, ErrorCode.APP_IS_ALREADY_DEPLOYED, appManifest.getId());
			}

			if (exists) {
				App app = appRegistry.lookup(appManifest.getId());
				if (app.isRunning()) {
					app.stop();
				}
			}

			appDeployService.deploy(deployManifest, appManifest, form.getDeployFile());
		}

		App app = newAppInstance(appManifest);
		appRegistry.bind(app.getId(), app);

		appManifestService.save(appManifest);
	}

	public synchronized void deregister(String id, boolean forceStop) {
		App app = appRegistry.lookup(id);
		AppManifest appManifest = app.getManifest();
		if (appManifest.getDeploy()) {
			Exceptions.throwsException(app.isRunning() && !forceStop, ErrorCode.APP_IS_RUNNING, id);
			if (app.isRunning() && forceStop) {
				app.stop();
			}
			appDeployService.undeploy(app.getManifest());
		}

		appRegistry.unbind(app.getId());

		appManifestService.delete(appManifest);
	}

	public synchronized void start(String agentId) {
		App app = appRegistry.lookup(agentId);
		app.start();

//		AppConfig appConfig = base.getConfig();
//		appConfig.setStatus(AppStatus.RUNNING);
//		agentDefinitionService.save(appConfig);
	}

	public synchronized void stop(String agentId) {
		App app = appRegistry.lookup(agentId);
		app.stop();

//		AppConfig appConfig = base.getConfig();
//		appConfig.setStatus(AppStatus.NOT_RUNNING);
//		agentDefinitionService.save(appConfig);
	}

//
//	public File getSystemLogFile(String agentId) {
//		App base = appRegistry.lookup(agentId);
//		File file = base.getContext().getSystemLogFile();
//		Exceptions.throwsException(file == null || !file.canRead(), ErrorCode.AGENT_SYSTEM_LOG_FILE_NOT_FOUND);
//		return file;
//	}

}
