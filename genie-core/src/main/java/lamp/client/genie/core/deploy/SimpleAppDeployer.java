package lamp.client.genie.core.deploy;


import lamp.client.genie.core.deploy.command.DeployCommand;
import lamp.client.genie.core.exception.DeployException;
import lamp.client.genie.utils.CollectionUtils;
import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.context.DeployContext;

import java.io.File;
import java.util.List;

public class SimpleAppDeployer implements AppDeployer {

	public SimpleAppDeployer() {
	}

	public void deploy(DeployContext context) {
		try {
			DeployManifest deployManifest = context.getDeployManifest();
			AppManifest appManifest = context.getAppManifest();

			File homeDirectory = appManifest.getHomeDirectory();
			String filename = deployManifest.getFilename();

			File file = new File(homeDirectory, filename);
			context.transferTo(file);

			List<DeployCommand> commands = deployManifest.getCommands();
			if (CollectionUtils.isNotEmpty(commands)) {
				for (DeployCommand command : commands) {
					command.execute(context);
				}
			}
		} catch (Exception e) {
			throw new DeployException("Deploy failed", e);
		}
	}

}
