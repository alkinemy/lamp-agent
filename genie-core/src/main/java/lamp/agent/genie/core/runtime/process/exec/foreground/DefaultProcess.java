package lamp.agent.genie.core.runtime.process.exec.foreground;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.exception.CommandExecuteException;
import lamp.agent.genie.core.runtime.process.exec.AbstractProcess;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Slf4j
public class DefaultProcess extends AbstractProcess {

	private static final int EXIT_CODE_ZERO = 0;
	private static final int EXIT_CODE_SIGTERM = 143;
	private static final int[] EXIT_CODES = {EXIT_CODE_ZERO, EXIT_CODE_SIGTERM};

	private volatile ExecuteWatchdog executeWatchdog;

	public DefaultProcess(AppContext context) {
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
			File systemLogFile = getSystemLogFile();
			PumpStreamHandler streamHandler;
			if (systemLogFile != null) {
				streamHandler = new PumpStreamHandler(new BufferedOutputStream(new FileOutputStream(systemLogFile)));
			} else {
				streamHandler = new PumpStreamHandler();
			}
			executor.setStreamHandler(streamHandler);
			executor.setWatchdog(watchdog);
			executor.execute(cmdLine, executeResultHandler);
			return watchdog;
		} catch (Exception e) {
			throw new CommandExecuteException(command, e);
		}
	}


}
