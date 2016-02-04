package lamp.agent.genie.core;


import java.io.File;
import java.util.Date;

public interface App {

	String getId();

	AppSpec getSpec();

	AppContext getContext();

	AppStatus getStatus();

	AppStatus getCorrectStatus();

	void start();

	void stop();

	boolean isRunning();

	boolean isMonitor();

	File getLogFile();

	Date getStartTime();

	Date getStopTime();

}
