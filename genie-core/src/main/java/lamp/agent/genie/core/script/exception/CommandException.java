package lamp.agent.genie.core.script.exception;

import lamp.agent.genie.core.exception.AppException;

public class CommandException extends AppException {

	public CommandException(String msg, String code, Object[] args) {
		super(msg, code, args);
	}

	public CommandException(String msg, Throwable t, String code, Object[] args) {
		super(msg, t, code, args);
	}

}
