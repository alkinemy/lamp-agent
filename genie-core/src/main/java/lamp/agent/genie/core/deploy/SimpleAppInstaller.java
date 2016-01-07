package lamp.agent.genie.core.deploy;


import lamp.agent.genie.core.context.InstallContext;
import lamp.agent.genie.core.deploy.command.InstallCommand;
import lamp.agent.genie.core.exception.InstallException;
import lamp.agent.genie.utils.CollectionUtils;

import java.io.File;
import java.util.List;

public class SimpleAppInstaller implements AppInstaller {

	public SimpleAppInstaller() {
	}

	public void install(InstallContext context) {
		try {
			InstallManifest installManifest = context.getInstallManifest();

			File file = context.getInstallFile();
			context.transferTo(file);

			List<InstallCommand> commands = installManifest.getCommands();
			if (CollectionUtils.isNotEmpty(commands)) {
				for (InstallCommand command : commands) {
					command.execute(context);
				}
			}
		} catch (Exception e) {
			throw new InstallException("Install failed", e);
		}
	}

}
