package lamp.agent.genie.core;

import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.shell.Shell;

import java.io.File;
import java.util.Map;

public interface AppContext {

	LampContext getLampContext();

	File getAppMetaInfoDirectory();

	AppSpec getAppSpec();

	AppSpec getParsedAppSpec();

	InstallSpec getInstallSpec();

	String getId();

	Shell getShell();

	File getStdOutFile();

	File getStdErrFile();

	AppProcess getProcess();

	AppStatus getStatus();

	AppStatus updateStatus(AppStatus status);

	AppStatus checkAndUpdateStatus();

	void createProcess();

	void terminateProcess();

	Map<String, Object> getParameters();

	String getValue(String value, Map<String, Object> parameters);


}
