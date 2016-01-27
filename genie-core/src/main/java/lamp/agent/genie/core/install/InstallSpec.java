package lamp.agent.genie.core.install;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InstallSpec {

	private String id;

	private String directory;
	private String filename;

	private String commands;

}
