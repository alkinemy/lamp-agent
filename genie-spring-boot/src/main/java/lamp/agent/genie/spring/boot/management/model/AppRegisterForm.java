package lamp.agent.genie.spring.boot.management.model;

import lamp.agent.genie.core.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

import static org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT;

@Getter
@Setter
@ToString
public class AppRegisterForm {

	@NotEmpty
	private String id;
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
	@NotEmpty
	private String workDirectory;
	private String logDirectory;

	private String pidFile;
	private String ptql;
	private String stdOutFile;
	private String stdErrorFile;

	private long checkStatusInterval = 1000L;

	private String commandShell;
	@NotEmpty
	private String startCommandLine;
	private Long startTimeout = INFINITE_TIMEOUT;
	private String stopCommandLine;
	private Long stopTimeout = INFINITE_TIMEOUT;

	private boolean monitor;

	// Install
	private boolean preInstalled;
	private String filename;
	private MultipartFile installFile;

	private String commands;


	private String parametersType;
	private String parameters;
}
