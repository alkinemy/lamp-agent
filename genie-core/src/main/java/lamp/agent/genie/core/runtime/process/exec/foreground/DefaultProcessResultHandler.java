package lamp.agent.genie.core.runtime.process.exec.foreground;

import lamp.agent.genie.core.SimpleAppInstanceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;

@Slf4j
public class DefaultProcessResultHandler extends DefaultExecuteResultHandler {

	private final SimpleAppInstanceContext context;
	private final String command;

	public DefaultProcessResultHandler(SimpleAppInstanceContext context, String command) {
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
