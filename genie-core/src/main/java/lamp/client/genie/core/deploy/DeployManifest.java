package lamp.client.genie.core.deploy;

import lamp.client.genie.core.deploy.command.DeployCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DeployManifest {

	private String id;

	private boolean overwrite;

	private String filename;

	private List<DeployCommand> commands;

}
