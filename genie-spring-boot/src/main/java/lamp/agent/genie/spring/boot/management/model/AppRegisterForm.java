package lamp.agent.genie.spring.boot.management.model;

import lamp.agent.genie.core.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.exec.ExecuteWatchdog;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT;

@Getter
@Setter
@ToString
public class AppRegisterForm {

	@NotEmpty
	private String id;
	@NotEmpty
	private String name;

	@NotEmpty
	private String appName;
	@NotEmpty
	private String appVersion;

	@NotNull
	private AppProcessType processType;

	private String appDirectory;
	private String workDirectory;

	@NotEmpty
	private String pidFile;
	private String logFile;

	private long checkStatusInterval = 1000L;

	@NotEmpty
	private String startCommandLine;
	private Long startTimeout = INFINITE_TIMEOUT;
	private String stopCommandLine;
	private Long stopTimeout = INFINITE_TIMEOUT;

	private boolean monitor;

	private Map<String, Object> parameters;

	// Install
	private boolean preInstalled;
	private String filename;
	private MultipartFile installFile;

	private String commands;

}
