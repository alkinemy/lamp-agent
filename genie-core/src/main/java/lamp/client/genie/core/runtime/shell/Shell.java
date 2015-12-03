package lamp.client.genie.core.runtime.shell;

import lamp.client.genie.core.runtime.process.AppProcessState;

import java.io.IOException;

public interface Shell {

	AppProcessState getProcessState(String pid);

	void kill(String pid, Signal signal);

	void close();

	public static enum Signal {
		TERM, KILL
	}
}
