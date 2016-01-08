package lamp.agent.genie.spring.boot.management.service;


import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.install.AppInstaller;
import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.core.install.SimpleAppInstaller;
import lamp.agent.genie.core.install.SimpleUninstallContext;
import lamp.agent.genie.spring.boot.base.impl.MultipartFileInstallContext;
import lamp.agent.genie.spring.boot.management.repository.InstallConfigRepository;
import lamp.agent.genie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class AppInstallService {

	private AppInstaller appInstaller;

	@Autowired
	private InstallConfigRepository installConfigRepository;

	@PostConstruct
	public void setUp() {
		appInstaller = new SimpleAppInstaller();
	}

	public void install(AppContext appContext, MultipartFile multipartFile) {
		AppConfig appConfig = appContext.getAppConfig();
		InstallConfig installConfig = appContext.getInstallConfig();

		File installDirectory = new File(appContext.getValue(appConfig.getHomeDirectory(), appContext.getParameters()));
		if (!installDirectory.exists()) {
			installDirectory.mkdirs();
		}
		installConfig.setDirectory(installDirectory.getAbsolutePath());

		String filename = installConfig.getFilename();
		if (StringUtils.isBlank(filename)) {
			filename = multipartFile.getOriginalFilename();
			installConfig.setFilename(filename);
		}

		MultipartFileInstallContext context = MultipartFileInstallContext.of(appContext, multipartFile);

		appInstaller.install(context);

		installConfigRepository.save(installConfig);
	}

	public void uninstall(AppContext appContext) {
		appInstaller.uninstall(SimpleUninstallContext.of(appContext));

		InstallConfig installConfig = appContext.getInstallConfig();
		installConfigRepository.delete(installConfig);
	}

}
