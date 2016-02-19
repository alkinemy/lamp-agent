package lamp.agent.genie.core.script.exception;

public class CommandExecuteException extends CommandException {

	public CommandExecuteException(String command) {
		super("Command execute failed : " + command, "COMMAND_EXECUTE_ERROR", new Object[] {command});
	}

	public CommandExecuteException(Throwable t, String command) {
		super("Command execute failed : " + command, t, "COMMAND_EXECUTE_ERROR", new Object[] {command});
	}

}
