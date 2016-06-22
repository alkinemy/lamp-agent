package lamp.agent.genie.spring.boot.management.model;

import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppDto {

	private String id;
	private String name;
	private String description;

	private String appId;
	private String appVersion;
	private String hostId;

	private AppProcessType processType;
	private String pid;
	private AppStatus status;
	private String statusMessage;

	private boolean monitored;

}
