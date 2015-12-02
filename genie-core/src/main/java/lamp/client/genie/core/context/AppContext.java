package lamp.client.genie.core.context;

import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.AppStatus;
import lamp.client.genie.core.runtime.process.AppProcess;
import lamp.client.genie.core.runtime.shell.Shell;

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

	AppProcess createProcess();

	Map<String,Object> getParameters();

}
