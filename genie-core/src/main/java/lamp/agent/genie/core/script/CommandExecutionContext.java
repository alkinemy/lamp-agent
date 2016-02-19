package lamp.agent.genie.core.script;

import lamp.agent.genie.core.AppContext;

import java.io.Closeable;
import java.io.OutputStream;

public interface CommandExecutionContext extends Closeable {

	AppContext getAppContext();


	OutputStream getOutputStream();

	OutputStream getErrorStream();

}
