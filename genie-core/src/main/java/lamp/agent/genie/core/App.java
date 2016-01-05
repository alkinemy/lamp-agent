package lamp.agent.genie.core;


import lamp.agent.genie.core.runtime.process.AppProcess;

import java.util.Date;

public interface App {

	String getId();

	AppManifest getManifest();

	AppStatus getStatus();

	AppProcess getProcess();

	void start();

	void stop();

	boolean isRunning();

	Date getStartTime();

	Date getStopTime();

}
