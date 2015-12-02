package lamp.client.genie.core.runtime.process;


public interface AppProcess {

	String getId();

	AppProcessStatus getStatus();

	void terminate();

}
