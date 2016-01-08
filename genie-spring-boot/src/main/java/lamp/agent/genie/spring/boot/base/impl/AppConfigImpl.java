package lamp.agent.genie.spring.boot.base.impl;

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
public class AppConfigImpl implements AppConfig {

	private String id;
	private String name;

	private String type;
	private String version;

	private AppProcessType processType;

	private String pidFile;
	private String logFile;

	private String homeDirectory;
	private String workDirectory;

	private long checkStatusInterval;

	private String startCommandLine;
	private long startTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;
	private String stopCommandLine;
	private long stopTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;
	private boolean autoStart;
	private boolean autoStop;

	private boolean preInstalled;

	private Map<String, Object> parameters;

	private long lastModified;

}