package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.AppContext;
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

	@Autowired
	private ScriptCommandService scriptCommandService;

	@PostConstruct
	public void setUp() {
		appInstaller = new SimpleAppInstaller();
		expressionParser = new ExpressionParser();
	}

	public void install(AppContext appContext, MultipartFile multipartFile) {
		Map<String, Object> parameters = appContext.getParameters();
		InstallSpec installSpec = appContext.getInstallSpec();
		String directory = appContext.getParsedAppSpec().getAppDirectory();
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

		List<ScriptCommand> commands = scriptCommandService.parse(installSpec.getCommands());

		File installLogFile = new File(appContext.getAppMetaInfoDirectory(), "install-" + System.currentTimeMillis() + ".log");

		MultipartFileInstallContext context = MultipartFileInstallContext.of(appContext, multipartFile, commands, installLogFile);

		appInstaller.install(context);

		installSpecRepository.save(installSpec);
	}

	public void update(AppContext appContext, MultipartFile multipartFile) {
		install(appContext, multipartFile);
	}

	public void uninstall(AppContext appContext) {
		appInstaller.uninstall(SimpleUninstallContext.of(appContext));

		InstallSpec installSpec = appContext.getInstallSpec();
		installSpecRepository.delete(installSpec);
	}

}
