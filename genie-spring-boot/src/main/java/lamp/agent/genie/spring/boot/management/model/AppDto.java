package lamp.agent.genie.spring.boot.management.model;

import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.runtime.process.AppProcessType;
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
	private String appName;
	private String appVersion;

	private AppProcessType processType;
	private String pid;
	private AppStatus status;
	private AppStatus correctStatus;

	private boolean isMonitor;

}
