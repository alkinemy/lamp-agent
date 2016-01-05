package lamp.agent.genie.core.exception;

public class DeployException extends AppException {

	public DeployException(String msg) {
		super(msg);
	}

	public DeployException(String msg, Throwable t) {
		super(msg, t);
	}

}
