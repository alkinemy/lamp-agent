package lamp.agent.genie.core.exception;

import lombok.Getter;

public class AppException extends RuntimeException {

	@Getter
	private String code;
	@Getter
	private Object[] args;

	public AppException(String message, String code, Object... args) {
		super(message);
		this.code = code;
		this.args = args;
	}

	public AppException(String message, Throwable t, String code, Object... args) {
		super(message, t);
		this.code = code;
		this.args = args;
	}

}
