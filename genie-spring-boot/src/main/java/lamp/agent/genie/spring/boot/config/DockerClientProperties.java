package lamp.agent.genie.spring.boot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "docker.client")
public class DockerClientProperties {

	private boolean enabled;

	private String dockerHost;
	private boolean dockerTlsVerify = true;
	private String dockerCertPath;

	private String dockerConfig;
	private String apiVersion = "1.21";


	private String registryUrl;
	private String registryUsername;
	private String registryPassword;
	private String registryEmail;

}
