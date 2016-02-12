package lamp.agent.genie.core.runtime.process.exec;

import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.exception.PidFileException;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.utils.FileUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;

import java.io.File;
import java.io.IOException;
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
		AppSpec appSpec = context.getAppSpec();
		AppSpec parsedAppSpec = context.getParsedAppSpec();

		this.systemLogFile = context.getSystemLogFile();

		this.workDirectory = new File(parsedAppSpec.getWorkDirectory());
		this.pidFile = new File(parsedAppSpec.getPidFile());

		this.startCommandLine = parsedAppSpec.getStartCommandLine();
		this.startTimeout = parsedAppSpec.getStartTimeout();
		this.stopCommandLine = parsedAppSpec.getStopCommandLine();
		this.stopTimeout = parsedAppSpec.getStopTimeout();

		this.lastModified = appSpec.getLastModified();
	}

	public void refresh() {
		AppSpec appSpec = context.getAppSpec();
		if (this.lastModified != appSpec.getLastModified()) {
			log.info("[{}] Process refresh", appSpec.getId());
			init();
		}
	}

	protected CommandLine parseCommandLine(String command) {
		String commandShell = getContext().getParsedAppSpec().getCommandShell();
		if (StringUtils.isNotBlank(commandShell)) {
			String[] commandShellArray = StringUtils.split(commandShell, " ");
			CommandLine commandLine = new CommandLine(commandShellArray[0]);
			if (commandShellArray.length > 1) {
				for (int i = 1; i < commandShellArray.length; i++) {
					commandLine.addArgument(commandShellArray[i], false);
				}
			}
			commandLine.addArgument(command, false);

			return commandLine;
		} else {
			return CommandLine.parse(command);
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

}
