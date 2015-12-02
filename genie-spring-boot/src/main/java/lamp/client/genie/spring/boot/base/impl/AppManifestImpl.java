package lamp.client.genie.spring.boot.base.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.runtime.process.AppProcessType;
import lamp.client.genie.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.exec.ExecuteWatchdog;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@Getter
@Setter
@ToString
public class AppManifestImpl implements AppManifest {

	private String id;
	private String name;

	private String type;
	private String version;

	private Boolean deploy;
	private AppProcessType processType;
	private boolean isDaemon;

	private String homeDirectoryPath;
	private String workDirectoryPath;
	private String logDirectoryPath;

	private String pidFilePath;

	private long checkStatusInterval;

	private String startCommandLine;
	private long startTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;
	private String stopCommandLine;
	private long stopTimeout = ExecuteWatchdog.INFINITE_TIMEOUT;
	private Boolean autoStart;
	private Boolean autoStop;

	private Map<String, Object> parameters;

	@JsonIgnore
	@Override
	public File getHomeDirectory() {
		return new File(homeDirectoryPath);
	}

	@JsonIgnore
	@Override
	public File getWorkDirectory() {
		return new File(workDirectoryPath);
	}

	@JsonIgnore
	@Override
	public File getLogDirectory() {
		return new File(logDirectoryPath);
	}

	@Nullable
	@Override
	public File getPidFile() {
		String pidFilePath = getPidFilePath();
		if (StringUtils.isBlank(pidFilePath)) {
			return null;
		} else if (pidFilePath.startsWith("/")) {
			return new File(pidFilePath);
		} else {
			return new File(getWorkDirectory(), pidFilePath);
		}
	}

}