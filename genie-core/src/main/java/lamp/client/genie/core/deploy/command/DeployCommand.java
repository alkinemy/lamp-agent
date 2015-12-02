package lamp.client.genie.core.deploy.command;

import lamp.client.genie.core.context.DeployContext;

public interface DeployCommand {

	void execute(DeployContext context);

}
