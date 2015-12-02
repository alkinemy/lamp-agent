package lamp.client.genie.core.exception;

public class CommandExecuteException extends AppException {

	public CommandExecuteException(String command) {
		super("Command execute failed");
	}

	public CommandExecuteException(String command, Throwable t) {
		super("Command execute failed", t);
	}

}
