package lamp.agent.genie.spring.boot.register.support.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class JwtHeader {

	@JsonProperty("typ")
	@NonNull
	private TokenType type;

	@JsonProperty("alg")
	@NonNull
	private TokenAlgorithm algorithm;

}
