package lamp.agent.genie.core.exception;

public class UnknownCommandException extends CommandException {

	public UnknownCommandException(String command) {
		super("Unknown command");
	}

	public UnknownCommandException(String command, Throwable t) {
		super("Unknown command", t);
	}

}
