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

	File getStdOutFile();

	File getStdErrFile();

	Date getStartTime();

	Date getStopTime();

}
