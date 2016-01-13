package lamp.agent.genie.spring.boot.management.form;

import lamp.agent.genie.core.runtime.process.AppProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.exec.ExecuteWatchdog;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@ToString
public class AppUpdateForm {

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

	private String homeDirectoryPath;
	private String workDirectoryPath;
	private String logDirectoryPath;

	@NotEmpty
	private String pidFilePath;

	private long checkStatusInterval = 1000L;

	@NotNull
	private String startCommandLine;
	private Long startTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;
	private String stopCommandLine;
	private Long stopTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;

	private boolean monitor;

	private Map<String, Object> parameters;

	private MultipartFile installFile;

}
