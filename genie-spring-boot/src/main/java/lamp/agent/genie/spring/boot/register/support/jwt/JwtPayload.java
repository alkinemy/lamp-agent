package lamp.agent.genie.spring.boot.register.support.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtPayload {

	@JsonProperty("iss")
	private String issuer;
	@JsonProperty("iat")
	private long issuedAtTime;
	@JsonProperty("exp")
	private long expirationTime;
	@JsonProperty("qsh")
	private String queryStringHash;
	@JsonProperty("sub")
	private String subject;

	@JsonProperty("context")
	private Map<String, Object> context;
}
