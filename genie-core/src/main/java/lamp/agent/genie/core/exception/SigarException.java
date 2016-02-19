package lamp.agent.genie.core.exception;

public class SigarException extends AppException {

	public SigarException(String msg) {
		super(msg, "SIGAR_ERROR");
	}

	public SigarException(String msg, Throwable t) {
		super(msg, t, "SIGAR_ERROR");
	}

}
