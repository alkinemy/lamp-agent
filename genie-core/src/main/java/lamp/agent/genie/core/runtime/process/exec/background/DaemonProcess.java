package lamp.agent.genie.core.runtime.process.exec.background;

import lamp.agent.genie.core.SimpleAppInstanceContext;
import lamp.agent.genie.core.script.exception.CommandExecuteException;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.core.runtime.process.exec.AbstractProcess;
import lamp.agent.genie.core.runtime.shell.Shell;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

@Slf4j
public class DaemonProcess extends AbstractProcess {


	public DaemonProcess(SimpleAppInstanceContext context) {
		super(context);
	}

	public void start() {
		doStart();
	}

	@Override public void terminate() {
		doStop();
	}


	protected ExecuteWatchdog doStart() {
		String commandLine = getStartCommandLine();
		long timeout = getStartTimeout();
		log.info("[App:{}] startCmdLine = {}", getContext().getId(), commandLine);

		return executeCommandLine(commandLine, timeout);
	}

	protected void doStop() {
		String commandLine = getStopCommandLine();
		log.info("[App:{}] stopCommandLine = {}", getContext().getId(), commandLine);
		if (StringUtils.isNotBlank(commandLine)) {
			long timeout = getStopTimeout();
			executeCommandLine(commandLine, timeout);
		} else {
			String pid = getId();
			getContext().getShell().kill(pid, Shell.Signal.TERM);
		}
	}

	protected ExecuteWatchdog executeCommandLine(String command, long timeout) throws CommandExecuteException {
		ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
		try {
			CommandLine cmdLine = parseCommandLine(command);

			DefaultExecutor executor = new DefaultExecutor();
			log.info("[App:{}] workDirectory = {}", getContext().getId(), getWorkDirectory().getAbsolutePath());
			executor.setWorkingDirectory(getWorkDirectory());
			PumpStreamHandler streamHandler = new PumpStreamHandler();
			executor.setStreamHandler(streamHandler);
			executor.setWatchdog(watchdog);
			executor.execute(cmdLine, new DaemonProcessResultHandler(getContext(), command));
		} catch (Exception e) {
			throw new CommandExecuteException(e, command);
		}
		return watchdog;
	}

	@Override public AppProcessState getStatus() {
		String pid = getId();
		if (StringUtils.isBlank(pid)) {
			return AppProcessState.NOT_RUNNING;
		}
		return getContext().getShell().getProcessState(pid);
	}

}
