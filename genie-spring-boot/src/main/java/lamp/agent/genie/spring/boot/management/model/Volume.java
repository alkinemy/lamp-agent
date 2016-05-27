package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Volume {

	private String containerPath;
	private String hostPath;
	private VolumeMode mode;

}
