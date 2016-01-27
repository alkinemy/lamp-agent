package lamp.agent.genie.core.install;


import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.exception.InstallException;
import lamp.agent.genie.core.install.command.Command;
import lamp.agent.genie.utils.CollectionUtils;
import lamp.agent.genie.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class SimpleAppInstaller implements AppInstaller {

	public SimpleAppInstaller() {
	}

	@Override public void install(InstallContext context) {
		try {
			AppContext appContext = context.getAppContext();
			InstallSpec installSpec = appContext.getInstallSpec();

			File file = new File(installSpec.getDirectory(), installSpec.getFilename());
			context.transferTo(file);

			List<Command> commands = context.getCommands();

			if (CollectionUtils.isNotEmpty(commands)) {
				for (Command command : commands) {
					command.execute(appContext);
				}
			}
		} catch (Exception e) {
			throw new InstallException("Install failed", e);
		}
	}

	@Override public void uninstall(UninstallContext context) {
		AppContext appContext = context.getAppContext();
		InstallSpec installSpec = appContext.getInstallSpec();

		File file = new File(installSpec.getDirectory(), installSpec.getFilename());

		log.info("[App:{}] file({}) deleting", appContext.getId(), file.getAbsolutePath());
		FileUtils.deleteQuietly(file);
	}

}
