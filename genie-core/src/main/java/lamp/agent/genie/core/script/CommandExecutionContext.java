package lamp.agent.genie.core.script;

import lamp.agent.genie.core.AppInstanceContext;
import lamp.agent.genie.utils.ExpressionParser;

import java.io.Closeable;
import java.io.OutputStream;

public interface CommandExecutionContext extends Closeable {

	AppInstanceContext getAppInstanceContext();

	OutputStream getOutputStream();

	OutputStream getErrorStream();

	ExpressionParser getExpressionParser();

}
