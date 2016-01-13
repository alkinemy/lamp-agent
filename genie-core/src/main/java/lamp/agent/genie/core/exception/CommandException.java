package lamp.agent.genie.core.exception;

public class CommandException extends AppException {

	public CommandException(String msg) {
		super(msg);
	}

	public CommandException(String msg, Throwable t) {
		super(msg, t);
	}

}
