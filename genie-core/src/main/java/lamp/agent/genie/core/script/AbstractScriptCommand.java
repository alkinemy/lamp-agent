package lamp.agent.genie.core.script;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class AbstractScriptCommand implements ScriptCommand {

	private Long id;
	private String name;
	private String description;

}
