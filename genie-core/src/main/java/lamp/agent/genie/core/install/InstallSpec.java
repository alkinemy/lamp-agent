package lamp.agent.genie.core.install;


import lamp.agent.genie.core.script.ScriptCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class InstallSpec {

	private String id;

	private String directory;
	private String filename;

	private List<ScriptCommand> scriptCommands;

}
