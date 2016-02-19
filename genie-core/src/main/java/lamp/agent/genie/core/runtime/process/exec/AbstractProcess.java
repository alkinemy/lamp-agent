package lamp.agent.genie.core.runtime.process.exec;

import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.exception.PidFileException;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessTime;
import lamp.agent.genie.utils.CommandLineUtils;
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
	private String ptql;
	@Getter
	private File stdOutFile;
	@Getter
	private File stdErrFile;
	@Getter
	private File workDirectory;
	@Getter
	private String startCommandLine;
	@Getter
	private long startTimeout;
	@Getter
	private String stopCommandLine;
	@Getter
	private long stopTimeout;

	private String pidFromPtql;
	private long procssFromPtqlTime;
	private long lastModified;

	public AbstractProcess(AppContext context) {
		Objects.requireNonNull(context);
		this.context = context;
		init();
	}

	protected void init() {
		AppSpec appSpec = context.getAppSpec();
		AppSpec parsedAppSpec = context.getParsedAppSpec();

		this.stdOutFile = context.getStdOutFile();
		this.stdErrFile = context.getStdErrFile();

		this.workDirectory = new File(parsedAppSpec.getWorkDirectory());
		this.pidFile = new File(parsedAppSpec.getPidFile());
		this.ptql = parsedAppSpec.getPtql();

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
		return CommandLineUtils.parse(getContext().getParsedAppSpec(), command);
	}

	@Override
	public String getId() {
		String id = getPidFromFile();

		if (StringUtils.isBlank(id) && StringUtils.isNotBlank(getPtql())) {
			log.debug("[App:{}] ptql = {}", context.getId(), getPtql());
			boolean findPid = true;
			if (StringUtils.isNotBlank(this.pidFromPtql)) {
				AppProcessTime processTime = context.getShell().getProcessTime(pidFromPtql);
				if (processTime != null && processTime.getTotal() >= procssFromPtqlTime) {
					findPid = false;
					id = this.pidFromPtql;
					this.procssFromPtqlTime = processTime.getTotal();
				}
			}

			if (findPid) {
				Long pid = context.getShell().getProcessId(getPtql());
				id = (pid != null ? String.valueOf(pid) : null);
				if (StringUtils.isNotBlank(id)) {
					AppProcessTime processTime = context.getShell().getProcessTime(id);
					if (processTime != null) {
						this.pidFromPtql = id;
						this.procssFromPtqlTime = processTime.getTotal();
					}
				}
			}
		}
		return id;
	}

	protected String getPidFromFile() {
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
