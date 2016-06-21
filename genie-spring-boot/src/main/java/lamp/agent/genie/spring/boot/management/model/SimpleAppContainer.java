package lamp.agent.genie.spring.boot.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.core.script.ScriptCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

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

	private AppResource appResource;

	private String groupId;
	private String artifactId;
	private String version;

	@NotNull
	private AppProcessType processType;

	private String appDirectory;
	private String workDirectory;
	private String logDirectory;

	private String pidFile;
	private String ptql;

	private String stdOutFile;
	private String stdErrFile;

	private long checkStatusInterval = 1000L;

	private String commandShell;
	private String startCommandLine;
	private Long startTimeout = INFINITE_TIMEOUT;
	private String stopCommandLine;
	private Long stopTimeout = INFINITE_TIMEOUT;


	// Install
	private boolean preInstalled;
	private String installFilename;

	private String commands;

	private List<ScriptCommand> scriptCommands;
	private Map<String, Object> parameters;


	private boolean monitor;
}
