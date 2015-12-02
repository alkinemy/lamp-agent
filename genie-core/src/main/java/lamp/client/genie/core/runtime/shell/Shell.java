package lamp.client.genie.core.runtime.shell;

import lamp.client.genie.core.runtime.process.AppProcessStatus;

import java.io.IOException;

public interface Shell {
	AppProcessStatus getProcessStatus(String pid) throws IOException;

	boolean processSigterm(String pid) throws IOException;
}
