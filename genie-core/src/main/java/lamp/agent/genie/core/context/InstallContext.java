package lamp.agent.genie.core.context;

import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.deploy.InstallManifest;

import java.io.File;
import java.io.IOException;

public interface InstallContext {

	InstallManifest getDeployManifest();

	AppManifest getAppManifest();

	void transferTo(File dest) throws IOException;

}
