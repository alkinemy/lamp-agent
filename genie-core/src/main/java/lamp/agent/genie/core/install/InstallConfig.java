package lamp.agent.genie.core.install;


import lamp.agent.genie.core.install.command.Command;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class InstallConfig {

	private String id;

	private String directory;
	private String filename;

	private List<Command> commands;

}
