package lamp.agent.genie.core.script;

import lamp.agent.genie.core.AppContext;
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

	public SimpleCommandExecutionContext(AppContext appContext, OutputStream outputStream) {
		this(appContext, outputStream, outputStream);
	}

	public SimpleCommandExecutionContext(AppContext appContext, OutputStream outputStream, OutputStream errorStream) {
		this.appContext = appContext;
		this.outputStream = outputStream;
		this.errorStream = errorStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public OutputStream getErrorStream() {
		return errorStream;
	}

	@Override public void close() throws IOException {
		IOUtils.closeQuietly(outputStream);
		IOUtils.closeQuietly(errorStream);
	}
}
