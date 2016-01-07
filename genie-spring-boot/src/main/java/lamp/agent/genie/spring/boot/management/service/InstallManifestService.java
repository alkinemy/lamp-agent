package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.deploy.InstallManifest;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.management.repository.InstallManifestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstallManifestService {

	@Autowired
	private InstallManifestRepository installManifestRepository;

	public void save(InstallManifest installManifest) {
		installManifestRepository.save(installManifest);
	}

	public InstallManifest getInstallManifest(String id) {
		InstallManifest installManifest = installManifestRepository.findOne(id);
		Exceptions.throwsException(installManifest == null, ErrorCode.APP_NOT_FOUND, id);
		return installManifest;
	}

	public InstallManifest getInstallManifest(String id, InstallManifest defaultValue) {
		InstallManifest installManifest = installManifestRepository.findOne(id);
		return installManifest != null ? installManifest : defaultValue;
	}

	public void delete(InstallManifest installManifest) {
		installManifestRepository.delete(installManifest);
	}

}
