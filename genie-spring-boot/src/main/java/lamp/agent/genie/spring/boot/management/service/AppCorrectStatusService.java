package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.AppInstanceStatus;
import lamp.agent.genie.spring.boot.management.repository.AppCorrectStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppCorrectStatusService {

	@Autowired
	private AppCorrectStatusRepository appCorrectStatusRepository;

	public void updateCorrectStatus(String id, AppInstanceStatus correctStatus) {
		appCorrectStatusRepository.save(id, correctStatus);
	}

	public AppInstanceStatus getCorrectStatus(String id) {
		AppInstanceStatus correctStatus = appCorrectStatusRepository.findOne(id);
		return correctStatus != null ? correctStatus : AppInstanceStatus.STOPPED;
	}

}
