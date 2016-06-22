package lamp.agent.genie.core.app.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lamp.agent.genie.core.app.AppContainer;
import lamp.agent.genie.core.app.AppContainerType;
import lamp.agent.genie.core.app.simple.resource.AppResource;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcessType;
import lamp.agent.genie.core.script.ScriptCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

import static org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(AppContainerType.Names.SIMPLE)
public class SimpleAppContainer implements AppContainer {

	private String id;
	private String name;

	private AppResource appResource;
	private String appDirectory;
	private String workDirectory;
	private String logDirectory;

	private String pidFile;
	private String ptql;

	private String stdOutFile;
	private String stdErrFile;

	private AppProcessType processType;

	private String commandShell;
	private String startCommandLine;
	private Long startTimeout = INFINITE_TIMEOUT;
	private String stopCommandLine;
	private Long stopTimeout = INFINITE_TIMEOUT;


	// Install
	private boolean preInstalled;
	private String installFilename;

	private List<ScriptCommand> scriptCommands;
	private Map<String, Object> parameters;

	private long checkStatusInterval = 1000L;

	private boolean monitor;

}
