package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.spring.boot.management.repository.AppCorrectStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppCorrectStatusService {

	@Autowired
	private AppCorrectStatusRepository appCorrectStatusRepository;

	public void updateCorrectStatus(String id, AppStatus correctStatus) {
		appCorrectStatusRepository.save(id, correctStatus);
	}

	public AppStatus getCorrectStatus(String id) {
		AppStatus correctStatus = appCorrectStatusRepository.findOne(id);
		return correctStatus != null ? correctStatus : AppStatus.NOT_RUNNING;
	}

}
