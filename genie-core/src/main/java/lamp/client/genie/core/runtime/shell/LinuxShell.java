package lamp.client.genie.core.runtime.shell;

import lamp.client.genie.core.runtime.process.AppProcessStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;

import java.io.IOException;

@Slf4j
public class LinuxShell implements Shell {

	@Override public AppProcessStatus getProcessStatus(String pid) throws IOException {
		String line = "ps -p " + pid;
		CommandLine cmdLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		int exitValue;
		try {
			exitValue = executor.execute(cmdLine);
		} catch (ExecuteException e) {
			log.info("ps command execute : {}", line);
			log.info("ps command execute failed ", e);
			exitValue = e.getExitValue();
		}

		return executor.isFailure(exitValue) ? AppProcessStatus.NOT_RUNNING : AppProcessStatus.RUNNING;
	}

	@Override public boolean processSigterm(String pid) throws IOException {
		String line = "kill -TERM " + pid;
		CommandLine cmdLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		int exitValue;
		try {
			exitValue = executor.execute(cmdLine);
		} catch (ExecuteException e) {
			log.info("kill command execute : {}", line);
			log.info("kill command execute failed ", e);
			exitValue = e.getExitValue();
		}
		return !executor.isFailure(exitValue);
	}

}
