package lamp.agent.genie.core.runtime.process;


public interface AppProcess {

	String getId();

	AppProcessState getStatus();

	void terminate();

	void refresh();

}
