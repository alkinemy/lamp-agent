package lamp.client.genie.spring.boot.management.service;


import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.context.DeployContext;
import lamp.client.genie.core.deploy.AppDeployer;
import lamp.client.genie.core.deploy.DeployManifest;
import lamp.client.genie.core.deploy.SimpleAppDeployer;
import lamp.client.genie.spring.boot.base.impl.MultipartFileDeployContext;
import lamp.client.genie.spring.boot.management.repository.DeployManifestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

@Service
public class AppDeployService {

	private AppDeployer appDeployer;

	@Autowired
	private DeployManifestRepository deployManifestRepository;

	@PostConstruct
	public void setUp() {
		appDeployer = new SimpleAppDeployer();
	}

	public void deploy(DeployManifest deployManifest, AppManifest appManifest, MultipartFile multipartFile) {
		DeployContext context = MultipartFileDeployContext.of(deployManifest, appManifest, multipartFile);
		appDeployer.deploy(context);

		deployManifestRepository.save(deployManifest);
	}

	public void undeploy(AppManifest appManifest) {
		DeployManifest deployManifest = deployManifestRepository.findOne(appManifest.getId());

		deployManifestRepository.delete(deployManifest);
	}

}
