package lamp.agent.genie.core.runtime.process.exec.background;

import lamp.agent.genie.core.AppContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;

@Slf4j
public class BackgroundProcessResultHandler extends DefaultExecuteResultHandler {

	private final AppContext context;
	private final String command;

	public BackgroundProcessResultHandler(AppContext context, String command) {
		this.context = context;
		this.command = command;
	}

	@Override public void onProcessComplete(int exitValue) {
		super.onProcessComplete(exitValue);

		context.checkAndUpdateStatus();
	}

	@Override public void onProcessFailed(ExecuteException e) {
		super.onProcessFailed(e);

		log.warn("Execute Command failed : \"" + command + "\" ", e);

		context.checkAndUpdateStatus();
	}
}
