package lamp.agent.genie.spring.boot.management.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.exception.CommandException;
import lamp.agent.genie.core.exception.UnknownCommandException;
import lamp.agent.genie.core.install.AppInstaller;
import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.core.install.SimpleAppInstaller;
import lamp.agent.genie.core.install.SimpleUninstallContext;
import lamp.agent.genie.core.install.command.Command;
import lamp.agent.genie.spring.boot.base.impl.MultipartFileInstallContext;
import lamp.agent.genie.spring.boot.management.repository.InstallConfigRepository;
import lamp.agent.genie.spring.boot.management.service.install.ExtendedCommand;
import lamp.agent.genie.spring.boot.management.service.install.SpringBootInstallCommand;
import lamp.agent.genie.spring.boot.management.support.ExpressionParser;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AppInstallService {

	private ObjectMapper objectMapper = new ObjectMapper();

	private AppInstaller appInstaller;

	private ExpressionParser expressionParser;

	@Autowired
	private InstallConfigRepository installConfigRepository;

	@Autowired
	private CommandService commandService;

	@PostConstruct
	public void setUp() {
		appInstaller = new SimpleAppInstaller();
		expressionParser = new ExpressionParser();
	}

	public void install(AppContext appContext, MultipartFile multipartFile) {
		Map<String, Object> parameters = appContext.getParameters();
		InstallConfig installConfig = appContext.getInstallConfig();
		String directory = installConfig.getDirectory();
		if (StringUtils.isBlank(directory)) {
			directory = appContext.getParsedAppConfig().getHomeDirectory();
		}
		directory = expressionParser.getValue(directory, parameters);
		File installDirectory = new File(directory);
		if (!installDirectory.exists()) {
			installDirectory.mkdirs();
		}
		installConfig.setDirectory(installDirectory.getAbsolutePath());

		String filename = installConfig.getFilename();
		if (StringUtils.isBlank(filename)) {
			filename = multipartFile.getOriginalFilename();
		}
		filename = expressionParser.getValue(filename, parameters);
		installConfig.setFilename(filename);

		List<Command> commands = commandService.createCommands(installConfig.getCommands());

		MultipartFileInstallContext context = MultipartFileInstallContext.of(appContext, multipartFile, commands);

		appInstaller.install(context);

		installConfigRepository.save(installConfig);
	}

	public void uninstall(AppContext appContext) {
		appInstaller.uninstall(SimpleUninstallContext.of(appContext));

		InstallConfig installConfig = appContext.getInstallConfig();
		installConfigRepository.delete(installConfig);
	}

}
