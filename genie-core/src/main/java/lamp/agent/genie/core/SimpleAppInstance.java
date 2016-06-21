package lamp.agent.genie.core;

import java.io.File;
import java.util.Date;

public interface SimpleAppInstance extends AppInstance<SimpleAppInstanceContext> {

	String getId();

	AppInstanceSpec getSpec();

	SimpleAppInstanceContext getContext();

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
