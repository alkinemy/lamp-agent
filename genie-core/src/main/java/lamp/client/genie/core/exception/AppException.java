package lamp.client.genie.core.exception;

public class AppException extends RuntimeException {

	public AppException(String message) {
		super(message);
	}

	public AppException(String message, Throwable t) {
		super(message, t);
	}

}
