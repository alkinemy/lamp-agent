package lamp.agent.genie.spring.boot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "metrics.diskSpace")
public class MetricsDiskSpaceProperties {

	private String name;
	private String path;

}
