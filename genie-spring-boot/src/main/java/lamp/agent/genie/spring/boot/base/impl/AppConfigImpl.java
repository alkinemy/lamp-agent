package lamp.agent.genie.spring.boot.base.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.exec.ExecuteWatchdog;

import java.util.Map;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppConfigImpl implements AppConfig {

	private String id;
	private String name;

	private String appName;
	private String appVersion;

	private AppProcessType processType;

	private String homeDirectory;
	private String workDirectory;

	private String pidFile;
	private String logFile;

	private long checkStatusInterval;

	private String startCommandLine;
	private long startTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;
	private String stopCommandLine;
	private long stopTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;

	private boolean monitor;

	private boolean preInstalled;

	private Map<String, Object> parameters;

	private long lastModified;

}