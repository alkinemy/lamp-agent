package lamp.agent.genie.core;

import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.shell.Shell;

import java.io.File;
import java.util.Map;

public interface AppContext {

	AppConfig getAppConfig();

	AppConfig getParsedAppConfig();

	InstallConfig getInstallConfig();

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
