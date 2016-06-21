package lamp.agent.genie.core;

import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.shell.Shell;

import java.io.File;
import java.util.Map;

public interface SimpleAppInstanceContext extends AppInstanceContext {

	LampContext getLampContext();

	File getAppMetaInfoDirectory();

	AppInstanceSpec getAppInstanceSpec();

	AppInstanceSpec getParsedAppInstanceSpec();

	InstallSpec getInstallSpec();

	String getId();

	Shell getShell();

	File getStdOutFile();

	File getStdErrFile();

	AppProcess getProcess();

	AppInstanceStatus getStatus();

	AppInstanceStatus updateStatus(AppInstanceStatus status);

	AppInstanceStatus checkAndUpdateStatus();

	void createProcess();

	void terminateProcess();

	Map<String, Object> getParameters();

	String getValue(String value, Map<String, Object> parameters);


}
