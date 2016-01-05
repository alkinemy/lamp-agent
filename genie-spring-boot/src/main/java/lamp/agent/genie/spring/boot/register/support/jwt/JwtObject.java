package lamp.agent.genie.spring.boot.register.support.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor(staticName = "of")
public class JwtObject {

	private JwtHeader header;
	private JwtPayload payload;
	private String signature;

}
