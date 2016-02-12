package lamp.agent.genie.spring.boot.management.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.install.AppInstaller;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.install.SimpleAppInstaller;
import lamp.agent.genie.core.install.SimpleUninstallContext;
import lamp.agent.genie.core.install.command.Command;
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

	private ObjectMapper objectMapper = new ObjectMapper();

	private AppInstaller appInstaller;

	private ExpressionParser expressionParser;

	@Autowired
	private InstallSpecRepository installSpecRepository;

	@Autowired
	private CommandService commandService;

	@PostConstruct
	public void setUp() {
		appInstaller = new SimpleAppInstaller();
		expressionParser = new ExpressionParser();
	}

	public void install(AppContext appContext, MultipartFile multipartFile) {
		Map<String, Object> parameters = appContext.getParameters();
		InstallSpec installSpec = appContext.getInstallSpec();
		String directory = installSpec.getDirectory();
		if (StringUtils.isBlank(directory)) {
			directory = appContext.getParsedAppSpec().getAppDirectory();
		}
		directory = expressionParser.getValue(directory, parameters);
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

		List<Command> commands = commandService.createCommands(installSpec.getCommands());

		MultipartFileInstallContext context = MultipartFileInstallContext.of(appContext, multipartFile, commands);

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
