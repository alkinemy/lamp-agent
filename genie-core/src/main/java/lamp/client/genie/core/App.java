package lamp.client.genie.core;


import lamp.client.genie.core.runtime.process.AppProcess;

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
