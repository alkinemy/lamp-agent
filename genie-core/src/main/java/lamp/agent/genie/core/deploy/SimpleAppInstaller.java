package lamp.agent.genie.core.deploy;


import lamp.agent.genie.core.exception.InstallException;
import lamp.agent.genie.core.deploy.command.InstallCommand;
import lamp.agent.genie.utils.CollectionUtils;
import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.context.InstallContext;

import java.io.File;
import java.util.List;

public class SimpleAppInstaller implements AppInstaller {

	public SimpleAppInstaller() {
	}

	public void deploy(InstallContext context) {
		try {
			InstallManifest deployManifest = context.getDeployManifest();
			AppManifest appManifest = context.getAppManifest();

			File homeDirectory = appManifest.getHomeDirectory();
			String filename = deployManifest.getFilename();

			File file = new File(homeDirectory, filename);
			context.transferTo(file);

			List<InstallCommand> commands = deployManifest.getCommands();
			if (CollectionUtils.isNotEmpty(commands)) {
				for (InstallCommand command : commands) {
					command.execute(context);
				}
			}
		} catch (Exception e) {
			throw new InstallException("Deploy failed", e);
		}
	}

}
