package lamp.agent.genie.core.command;

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

	public Command build() {
		String commandClassname = commandName;
		if (commandClassname.indexOf('.') == -1) {
			commandClassname = Command.class.getPackage().getName() + commandClassname;
		}

		try {
			Class<Command> commandClass = (Class<Command>) Class.forName(commandClassname);
			Command command = commandClass.newInstance();


			return command;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid command", e);
		}

	}
}
