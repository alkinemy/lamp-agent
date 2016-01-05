package lamp.agent.genie.spring.boot.management.service;


import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.context.InstallContext;
import lamp.agent.genie.core.deploy.AppInstaller;
import lamp.agent.genie.core.deploy.InstallManifest;
import lamp.agent.genie.core.deploy.SimpleAppInstaller;
import lamp.agent.genie.spring.boot.base.impl.MultipartFileDeployContext;
import lamp.agent.genie.spring.boot.management.repository.InstallManifestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

@Service
public class AppDeployService {

	private AppInstaller appDeployer;

	@Autowired
	private InstallManifestRepository deployManifestRepository;

	@PostConstruct
	public void setUp() {
		appDeployer = new SimpleAppInstaller();
	}

	public void install(InstallManifest deployManifest, AppManifest appManifest, MultipartFile multipartFile) {
		InstallContext context = MultipartFileDeployContext.of(deployManifest, appManifest, multipartFile);
		appDeployer.deploy(context);

		deployManifestRepository.save(deployManifest);
	}

	public void undeploy(AppManifest appManifest) {
		InstallManifest deployManifest = deployManifestRepository.findOne(appManifest.getId());

		deployManifestRepository.delete(deployManifest);
	}

}
