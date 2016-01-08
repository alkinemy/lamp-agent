package lamp.agent.genie.core.runtime.process.exec;

import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.exception.PidFileException;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.utils.FileUtils;
import lamp.agent.genie.utils.StringUtils;
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
	private File pidFile;
	@Getter
	private File workDirectory;
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

	private long lastModified;

	public AbstractProcess(AppContext context) {
		Objects.requireNonNull(context);
		this.context = context;

		init();
	}

	protected void init() {
		AppConfig appConfig = context.getAppConfig();
		Map<String, Object> parameters = context.getParameters();

		this.systemLogFile = context.getSystemLogFile();

		this.workDirectory = new File(context.getValue(appConfig.getWorkDirectory(), parameters));
		this.pidFile = getPidFile(appConfig.getPidFile(), parameters);

		this.startCommandLine = context.getValue(appConfig.getStartCommandLine(), parameters);
		this.startTimeout = context.getValue(appConfig.getStartTimeout(), parameters);
		this.stopCommandLine = context.getValue(appConfig.getStopCommandLine(), parameters);
		this.stopTimeout = context.getValue(appConfig.getStopTimeout(), parameters);

		this.lastModified = appConfig.getLastModified();
	}

	public void refresh() {
		AppConfig appConfig = context.getAppConfig();
		if (this.lastModified != appConfig.getLastModified()) {
			log.info("[{}] Process refresh", appConfig.getId());
			init();
		}
	}

	protected File getPidFile(String pidFile, Object parameters) {
		String pidFilePath = context.getValue(pidFile, parameters);
		if (StringUtils.isBlank(pidFilePath)) {
			return null;
		} else if (pidFilePath.startsWith("/")) {
			return new File(pidFilePath);
		} else {
			return new File(getWorkDirectory(), pidFilePath);
		}
	}

	@Override
	public String getId() {
		File pidFile = getPidFile();
		log.debug("[App:{}] pidFile = {}", context.getId(), pidFile != null ? pidFile.getAbsolutePath() : null);
		if (pidFile != null && pidFile.exists()) {
			try {
				return FileUtils.readFileToString(pidFile);
			} catch (IOException e) {
				throw new PidFileException("Can't read pid file", e);
			}
		}
		return null;
	}

	@Override public AppProcessState getStatus() {
		String pid = getId();
		if (StringUtils.isBlank(pid)) {
			return AppProcessState.NOT_RUNNING;
		}
		return getContext().getShell().getProcessState(pid);
	}

}
