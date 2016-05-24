package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DockerContainerRunForm {

	private String imageName;
	private boolean imageUpdate = true;

	private String networkMode;

	private List<String> parameters;

	private List<String> ports;

	private String volumes;

}
