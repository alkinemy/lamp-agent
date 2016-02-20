package lamp.agent.genie.spring.boot.base.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

import static org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppSpecImpl implements AppSpec {

	private String id;
	private String name;
	private String description;

	private String groupId;
	private String artifactId;
	private String version;

	private AppProcessType processType;

	private String appDirectory;
	private String workDirectory;
	private String logDirectory;

	private String pidFile;
	private String ptql;
	private String stdOutFile;
	private String stdErrFile;

	private long checkStatusInterval;

	private String commandShell;
	private String startCommandLine;
	private long startTimeout = INFINITE_TIMEOUT;
	private String stopCommandLine;
	private long stopTimeout = INFINITE_TIMEOUT;

	private boolean monitor;

	private boolean preInstalled;

	private Map<String, Object> parameters;

	private long lastModified;

}