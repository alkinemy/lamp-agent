package lamp.agent.genie.core.script;

import java.util.Map;

public class CommandBuilder {

	private String commandName;
	private Map<String, Object> parameters;

	public CommandBuilder(String commandName) {
		this.commandName = commandName;
	}

	public CommandBuilder parameters(Map<String, Object> parameters) {
		this.parameters = parameters;

		return this;
	}

	public ScriptCommand build() {
		String commandClassname = commandName;
		if (commandClassname.indexOf('.') == -1) {
			commandClassname = ScriptCommand.class.getPackage().getName() + commandClassname;
		}

		try {
			Class<ScriptCommand> commandClass = (Class<ScriptCommand>) Class.forName(commandClassname);
			ScriptCommand command = commandClass.newInstance();


			return command;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid command", e);
		}

	}
}
