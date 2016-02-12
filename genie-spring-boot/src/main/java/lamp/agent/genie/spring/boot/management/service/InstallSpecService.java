package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.management.repository.InstallSpecRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstallSpecService {

	@Autowired
	private InstallSpecRepository installSpecRepository;

	public void save(InstallSpec installSpec) {
		installSpecRepository.save(installSpec);
	}

	public InstallSpec getInstallConfig(String id) {
		InstallSpec installSpec = installSpecRepository.findOne(id);
		Exceptions.throwsException(installSpec == null, ErrorCode.APP_NOT_FOUND, id);
		return installSpec;
	}

	public InstallSpec getInstallConfig(String id, InstallSpec defaultValue) {
		InstallSpec installSpec = installSpecRepository.findOne(id);
		return installSpec != null ? installSpec : defaultValue;
	}

	public void delete(InstallSpec installSpec) {
		installSpecRepository.delete(installSpec);
	}

}
