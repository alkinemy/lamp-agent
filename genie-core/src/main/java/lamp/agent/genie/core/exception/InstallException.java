package lamp.agent.genie.core.exception;

public class InstallException extends AppException {

	public InstallException(String msg) {
		super(msg, "INSTALL_FAILED");
	}

	public InstallException(String msg, Throwable t) {
		super(msg, t, "INSTALL_FAILED");
	}

}
