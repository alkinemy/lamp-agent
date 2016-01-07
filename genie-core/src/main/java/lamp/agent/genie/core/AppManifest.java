package lamp.agent.genie.core;

import lamp.agent.genie.core.runtime.process.AppProcessType;

import java.io.File;
import java.util.Map;


public interface AppManifest {

	String getId();
	String getName();

	String getType();
	String getVersion();

	AppProcessType getProcessType();
	long getCheckStatusInterval();

	String getStartCommandLine();
	long getStartTimeout();

	String getStopCommandLine();
	long getStopTimeout();

	boolean isAutoStart();
	boolean isAutoStop();

	String getFilename();
	boolean isPreInstalled();


	Map<String, Object> getParameters();

	File getPidFile();

	File getHomeDirectory();

	File getWorkDirectory();

	File getLogDirectory();

}