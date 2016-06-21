package lamp.agent.genie.spring.boot.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(AppContainerType.Names.DOCKER)
public class DockerAppContainer implements AppContainer {

	private String id;
	private String image;
	private String network;

	private boolean forcePullImage;
	private boolean privileged;

	private List<String> portMappings;

	private List<String> volumes;

	private List<String> env;

//	private List<Parameter> parameters = new ArrayList<>();



}
