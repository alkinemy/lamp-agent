package lamp.agent.genie.core.app.simple.runtime.process;


public interface AppProcess {

	String getId();

	AppProcessState getStatus();

	void terminate();


}
