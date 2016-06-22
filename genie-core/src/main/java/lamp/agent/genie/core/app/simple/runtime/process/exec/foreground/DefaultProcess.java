package lamp.agent.genie.core.app.simple.runtime.process.exec.foreground;

import lamp.agent.genie.core.app.simple.SimpleAppContext;
import lamp.agent.genie.core.LampCoreConstants;
import lamp.agent.genie.core.script.exception.CommandExecuteException;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcessState;
import lamp.agent.genie.core.app.simple.runtime.process.exec.AbstractProcess;
import lamp.agent.genie.core.support.vm.JavaVirtualMachineTools;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;

import java.io.*;
import java.util.Map;

@Slf4j
public class DefaultProcess extends AbstractProcess {

	private static final int EXIT_CODE_ZERO = 0;
	private static final int EXIT_CODE_SIGTERM = 143;
	private static final int[] EXIT_CODES = {EXIT_CODE_ZERO, EXIT_CODE_SIGTERM};

	private volatile ExecuteWatchdog executeWatchdog;

	public DefaultProcess(SimpleAppContext context) {
		super(context);

		this.executeWatchdog = doStart();
	}

	@Override public void terminate() {
		doStop();
	}

	protected ExecuteWatchdog doStart() {
		String commandLine = getStartCommandLine();
		long timeout = ExecuteWatchdog.INFINITE_TIMEOUT;
		log.info("[App:{}] startCmdLine : {}", getContext().getId(), commandLine);

		return executeCommandLine(commandLine, timeout);
	}

	protected void doStop() {
		String commandLine = getStopCommandLine();
		long timeout = getStopTimeout();
		log.info("[App:{}] stopCommandLine : {}", getContext().getId(), commandLine);
		if (StringUtils.isNotBlank(commandLine)) {
			this.executeWatchdog = executeCommandLine(commandLine, timeout);
		} else {
			this.executeWatchdog.destroyProcess();
		}
	}

	protected ExecuteWatchdog executeCommandLine(String command, long timeout) throws CommandExecuteException {
		try {
			CommandLine cmdLine = parseCommandLine(command);

			ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
			ExecuteResultHandler executeResultHandler = new DefaultProcessResultHandler(getContext(), command);
			DaemonExecutor executor = new DaemonExecutor();
			executor.setExitValues(EXIT_CODES);
			executor.setWorkingDirectory(getWorkDirectory());
			File stdOutFile = getStdOutFile();
			File stdErrFile = getStdErrFile();
			// FIXME Rolling 기능같은게 필요하다.
			PumpStreamHandler streamHandler;
			if (stdOutFile != null && stdOutFile.equals(stdErrFile)) {
				streamHandler = new PumpStreamHandler(getOutputStream(stdOutFile));
			} else {
				streamHandler = new PumpStreamHandler(getOutputStream(stdOutFile), getOutputStream(stdErrFile));
			}
			executor.setStreamHandler(streamHandler);
			executor.setWatchdog(watchdog);
			executor.execute(cmdLine, executeResultHandler);
			return watchdog;
		} catch (Exception e) {
			throw new CommandExecuteException(e, command);
		}
	}

	protected OutputStream getOutputStream(File file) throws FileNotFoundException {
		if (file == null) {
			return null;
		}
		return new BufferedOutputStream(new FileOutputStream(file));
	}

	@Override
	public String getId() {
		String id = super.getId();
		if (id == null) {
			Map<String, Object> parameters = getContext().getAppContainer().getParameters();
			if (parameters != null) {
				Object displayNameObject = parameters.get(LampCoreConstants.JVM_DISPLAY_NAME);
				if (displayNameObject != null) {
					String displayName = String.valueOf(displayNameObject);
					return JavaVirtualMachineTools.getPidByDisplayName(displayName);
				}
			}
		}
		return null;
	}

	@Override
	public AppProcessState getStatus() {
		ExecuteWatchdog executeWatchdog = this.executeWatchdog;
		if (executeWatchdog != null && !executeWatchdog.killedProcess()) {
			return AppProcessState.RUNNING;
		} else {
			return AppProcessState.NOT_RUNNING;
		}
	}

}
