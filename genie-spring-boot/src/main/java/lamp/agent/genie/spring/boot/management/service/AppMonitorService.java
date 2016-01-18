package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.spring.boot.management.model.AppMonitor;
import lamp.agent.genie.spring.boot.register.model.AgentEvent;
import lamp.agent.genie.spring.boot.register.model.AgentEventName;
import lamp.agent.genie.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AppMonitorService {

	private static final long ONE_MINUTE = 60 * 1000;

	@Autowired
	private AppManagementService appManagementService;

	@Autowired
	private AgentEventPublishService agentEventPublishService;

	private Map<String, AppMonitor> appMonitorMap = new HashMap<>();

	public synchronized void monitor() {
		List<App> apps = appManagementService.getApps();
		if (CollectionUtils.isEmpty(apps)) {
			return;
		}

		for (App app : apps) {
			log.debug("[App:{}] monitor={}, status={}, correctStatus={}", app.getId(), app.isMonitor(), app.getStatus(), app.getCorrectStatus());
			if (app.isMonitor()
					&& AppStatus.NOT_RUNNING.equals(app.getStatus())
					&& AppStatus.RUNNING.equals(app.getCorrectStatus())) {

				AppMonitor appMonitor = appMonitorMap.get(app.getId());
				if (appMonitor == null) {
					appMonitor = new AppMonitor();
					appMonitorMap.put(app.getId(), appMonitor);
				}
				long currentTimeMillis = System.currentTimeMillis();
				// TODO 로직 개선 필요
				long diffTimeMillis = currentTimeMillis - appMonitor.getLastRetryTimeMillis();
				if (diffTimeMillis > ONE_MINUTE) {
					// 1분이 지났을 경우, 정상적으로 실행되었다고 보고 리셋함.
					appMonitor.setRetryCount(0);
				} else {
					appMonitor.setRetryCount(appMonitor.getRetryCount() + 1);
				}
				appMonitor.setLastRetryTimeMillis(currentTimeMillis);

				log.warn("[App:{}] Not Running. Trying to start App (retry={})", app.getId(), appMonitor.getRetryCount());

				agentEventPublishService.publish(AgentEvent.of(AgentEventName.APP_STARTING_BY_MONITOR, app.getId()));

				appManagementService.start(app.getId());
			}
		}
	}

}
