package lamp.agent.genie.core;

import lamp.agent.genie.core.runtime.process.AppProcessType;

import java.util.Map;


public interface AppConfig {

	String getId();
	String getName();

	String getAppName();
	String getAppVersion();

	AppProcessType getProcessType();
	long getCheckStatusInterval();
	boolean isPreInstalled();

	String getAppDirectory();
	String getWorkDirectory();

	String getPidFile();
	String getLogFile();

	String getStartCommandLine();
	long getStartTimeout();

	String getStopCommandLine();
	long getStopTimeout();

	boolean isMonitor();

	Map<String, Object> getParameters();

	long getLastModified();



}