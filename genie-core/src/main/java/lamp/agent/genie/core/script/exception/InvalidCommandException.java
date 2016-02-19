package lamp.agent.genie.core.script.exception;

import lamp.agent.genie.core.exception.AppException;

public class InvalidCommandException extends AppException {

	public InvalidCommandException(Throwable t, String command) {
		super("Invalid command : " + command, t, "INVALID_COMMAND", new Object[] {command});
	}

}
