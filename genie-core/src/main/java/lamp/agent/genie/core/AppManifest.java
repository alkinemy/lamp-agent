package lamp.agent.genie.core;

import lamp.agent.genie.core.runtime.process.AppProcessType;

import java.io.File;
import java.util.Map;


public interface AppManifest {

	String getId();
	String getName();

	String getType();
	String getVersion();

	Boolean getDeploy();
	AppProcessType getProcessType();
	long getCheckStatusInterval();

	String getStartCommandLine();
	long getStartTimeout();

	String getStopCommandLine();
	long getStopTimeout();

	boolean isAutoStart();
	boolean isAutoStop();

	Map<String, Object> getParameters();

	File getPidFile();

	File getHomeDirectory();

	File getWorkDirectory();

	File getLogDirectory();

}