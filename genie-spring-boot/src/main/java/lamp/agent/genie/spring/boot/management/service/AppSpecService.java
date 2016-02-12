package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.management.repository.AppSpecRepository;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AppSpecService {

	@Autowired
	private AppSpecRepository appSpecRepository;

	public void save(AppSpec appSpec) {
		appSpecRepository.save(appSpec);
	}

	public List<AppSpec> getAppManifests() {
		return appSpecRepository.findAll();
	}

	public AppSpec getAppManifest(String id) {
		AppSpec appSpec = appSpecRepository.findOne(id);
		Exceptions.throwsException(appSpec == null, ErrorCode.APP_NOT_FOUND, id);
		return appSpec;
	}

	public AppSpec getAppManifest(String id, AppSpec defaultValue) {
		AppSpec appSpec = appSpecRepository.findOne(id);
		return appSpec != null ? appSpec : defaultValue;
	}

	public void delete(AppSpec appSpec) {
		appSpecRepository.delete(appSpec);
	}

}
