package lamp.client.genie.core.runtime.process.exec;

import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.context.AppContext;
import lamp.client.genie.core.exception.PidFileException;
import lamp.client.genie.core.runtime.process.AppProcess;
import lamp.client.genie.core.runtime.process.AppProcessStatus;
import lamp.client.genie.utils.FileUtils;
import lamp.client.genie.utils.StringUtils;
import lamp.client.genie.utils.VariableReplaceUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class AbstractProcess implements AppProcess {

	@Getter
	private final AppContext context;
	@Getter
	private final File pidFile;
	@Getter
	private File workingDirectory;
	@Getter
	private File systemLogFile;
	@Getter
	private String startCommandLine;
	@Getter
	private long startTimeout;
	@Getter
	private String stopCommandLine;
	@Getter
	private long stopTimeout;

	public AbstractProcess(AppContext context) {
		Objects.requireNonNull(context);
		this.context = context;

		AppManifest config = context.getAppManifest();

		Map<String, Object> parameters = context.getParameters();
		this.pidFile = config.getPidFile();
		this.workingDirectory = config.getWorkDirectory();
		this.systemLogFile = null;
		this.startCommandLine = VariableReplaceUtils.replaceVariables(config.getStartCommandLine(), parameters);
		this.startTimeout = config.getStartTimeout();
		this.stopCommandLine = VariableReplaceUtils.replaceVariables(config.getStopCommandLine(), parameters);
		this.stopTimeout = config.getStopTimeout();
	}

	@Override
	public String getId() {
		File pidFile = getContext().getPidFile();
		if (pidFile != null && pidFile.exists()) {
			try {
				return FileUtils.readFileToString(pidFile);
			} catch (IOException e) {
				throw new PidFileException("Can't read pid file", e);
			}
		}
		return null;
	}

	@Override public AppProcessStatus getStatus() {
		String pid = getId();
		if (StringUtils.isBlank(pid)) {
			return AppProcessStatus.NOT_RUNNING;
		}
		try {
			return getContext().getShell().getProcessStatus(pid);
		} catch (IOException e) {
			log.info("Unable to get process status", e);
			return AppProcessStatus.NOT_RUNNING;
		}
	}

}
