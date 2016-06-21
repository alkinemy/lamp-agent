package lamp.agent.genie.core.script;

import lamp.agent.genie.core.SimpleAppInstanceContext;
import lamp.agent.genie.utils.ExpressionParser;

import java.io.Closeable;
import java.io.OutputStream;

public interface CommandExecutionContext extends Closeable {

	SimpleAppInstanceContext getAppInstanceContext();

	OutputStream getOutputStream();

	OutputStream getErrorStream();

	ExpressionParser getExpressionParser();

}
