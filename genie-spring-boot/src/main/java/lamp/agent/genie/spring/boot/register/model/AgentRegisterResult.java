package lamp.agent.genie.spring.boot.register.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentRegisterResult {

	private Long registerId;

	private String hostname;
	private String address;

}
