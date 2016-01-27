package lamp.agent.genie.core;

import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.shell.Shell;

import java.io.File;
import java.util.Map;

public interface AppContext {

	AppSpec getAppSpec();

	AppSpec getParsedAppSpec();

	InstallSpec getInstallSpec();

	String getId();

	Shell getShell();

	File getSystemLogFile();

	AppProcess getProcess();

	AppStatus getStatus();

	AppStatus updateStatus(AppStatus status);

	AppStatus checkAndUpdateStatus();

	void createProcess();

	void terminateProcess();

	Map<String,Object> getParameters();


}
