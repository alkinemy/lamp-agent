package lamp.agent.genie.spring.boot.management.service;


import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.deploy.AppInstaller;
import lamp.agent.genie.core.deploy.InstallManifest;
import lamp.agent.genie.core.deploy.SimpleAppInstaller;
import lamp.agent.genie.spring.boot.base.impl.MultipartFileInstallContext;
import lamp.agent.genie.spring.boot.management.repository.InstallManifestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class AppInstallService {

	private AppInstaller appInstaller;

	@Autowired
	private InstallManifestRepository installManifestRepository;

	@PostConstruct
	public void setUp() {
		appInstaller = new SimpleAppInstaller();
	}

	public void install(InstallManifest installManifest, AppManifest appManifest, MultipartFile multipartFile) {
		MultipartFileInstallContext context = MultipartFileInstallContext.of(installManifest, appManifest, multipartFile);

		File homeDirectory = appManifest.getHomeDirectory();
		String filename = installManifest.getFilename();
		if (!homeDirectory.exists()) {
			homeDirectory.mkdirs();
		}
		File installFile = new File(homeDirectory, filename);
		context.setInstallFile(installFile);

		appInstaller.install(context);

		installManifestRepository.save(installManifest);
	}

	public void uninstall(AppManifest appManifest) {
		InstallManifest installManifest = installManifestRepository.findOne(appManifest.getId());

		installManifestRepository.delete(installManifest);
	}

}
