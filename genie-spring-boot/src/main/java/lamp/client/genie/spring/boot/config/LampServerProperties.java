package lamp.client.genie.spring.boot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "lamp.api.server")
public class LampServerProperties {

	private String url;

	private String username;
	private String password;

}
