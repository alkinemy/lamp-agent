package lamp.agent.genie.core.app.simple;

import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcessType;

import java.util.Map;


public interface SimpleAppSpec extends AppSpec {

	String getId();
	String getName();
	String getDescription();

	String getGroupId();
	String getArtifactId();
	String getVersion();

	AppProcessType getProcessType();
	long getCheckStatusInterval();
	boolean isPreInstalled();

	String getAppDirectory();
	String getWorkDirectory();
	String getLogDirectory();

	String getPidFile();
	String getPtql();
	String getStdOutFile();
	String getStdErrFile();

	String getCommandShell();

	String getStartCommandLine();
	long getStartTimeout();

	String getStopCommandLine();
	long getStopTimeout();

	boolean isMonitor();

	Map<String, Object> getParameters();

	long getLastModified();



}