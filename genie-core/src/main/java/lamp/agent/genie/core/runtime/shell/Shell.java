package lamp.agent.genie.core.runtime.shell;

import lamp.agent.genie.core.runtime.process.AppProcessState;

public interface Shell {

	AppProcessState getProcessState(String pid);

	void kill(String pid, Signal signal);

	void close();

	public static enum Signal {
		TERM, KILL
	}
}
