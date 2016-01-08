package lamp.agent.genie.core;

import lamp.agent.genie.core.runtime.process.AppProcessType;

import java.io.File;
import java.util.Map;


public interface AppConfig {

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

	boolean isPreInstalled();

	String getPidFile();
	String getLogFile();
	String getHomeDirectory();
	String getWorkDirectory();

	Map<String, Object> getParameters();

	long getLastModified();



}