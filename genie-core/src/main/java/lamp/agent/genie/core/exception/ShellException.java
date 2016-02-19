package lamp.agent.genie.core.exception;

public class ShellException extends AppException {

	public ShellException(String msg) {
		super(msg, "SHELL_ERROR");
	}

	public ShellException(String msg, Throwable t) {
		super(msg, t, "SHELL_ERROR");
	}

}
