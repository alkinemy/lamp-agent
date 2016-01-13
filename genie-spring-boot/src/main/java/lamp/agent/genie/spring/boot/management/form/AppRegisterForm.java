package lamp.agent.genie.spring.boot.management.form;

import lamp.agent.genie.core.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.exec.ExecuteWatchdog;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

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

	private String homeDirectory;
	private String workDirectory;

	@NotEmpty
	private String pidFile;
	private String logFile;


	private long checkStatusInterval = 1000L;

	@NotEmpty
	private String startCommandLine;
	private Long startTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;
	private String stopCommandLine;
	private Long stopTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;

	private Map<String, Object> parameters;

	// Install
	private boolean preInstalled;
	private String filename;
	private MultipartFile installFile;

	private String commands;

}
