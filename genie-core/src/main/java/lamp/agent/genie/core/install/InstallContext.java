package lamp.agent.genie.core.install;

import lamp.agent.genie.core.AppContext;

import java.io.File;
import java.io.IOException;

public interface InstallContext {

	AppContext getAppContext();

	void transferTo(File dest) throws IOException;

}
