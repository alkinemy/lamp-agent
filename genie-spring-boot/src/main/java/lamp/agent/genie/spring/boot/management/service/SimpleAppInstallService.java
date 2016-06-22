package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lamp.agent.genie.core.app.simple.SimpleAppContext;
import lamp.agent.genie.core.app.simple.install.AppInstaller;
import lamp.agent.genie.core.app.simple.install.InstallSpec;
import lamp.agent.genie.core.app.simple.install.SimpleAppInstaller;
import lamp.agent.genie.core.app.simple.install.SimpleUninstallContext;
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
public class SimpleAppInstallService {

	private AppInstaller appInstaller;

	private ExpressionParser expressionParser;

	@Autowired
	private InstallSpecRepository installSpecRepository;


	@PostConstruct
	public void setUp() {
		appInstaller = new SimpleAppInstaller();
		expressionParser = new ExpressionParser();
	}

	public void install(SimpleAppContainer simpleAppContainer, SimpleAppContext appContext, MultipartFile multipartFile) {
		Map<String, Object> parameters = appContext.getParameters();
		InstallSpec installSpec = newInstallSpec(simpleAppContainer);
		String directory = appContext.getParsedAppContainer().getAppDirectory();
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

		File installLogFile = new File(appContext.getAppMetaInfoDirectory(), "install-" + System.currentTimeMillis() + ".log");

		MultipartFileInstallContext context = MultipartFileInstallContext.of(appContext, installSpec, multipartFile, commands, installLogFile);

		appInstaller.install(context);

		installSpecRepository.save(installSpec);
	}

	protected InstallSpec newInstallSpec(SimpleAppContainer appContainer) {
		InstallSpec installSpec = new InstallSpec();
		installSpec.setId(appContainer.getId());
		installSpec.setDirectory(null);
		installSpec.setFilename(appContainer.getInstallFilename());
		installSpec.setScriptCommands(appContainer.getScriptCommands());

		return installSpec;
	}


	public void uninstall(SimpleAppContainer appContainer, SimpleAppContext appContext) {
		InstallSpec installSpec = newInstallSpec(appContainer);
		appInstaller.uninstall(SimpleUninstallContext.of(appContext, installSpec));


		installSpecRepository.delete(installSpec);
	}

}
