package lamp.agent.genie.docker;

import lamp.agent.genie.core.SimpleAppInstanceContext;
import lamp.agent.genie.core.AppInstanceSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.utils.FilenameUtils;
import lombok.Getter;

import java.io.File;
import java.util.Objects;

public class DockerAppProcess implements AppProcess {

	@Getter
	private final SimpleAppInstanceContext context;

	public DockerAppProcess(SimpleAppInstanceContext context) {
		Objects.requireNonNull(context);
		this.context = context;
		init();
	}

	protected void init() {
		AppInstanceSpec appInstanceSpec = context.getAppInstanceSpec();
		AppInstanceSpec parsedAppInstanceSpec = context.getParsedAppInstanceSpec();

		this.stdOutFile = context.getStdOutFile();
		this.stdErrFile = context.getStdErrFile();

		this.workDirectory = new File(parsedAppInstanceSpec.getWorkDirectory());
		String pidFilePath = FilenameUtils.normalize(parsedAppInstanceSpec.getPidFile());
		if (FilenameUtils.getName(pidFilePath).equals(pidFilePath)) {
			this.pidFile = new File(this.workDirectory, pidFilePath);
		} else {
			this.pidFile = new File(parsedAppInstanceSpec.getPidFile());
		}

		this.ptql = parsedAppInstanceSpec.getPtql();

		this.startCommandLine = parsedAppInstanceSpec.getStartCommandLine();
		this.startTimeout = parsedAppInstanceSpec.getStartTimeout();
		this.stopCommandLine = parsedAppInstanceSpec.getStopCommandLine();
		this.stopTimeout = parsedAppInstanceSpec.getStopTimeout();

		this.lastModified = appInstanceSpec.getLastModified();
	}

	@Override public String getId() {
		return null;
	}

	@Override public AppProcessState getStatus() {
		return null;
	}

	@Override public void terminate() {

	}

	@Override public void refresh() {

	}
}
