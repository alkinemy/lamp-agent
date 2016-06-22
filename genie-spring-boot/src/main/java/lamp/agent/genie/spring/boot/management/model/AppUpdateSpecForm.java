package lamp.agent.genie.spring.boot.management.model;

import lamp.agent.genie.core.app.simple.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT;

@Getter
@Setter
@ToString
public class AppUpdateSpecForm {

	@NotEmpty
	private String name;

	private String groupId;
	@NotEmpty
	private String artifactId;
	@NotEmpty
	private String version;

	@NotNull
	private AppProcessType processType;

	private String appDirectory;
	private String workDirectory;

	@NotEmpty
	private String pidFile;
	private String logFile;

	private long checkStatusInterval = 1000L;

	private String commandShell;
	@NotEmpty
	private String startCommandLine;
	private Long startTimeout = INFINITE_TIMEOUT;
	private String stopCommandLine;
	private Long stopTimeout = INFINITE_TIMEOUT;

	private boolean monitor;

	private Map<String, Object> parameters;

}
