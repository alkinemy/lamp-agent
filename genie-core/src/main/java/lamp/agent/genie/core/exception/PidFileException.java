package lamp.agent.genie.core.exception;

public class PidFileException extends AppException {

	public PidFileException(String msg) {
		super(msg, "PID_FILE_ERROR");
	}

	public PidFileException(String msg, Throwable t) {
		super(msg, t, "PID_FILE_ERROR");
	}

}
