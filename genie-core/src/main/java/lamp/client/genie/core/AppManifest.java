package lamp.client.genie.core;

import lamp.client.genie.core.runtime.process.AppProcessType;

import java.io.File;
import java.util.Map;


public interface AppManifest {

	String getId();
	String getName();

	String getType();
	String getVersion();

	Boolean getDeploy();
	AppProcessType getProcessType();
	Long getCheckStatusInterval();

	String getStartCommandLine();
	Long getStartTimeout();

	String getStopCommandLine();
	Long getStopTimeout();

	Boolean getAutoStart();
	Boolean getAutoStop();

	Map<String, Object> getParameters();

	File getPidFile();

	File getHomeDirectory();

	File getWorkDirectory();

	File getLogDirectory();

}