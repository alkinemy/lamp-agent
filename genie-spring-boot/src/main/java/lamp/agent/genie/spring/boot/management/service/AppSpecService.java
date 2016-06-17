package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.AppInstanceSpec;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.management.repository.AppSpecRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AppSpecService {

	@Autowired
	private AppSpecRepository appSpecRepository;

	public void save(AppInstanceSpec appInstanceSpec) {
		appSpecRepository.save(appInstanceSpec);
	}

	public List<AppInstanceSpec> getAppManifests() {
		return appSpecRepository.findAll();
	}

	public AppInstanceSpec getAppManifest(String id) {
		AppInstanceSpec appInstanceSpec = appSpecRepository.findOne(id);
		Exceptions.throwsException(appInstanceSpec == null, ErrorCode.APP_INSTANCE_NOT_FOUND, id);
		return appInstanceSpec;
	}

	public AppInstanceSpec getAppManifest(String id, AppInstanceSpec defaultValue) {
		AppInstanceSpec appInstanceSpec = appSpecRepository.findOne(id);
		return appInstanceSpec != null ? appInstanceSpec : defaultValue;
	}

	public void delete(AppInstanceSpec appInstanceSpec) {
		appSpecRepository.delete(appInstanceSpec);
	}

	public void deleteAppMetaInfoDirectory(AppInstanceSpec appInstanceSpec) {
		appSpecRepository.deleteAppMetaInfoDirectory(appInstanceSpec);
	}

}
