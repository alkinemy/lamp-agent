package lamp.client.genie.spring.boot.management.controller;

import lamp.client.genie.core.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.exec.ExecuteWatchdog;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
public class AppDeployForm {

	@NotEmpty
	private String id;
	@NotEmpty
	private String name;

	@NotEmpty
	private String type;
	@NotEmpty
	private String version;

	@NotNull
	private AppProcessType processType;
	private boolean isDaemon;

	private String homeDirectoryPath;
	private String workingDirectoryPath;
	private String logDirectoryPath;

	@NotEmpty
	private String pidFilePath;

	private long checkStatusInterval;

	@NotNull
	private String startCommandLine;
	private Long startTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;
	private String stopCommandLine;
	private Long stopTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;

	private Map<String, Object> parameters;

	private MultipartFile appFile;

}
