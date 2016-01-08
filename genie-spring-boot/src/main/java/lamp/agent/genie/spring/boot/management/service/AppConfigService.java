package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.management.repository.AppConfigRepository;
import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AppConfigService {

	@Autowired
	private AppConfigRepository appConfigRepository;

	public void save(AppConfig appConfig) {
		appConfigRepository.save(appConfig);
	}

	public List<AppConfig> getAppManifests() {
		return appConfigRepository.findAll();
	}

	public AppConfig getAppManifest(String id) {
		AppConfig appConfig = appConfigRepository.findOne(id);
		Exceptions.throwsException(appConfig == null, ErrorCode.APP_NOT_FOUND, id);
		return appConfig;
	}

	public AppConfig getAppManifest(String id, AppConfig defaultValue) {
		AppConfig appConfig = appConfigRepository.findOne(id);
		return appConfig != null ? appConfig : defaultValue;
	}

	public void delete(AppConfig appConfig) {
		appConfigRepository.delete(appConfig);
	}

}
