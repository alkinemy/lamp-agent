package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.AppInstanceContext;
import lamp.agent.genie.core.install.AppInstaller;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.install.SimpleAppInstaller;
import lamp.agent.genie.core.install.SimpleUninstallContext;
import lamp.agent.genie.core.script.ScriptCommand;
import lamp.agent.genie.spring.boot.base.impl.MultipartFileInstallContext;
import lamp.agent.genie.spring.boot.management.repository.InstallSpecRepository;
import lamp.agent.genie.spring.boot.management.support.ExpressionParser;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AppInstallService {

	private AppInstaller appInstaller;

	private ExpressionParser expressionParser;

	@Autowired
	private InstallSpecRepository installSpecRepository;


	@PostConstruct
	public void setUp() {
		appInstaller = new SimpleAppInstaller();
		expressionParser = new ExpressionParser();
	}

	public void install(AppInstanceContext appInstanceContext, MultipartFile multipartFile) {
		Map<String, Object> parameters = appInstanceContext.getParameters();
		InstallSpec installSpec = appInstanceContext.getInstallSpec();
		String directory = appInstanceContext.getParsedAppInstanceSpec().getAppDirectory();
		File installDirectory = new File(directory);
		if (!installDirectory.exists()) {
			installDirectory.mkdirs();
		}
		installSpec.setDirectory(installDirectory.getAbsolutePath());

		String filename = installSpec.getFilename();
		if (StringUtils.isBlank(filename)) {
			filename = multipartFile.getOriginalFilename();
		}
		filename = expressionParser.getValue(filename, parameters);
		installSpec.setFilename(filename);

		List<ScriptCommand> commands = installSpec.getScriptCommands();

		File installLogFile = new File(appInstanceContext.getAppMetaInfoDirectory(), "install-" + System.currentTimeMillis() + ".log");

		MultipartFileInstallContext context = MultipartFileInstallContext.of(appInstanceContext, multipartFile, commands, installLogFile);

		appInstaller.install(context);

		installSpecRepository.save(installSpec);
	}

	public void update(AppInstanceContext appInstanceContext, MultipartFile multipartFile) {
		install(appInstanceContext, multipartFile);
	}

	public void uninstall(AppInstanceContext appInstanceContext) {
		appInstaller.uninstall(SimpleUninstallContext.of(appInstanceContext));

		InstallSpec installSpec = appInstanceContext.getInstallSpec();
		installSpecRepository.delete(installSpec);
	}

}
