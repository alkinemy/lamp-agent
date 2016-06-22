package lamp.agent.genie.core.app.simple;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcess;
import lamp.agent.genie.core.app.simple.runtime.shell.Shell;

import java.io.File;
import java.util.Map;

public interface SimpleAppContext extends AppContext {

	LampContext getLampContext();

	File getAppMetaInfoDirectory();

	SimpleAppContainer getAppContainer();

	SimpleAppContainer getParsedAppContainer();

	Shell getShell();

	void doCreateProcess();

	void doTerminateProcess();

	File getStdOutFile();

	File getStdErrFile();

	AppProcess getProcess();

	AppStatus getStatus();

	AppStatus updateStatus(AppStatus status);

	AppStatus checkAndUpdateStatus();

	Map<String, Object> getParameters();

	String getValue(String value, Map<String, Object> parameters);


}
