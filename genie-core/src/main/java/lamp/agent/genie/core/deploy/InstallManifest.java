package lamp.agent.genie.core.deploy;

import lamp.agent.genie.core.deploy.command.InstallCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class InstallManifest {

	private String id;

	private String filename;

	private List<InstallCommand> commands;

}
