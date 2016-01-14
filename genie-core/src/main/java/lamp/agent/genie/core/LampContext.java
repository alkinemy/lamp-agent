package lamp.agent.genie.core;

import lamp.agent.genie.core.runtime.shell.Shell;

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
