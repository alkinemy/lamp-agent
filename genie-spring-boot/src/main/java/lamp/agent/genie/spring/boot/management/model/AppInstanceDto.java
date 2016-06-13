package lamp.agent.genie.spring.boot.management.model;

import lamp.agent.genie.core.AppInstanceStatus;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppInstanceDto {

	private String id;
	private String name;
	private String description;

	private String appId;
	private String appVersion;
	private String hostId;

	private AppProcessType processType;
	private String pid;
	private AppInstanceStatus status;
	private String statusMessage;

	private boolean monitored;

}
