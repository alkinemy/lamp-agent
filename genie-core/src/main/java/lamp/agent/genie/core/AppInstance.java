package lamp.agent.genie.core;


import java.io.File;
import java.util.Date;

public interface AppInstance {

	String getId();

	AppInstanceSpec getSpec();

	AppInstanceContext getContext();

	AppInstanceStatus getStatus();

	AppInstanceStatus getCorrectStatus();

	void start();

	void stop();

	boolean isRunning();

	boolean monitored();

	File getStdOutFile();

	File getStdErrFile();

	Date getStartTime();

	Date getStopTime();

}
