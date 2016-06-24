package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.app.AppContainer;
import lamp.agent.genie.core.app.docker.DockerAppContainer;
import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lamp.agent.genie.core.app.simple.SimpleAppContext;
import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.management.model.AppDeployForm;
import lamp.agent.genie.spring.boot.management.model.AppRedeployForm;
import lamp.agent.genie.spring.boot.register.model.AgentEvent;
import lamp.agent.genie.spring.boot.register.model.AgentEventName;
import lamp.agent.genie.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AppManagementService {

	@Autowired
	private LampContext lampContext;

	@Autowired
	private AppService appService;

	@Autowired
	private AppContextService appContextService;

	@Autowired
	private SmartAssembler smartAssembler;

	@Autowired
	private SimpleAppInstallService appInstallService;

	@Autowired
	private AppCorrectStatusService appCorrectStatusService;

	@Autowired
	private AgentEventPublishService agentEventPublishService;


	public synchronized List<App> getApps() {
		return appService.getApps();
	}

	public synchronized App getApp(String id) {
		return appService.getApp(id);
	}

	public synchronized void deploy(AppDeployForm form) {
		String id = form.getId();
		AppContainer appContainer = JsonUtils.parse(form.getAppContainer(), AppContainer.class);
		if (appContainer instanceof SimpleAppContainer) {
			((SimpleAppContainer) appContainer).setId(id);
		} else if (appContainer instanceof DockerAppContainer) {
			((DockerAppContainer) appContainer).setId(id);
		}

		Exceptions.throwsException(appService.existsApp(appContainer.getId()), ErrorCode.APP_ALWAYS_EXIST);


		AppContext appContext = appContextService.createAppContext(appContainer);

		if (appContainer instanceof SimpleAppContainer) {
			SimpleAppContainer simpleAppContainer = (SimpleAppContainer) appContainer;
			if (!simpleAppContainer.isPreInstalled()) {

				appInstallService.install((SimpleAppContext) appContext, form.getResource());

				agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_INSTALLED, id));
			}
		} else if (appContainer instanceof DockerAppContainer) {

		}

		appService.createApp(id, appContainer, appContext);

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_REGISTERED, id));

	}

	public synchronized void redeploy(String id, AppRedeployForm form) {
		AppContainer appContainer = JsonUtils.parse(form.getAppContainer(), AppContainer.class);
		if (appContainer instanceof SimpleAppContainer) {
			((SimpleAppContainer) appContainer).setId(id);
		} else if (appContainer instanceof DockerAppContainer) {
			((DockerAppContainer) appContainer).setId(id);
		}

		App app = appService.getApp(id);
		if (app.isRunning()) {
			stop(id);
		}

		AppContext appContext = appContextService.createAppContext(appContainer);

		if (appContainer instanceof SimpleAppContainer) {
			SimpleAppContainer simpleAppContainer = (SimpleAppContainer) appContainer;
			if (!simpleAppContainer.isPreInstalled()) {

				appInstallService.reinstall((SimpleAppContext) appContext, form.getResource());

				agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_INSTALLED, id));
			}
		}

		appService.createApp(id, appContainer, appContext);

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_REGISTERED, id));

	}


//	public synchronized void update(String id, AppUpdateForm form) {
//		form.setId(id);
//
//		undeploy(id, form.isForceStop());
//
//		deploy(form);
//	}

//	public synchronized void updateFile(String id, AppFileUpdateForm form) {
//		SimpleApp appInstance = appService.getApp(id);
//
//		boolean isRunning = appInstance.isRunning();
//		Exceptions.throwsException(isRunning && !form.isForceStop(), ErrorCode.APP_IS_RUNNING);
//
//		if (isRunning) {
//			appInstance.stop();
//			agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STOPPED, id));
//		}
//
//		SimpleAppContext appInstanceContext = appInstance.getAppContext();
//
//		appInstallService.update(appInstanceContext, form.getInstallFile());
//		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_FILE_UPDATED, id));
//
//		AppSpecImpl appSpec = (AppSpecImpl) appInstance.getSpec();
//		appSpec.setVersion(form.getVersion());
//		appSpecService.saveApp(appSpec);
//
//		if (isRunning) {
//			appInstance.start();
//		}
//	}
//
//	public synchronized void updateSpec(String id, AppUpdateSpecForm form) {
//		SimpleApp appInstance = appService.getApp(id);
//		SimpleAppSpec appSpec = appInstance.getSpec();
//
//		BeanUtils.copyProperties(form, appSpec);
//
//		appSpecService.saveApp(appSpec);
//	}

	public synchronized void undeploy(String id, boolean forceStop) {
		App app = appService.getApp(id);

		Exceptions.throwsException(app.isRunning() && !forceStop, ErrorCode.APP_IS_RUNNING, id);

		if (app.isRunning() && forceStop) {
			stop(id);
		}

		AppContainer appContainer = app.getAppContainer();
		if (appContainer instanceof SimpleAppContainer) {
			if (!((SimpleAppContainer) appContainer).isPreInstalled()) {

				appInstallService.uninstall((SimpleAppContext) app.getAppContext());
				agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_UNINSTALLED, id));
			}
		}

		appService.deleteApp(app);

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_UNREGISTERED, id));
	}

	public synchronized void start(String id) {
		App app = appService.getApp(id);
		app.start();

		appCorrectStatusService.updateCorrectStatus(app.getId(), AppStatus.RUNNING);

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STARTED, id));
	}

	public synchronized void stop(String id) {
		appCorrectStatusService.updateCorrectStatus(id, AppStatus.STOPPED);

		App app = appService.getApp(id);
		app.stop();

		agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STOPPED, id));
	}

	public synchronized AppStatus status(String id) {
		App app = appService.getApp(id);
		return app.getStatus();
	}



}
