package lamp.agent.genie.spring.boot.base.exception;

import java.util.Objects;

public class MessageException extends RuntimeException {

	private String code;
	private Object[] args;

	public MessageException(String defaultMessage, String code, Object[] args) {
		super(defaultMessage);
		Objects.requireNonNull(code);
		this.code = code;
		this.args = args;
	}

	public MessageException(String defaultMessage, Throwable t, String code, Object[] args) {
		super(defaultMessage, t);
		Objects.requireNonNull(code);
		this.code = code;
		this.args = args;
	}

	public String getCode() {
		return code;
	}

	public Object[] getArgs() {
		return args;
	}

}
