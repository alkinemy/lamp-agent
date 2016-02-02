package lamp.agent.genie.spring.boot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "lamp.server")
public class LampServerProperties {

	private String url;

	private String username;
	private String password;

	private int connectTimeout = 3 * 1000;
	private int connectionRequestTimeout = 3 * 1000;
	private int readTimeout = 3 * 1000;

}
