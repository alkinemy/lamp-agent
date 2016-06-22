package lamp.agent.genie.core.app.simple.runtime.process.exec.foreground;

import lamp.agent.genie.core.app.simple.SimpleAppContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;

@Slf4j
public class DefaultProcessResultHandler extends DefaultExecuteResultHandler {

	private final SimpleAppContext context;
	private final String command;

	public DefaultProcessResultHandler(SimpleAppContext context, String command) {
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
