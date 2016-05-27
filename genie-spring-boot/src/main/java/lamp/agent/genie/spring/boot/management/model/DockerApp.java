package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class DockerApp {

	private String id;

	private String groupId;

	private int cpu;
	private int memory;
	private int diskSpace;
	private int instances;

	private DockerContainer container;

}
