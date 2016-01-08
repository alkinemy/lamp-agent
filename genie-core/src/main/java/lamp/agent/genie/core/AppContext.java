package lamp.agent.genie.core;

import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.core.runtime.shell.Shell;

import java.io.File;
import java.util.Map;

public interface AppContext {

	AppConfig getAppConfig();

	InstallConfig getInstallConfig();

	String getId();

	Shell getShell();

	File getSystemLogFile();

	<T> T getValue(T value, Object parameters);

	AppStatus getStatus();

	AppStatus updateStatus(AppStatus status);

	AppStatus checkAndUpdateStatus();

	void createProcess();

	void terminateProcess();

	Map<String,Object> getParameters();


}
