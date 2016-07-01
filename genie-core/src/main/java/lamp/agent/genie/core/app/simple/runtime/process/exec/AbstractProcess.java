package lamp.agent.genie.core.app.simple.runtime.process.exec;

import lamp.agent.genie.core.LampCoreConstants;
import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lamp.agent.genie.core.app.simple.SimpleAppContext;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcess;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcessTime;
import lamp.agent.genie.core.exception.PidFileException;
import lamp.agent.genie.core.support.vm.JavaVirtualMachineTools;
import lamp.agent.genie.utils.CommandLineUtils;
import lamp.agent.genie.utils.FileUtils;
import lamp.agent.genie.utils.FilenameUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class AbstractProcess implements AppProcess {

	@Getter
	private final SimpleAppContext context;
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

	public AbstractProcess(SimpleAppContext context) {
		Objects.requireNonNull(context);
		this.context = context;
		init();
	}

	protected void init() {
		SimpleAppContainer parsedAppContainer = context.getParsedAppContainer();

		this.workDirectory = new File(parsedAppContainer.getWorkDirectory());
		this.pidFile = getFile(workDirectory, parsedAppContainer.getPidFile());

		this.stdOutFile = getFile(workDirectory, parsedAppContainer.getStdOutFile());
		this.stdErrFile = getFile(workDirectory, parsedAppContainer.getStdErrFile());

		this.ptql = parsedAppContainer.getPtql();

		this.startCommandLine = parsedAppContainer.getStartCommandLine();
		this.startTimeout = parsedAppContainer.getStartTimeout();
		this.stopCommandLine = parsedAppContainer.getStopCommandLine();
		this.stopTimeout = parsedAppContainer.getStopTimeout();

	}

	protected File getFile(File workDirectory, String filename) {
		if (StringUtils.isNotBlank(filename)) {
			String normalizedFilename = FilenameUtils.normalize(filename);
			if (FilenameUtils.getName(normalizedFilename).equals(normalizedFilename)) {
				return new File(workDirectory, normalizedFilename);
			} else {
				return new File(filename);
			}
		}
		return null;
	}

	protected CommandLine parseCommandLine(String command) {
		return CommandLineUtils.parse(getContext().getParsedAppContainer(), command);
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
		} else {
			Map<String, Object> parameters = getContext().getAppContainer().getParameters();
			if (parameters != null) {
				Object displayNameObject = parameters.get(LampCoreConstants.JVM_DISPLAY_NAME);
				if (displayNameObject != null) {
					String displayName = String.valueOf(displayNameObject);
					return JavaVirtualMachineTools.getPidByDisplayName(displayName);
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
