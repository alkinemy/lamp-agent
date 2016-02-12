package lamp.agent.genie.spring.boot.register.support.jwt;

public class JwtException extends RuntimeException {

	public JwtException(String defaultMessage) {
		super(defaultMessage);
	}

	public JwtException(String defaultMessage, Throwable t) {
		super(defaultMessage, t);
	}

}
