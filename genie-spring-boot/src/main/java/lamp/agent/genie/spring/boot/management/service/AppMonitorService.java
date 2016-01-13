package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AppMonitorService {

	@Autowired
	private AppManagementService appManagementService;


	public void monitor() {
		List<App> apps = appManagementService.getApps();
		if (CollectionUtils.isEmpty(apps)) {
			return;
		}

		for (App app : apps) {
			log.debug("[App:{}] monitor={}, status={}, correctStatus={}", app.getId(), app.isMonitor(), app.getStatus(), app.getCorrectStatus());
			if (app.isMonitor()
					&& AppStatus.NOT_RUNNING.equals(app.getStatus())
					&& AppStatus.RUNNING.equals(app.getCorrectStatus())) {
				log.warn("[App:{}} Not Running. Trying to start App", app.getId());
				appManagementService.start(app.getId());
			}
		}
	}

}
