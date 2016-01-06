package lamp.agent.genie.core.context;

import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.shell.Shell;

import java.io.File;
import java.util.Map;

public interface AppContext {

	AppManifest getAppManifest();

	String getId();

	Shell getShell();

	File getPidFile();

	AppStatus getStatus();

	AppStatus updateStatus(AppStatus status);

	AppStatus checkAndUpdateStatus();

	void createProcess();

	void terminateProcess();

	Map<String,Object> getParameters();

}
