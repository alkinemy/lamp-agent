package lamp.agent.genie.core.exception;

public class CommandExecuteException extends CommandException {

	public CommandExecuteException(String command) {
		super("Command execute failed");
	}

	public CommandExecuteException(String command, Throwable t) {
		super("Command execute failed", t);
	}

}
