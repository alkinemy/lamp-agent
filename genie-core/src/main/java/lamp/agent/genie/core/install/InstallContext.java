package lamp.agent.genie.core.install;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.script.ScriptCommand;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface InstallContext {

	AppContext getAppContext();

	void transferTo(File dest) throws IOException;

	List<ScriptCommand> getCommands();

	File getInstallLogFile();

}
