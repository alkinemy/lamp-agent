package lamp.client.genie.core.context;

import lamp.client.genie.core.deploy.DeployManifest;
import lamp.client.genie.core.AppManifest;

import java.io.File;
import java.io.IOException;

public interface DeployContext {

	DeployManifest getDeployManifest();

	AppManifest getAppManifest();

	void transferTo(File dest) throws IOException;

}
