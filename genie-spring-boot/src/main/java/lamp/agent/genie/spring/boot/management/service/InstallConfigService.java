package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.management.repository.InstallConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstallConfigService {

	@Autowired
	private InstallConfigRepository installConfigRepository;

	public void save(InstallConfig installConfig) {
		installConfigRepository.save(installConfig);
	}

	public InstallConfig getInstallConfig(String id) {
		InstallConfig installConfig = installConfigRepository.findOne(id);
		Exceptions.throwsException(installConfig == null, ErrorCode.APP_NOT_FOUND, id);
		return installConfig;
	}

	public InstallConfig getInstallConfig(String id, InstallConfig defaultValue) {
		InstallConfig installConfig = installConfigRepository.findOne(id);
		return installConfig != null ? installConfig : defaultValue;
	}

	public void delete(InstallConfig installConfig) {
		installConfigRepository.delete(installConfig);
	}

}
