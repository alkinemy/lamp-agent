package lamp.agent.genie.core.runtime.shell;

import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.core.runtime.process.AppProcessTime;

public interface Shell {

	AppProcessState getProcessState(String pid);

	Long getProcessId(String ptql);

	AppProcessTime getProcessTime(String pid);

	void kill(String pid, Signal signal);

	void close();

	public static enum Signal {
		TERM, KILL
	}
}
