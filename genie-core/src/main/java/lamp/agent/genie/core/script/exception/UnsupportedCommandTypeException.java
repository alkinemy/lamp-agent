package lamp.agent.genie.core.script.exception;

import lamp.agent.genie.core.script.ScriptCommandType;

public class UnsupportedCommandTypeException extends CommandException {

	public UnsupportedCommandTypeException(ScriptCommandType type) {
		super("Unsupported command type :" + type, "UNSUPPORTED_COMMAND_TYPE", new Object[] {type});
	}

}
