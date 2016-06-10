package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ArtifactAppResource implements AppResource {

	private String repositoryId;

	private String groupId;

	private String artifactId;

	private String version;

}
