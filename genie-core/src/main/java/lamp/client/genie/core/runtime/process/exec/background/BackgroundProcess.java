package lamp.client.genie.core.runtime.process.exec.background;

import lamp.client.genie.core.context.AppContext;
import lamp.client.genie.core.exception.CommandExecuteException;
import lamp.client.genie.core.runtime.process.AppProcessStatus;
import lombok.extern.slf4j.Slf4j;
import lamp.client.genie.core.runtime.process.exec.AbstractProcess;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Slf4j
public class BackgroundProcess extends AbstractProcess {


	public BackgroundProcess(AppContext context) {
		super(context);

		AppProcessStatus status = getStatus();
		if (AppProcessStatus.NOT_RUNNING.equals(status)) {
			doStart();
		}
	}

	@Override public void terminate() {
		doStop();
	}


	protected ExecuteWatchdog doStart() {
		String commandLine = getStartCommandLine();
		long timeout = getStartTimeout();
		log.info("[App] '{}' start : {}", getContext().getId(), commandLine);
		return executeCommandLine(commandLine, timeout);
	}

	protected void doStop() {
		String commandLine = getStopCommandLine();
		long timeout = getStopTimeout();
		log.info("[App] '{}' stopCmdLine : {}", getContext().getId(), commandLine);

		executeCommandLine(commandLine, timeout);
	}

	protected ExecuteWatchdog executeCommandLine(String command, long timeout) throws CommandExecuteException {
		ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
		try {
			CommandLine cmdLine = CommandLine.parse(command);

			DefaultExecutor executor = new DefaultExecutor();
			executor.setWorkingDirectory(getWorkingDirectory());
			File systemLogFile = getSystemLogFile();
			PumpStreamHandler streamHandler;
			if (systemLogFile != null) {
				streamHandler = new PumpStreamHandler(new BufferedOutputStream(new FileOutputStream(systemLogFile)));
			} else {
				streamHandler = new PumpStreamHandler();
			}
			executor.setStreamHandler(streamHandler);
			executor.setWatchdog(watchdog);

			executor.execute(cmdLine, new BackgroundProcessResultHandler(getContext(), command));
		} catch (Exception e) {
			throw new CommandExecuteException(command, e);
		}
		return watchdog;
	}


}
