package lamp.client.genie.spring.boot.management.service;

import lamp.client.genie.core.AppManifest;
import lamp.client.genie.spring.boot.base.exception.ErrorCode;
import lamp.client.genie.spring.boot.base.exception.Exceptions;
import lamp.client.genie.spring.boot.management.repository.AppManifestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AppManifestService {

	@Autowired
	private AppManifestRepository appManifestRepository;

	public void save(AppManifest appManifest) {
		appManifestRepository.save(appManifest);
	}

	public List<AppManifest> getAppManifests() {
		return appManifestRepository.findAll();
	}

	public AppManifest getAppManifest(String id) {
		AppManifest appManifest = appManifestRepository.findOne(id);
		Exceptions.throwsException(appManifest == null, ErrorCode.APP_NOT_FOUND, id);
		return appManifest;
	}

	public AppManifest getAppManifest(String id, AppManifest defaultValue) {
		AppManifest appManifest = appManifestRepository.findOne(id);
		return appManifest != null ? appManifest : defaultValue;
	}

	public void delete(AppManifest appManifest) {
		appManifestRepository.delete(appManifest);
	}

}
