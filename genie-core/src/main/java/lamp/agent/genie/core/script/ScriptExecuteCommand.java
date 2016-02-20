package lamp.agent.genie.core.script;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.script.exception.CommandExecuteException;
import lamp.agent.genie.utils.CommandLineUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.File;

@Slf4j
@Getter
@Setter
public class ScriptExecuteCommand extends AbstractScriptCommand {

	private String commandShell;

	private String commandLine;

	private Long timeout;

	@Override
	public void execute(CommandExecutionContext context) {
		AppContext appContext = context.getAppContext();
		String commandLine = context.getAppContext().getValue(getCommandLine(), appContext.getParameters());
		log.info("Execute : {}", commandLine);

		AppSpec appSpec = context.getAppContext().getParsedAppSpec();
		String appDirectory = appSpec.getAppDirectory();
		log.info("appDirectory = {}", appDirectory);

		try {
			CommandLine cmdLine = CommandLineUtils.parse(appSpec, commandLine);
			DaemonExecutor executor = new DaemonExecutor();
			executor.setWorkingDirectory(new File(appDirectory));
			if (timeout != null) {
				ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
				executor.setWatchdog(watchdog);
			}

			PumpStreamHandler streamHandler = new PumpStreamHandler(context.getOutputStream(), context.getErrorStream());
			executor.setStreamHandler(streamHandler);
			executor.execute(cmdLine);
		} catch (Exception e) {
			throw new CommandExecuteException(e, commandLine);
		}
	}


}
