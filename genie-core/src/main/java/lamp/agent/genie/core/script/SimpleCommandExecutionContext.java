package lamp.agent.genie.core.script;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.utils.ExpressionParser;
import lamp.agent.genie.utils.IOUtils;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleCommandExecutionContext implements CommandExecutionContext {

	@Getter
	@NonNull
	private final AppContext appContext;

	private final OutputStream outputStream;
	private final OutputStream errorStream;

	private final ExpressionParser expressionParser;

	public SimpleCommandExecutionContext(AppContext appContext, OutputStream outputStream, ExpressionParser expressionParser) {
		this(appContext, outputStream, outputStream, expressionParser);
	}

	public SimpleCommandExecutionContext(AppContext appContext, OutputStream outputStream, OutputStream errorStream, ExpressionParser expressionParser) {
		this.appContext = appContext;
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
