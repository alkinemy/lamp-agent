package lamp.agent.genie.core;

import lamp.agent.genie.core.app.simple.runtime.shell.Shell;

import java.io.File;

public interface LampContext {

	String getHostname();

	String getAddress();

	MountPoint getMountPoint();

	File getAppDirectory();

	File getAppMetaInfoDirectory(String id);

	File getLogDirectory();

	Shell getShell();

}
