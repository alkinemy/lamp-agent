package lamp.agent.genie.core;

import lamp.agent.genie.core.runtime.process.AppProcessType;

import java.io.File;
import java.util.Map;


public interface AppConfig {

	String getId();
	String getName();

	String getAppName();
	String getAppVersion();

	AppProcessType getProcessType();
	long getCheckStatusInterval();
	boolean isPreInstalled();

	String getHomeDirectory();
	String getWorkDirectory();

	String getPidFile();
	String getLogFile();

	String getStartCommandLine();
	long getStartTimeout();

	String getStopCommandLine();
	long getStopTimeout();

	boolean isAutoStart();
	boolean isAutoStop();

	Map<String, Object> getParameters();

	long getLastModified();



}