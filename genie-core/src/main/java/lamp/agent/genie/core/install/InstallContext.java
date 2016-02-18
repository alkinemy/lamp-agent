package lamp.agent.genie.core.install;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.command.Command;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface InstallContext {

	AppContext getAppContext();

	void transferTo(File dest) throws IOException;

	List<Command> getCommands();
}
