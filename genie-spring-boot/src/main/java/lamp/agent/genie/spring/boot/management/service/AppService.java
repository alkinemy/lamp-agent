package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.*;
import lamp.agent.genie.core.app.AppContainer;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.management.repository.AppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Slf4j
@Service
public class AppService {

	@Autowired
	private AppRepository appRepository;

	@Autowired
	private AppContextService appContextService;

	@Autowired
	private AppRegistry appRegistry;

	@PostConstruct
	public void init() {
		{
			List<App> apps = appRepository.findAll();
			for (App app : apps) {
				try {
					AppContext appContext = appContextService.createAppContext(app.getAppContainer());
					if (app instanceof AppImpl) {
						((AppImpl)app).setAppContext(appContext);
					}

					appRegistry.bind(app.getId(), app);
					log.info("[App:{}] registered", app.getId());
				} catch (Exception e) {
					log.warn("[App: " + app.getId() + "] Load failed", e);
				}
			}

		}

		{
			// AUTO START
			List<App> apps = appRegistry.list();
			for (App app : apps) {
				try {
					if (AppStatus.RUNNING.equals(app.getCorrectStatus())
							&& AppStatus.STOPPED.equals(app.getStatus())) {
						log.info("[App:{}] staring", app.getId());
						app.start();
						log.info("[App:{}] started", app.getId());
					}
				} catch (Exception e) {
					log.warn("[App:" + app.getId() + "] Start failed", e);
				}
			}
		}
	}

	@PreDestroy
	public void close() {
	}


	public void saveApp(App app) {
		appRepository.save(app);
	}


	public App loadApp(String id) {
		App app = appRepository.findOne(id);
		Exceptions.throwsException(app == null, ErrorCode.APP_NOT_FOUND, id);
		return app;
	}

	public synchronized List<App> getApps() {
		return appRegistry.list();
	}

	public synchronized App getApp(String id) {
		return appRegistry.lookup(id);
	}

//	public SimpleAppSpec loadApp(String id, SimpleAppSpec defaultValue) {
//		SimpleAppSpec appSpec = appRepository.findOne(id);
//		return appSpec != null ? appSpec : defaultValue;
//	}

	public synchronized App createApp(String id, AppContainer appContainer, AppContext appContext) {
		AppImpl app = new AppImpl();
		app.setId(id);
		app.setAppContainer(appContainer);
		app.setAppContext(appContext);

		appRepository.save(app);
		appRegistry.bind(app.getId(), app);
		return app;
	}

	public synchronized void deleteApp(App app) {
		appRepository.delete(app);
		appRegistry.unbind(app.getId());
	}


	public boolean existsApp(String id) {
		return appRegistry.exists(id);
	}
}
