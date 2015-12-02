package lamp.client.genie.core.context;

import lamp.client.genie.core.runtime.shell.Shell;

import java.io.File;

public interface LampContext {

	String getHostname();

	String getAddress();

	MountPoint getMountPoint();

	File getAppDirectory();

	File getAppDirectory(String id);

	File getLogDirectory();

	Shell getShell();

}
