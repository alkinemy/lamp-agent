package lamp.agent.genie.core.script;

import lamp.agent.genie.core.AppInstanceContext;
import lamp.agent.genie.utils.ExpressionParser;
import lamp.agent.genie.utils.IOUtils;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleCommandExecutionContext implements CommandExecutionContext {

	@Getter
	@NonNull
	private final AppInstanceContext appInstanceContext;

	private final OutputStream outputStream;
	private final OutputStream errorStream;

	private final ExpressionParser expressionParser;

	public SimpleCommandExecutionContext(AppInstanceContext appInstanceContext, OutputStream outputStream, ExpressionParser expressionParser) {
		this(appInstanceContext, outputStream, outputStream, expressionParser);
	}

	public SimpleCommandExecutionContext(AppInstanceContext appInstanceContext, OutputStream outputStream, OutputStream errorStream, ExpressionParser expressionParser) {
		this.appInstanceContext = appInstanceContext;
		this.outputStream = outputStream;
		this.errorStream = errorStream;
		this.expressionParser = expressionParser;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public OutputStream getErrorStream() {
		return errorStream;
	}

	public ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	@Override public void close() throws IOException {
		IOUtils.closeQuietly(outputStream);
		IOUtils.closeQuietly(errorStream);
	}
}
