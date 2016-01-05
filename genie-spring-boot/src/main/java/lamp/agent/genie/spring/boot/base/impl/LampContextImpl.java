package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.spring.boot.config.LampAgentProperties;
import lamp.agent.genie.core.context.LampContext;
import lamp.agent.genie.core.context.MountPoint;
import lamp.agent.genie.core.runtime.shell.Shell;
import lamp.agent.genie.core.runtime.shell.SigarShell;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class LampContextImpl implements LampContext {

	private static final String APP_DIRECTORY = "apps";
	private static final String LOG_DIRECTORY = "logs";

	private final Shell shell;
	private LampAgentProperties lampClientProperties;


	public LampContextImpl(LampAgentProperties lampClientProperties) {
		this.lampClientProperties = lampClientProperties;
		this.shell = new SigarShell();
	}

	public void close() {
		shell.close();
	}

	@Override public String getHostname() {
		return lampClientProperties.getHostname();
	}

	@Override public String getAddress() {
		return lampClientProperties.getAddress();
	}

	@Override public MountPoint getMountPoint() {
		return MountPoint.fromPath(lampClientProperties.getMountPointPath());
	}

	@Override public File getAppDirectory() {
		return getMountPoint().getDirectory(APP_DIRECTORY, true);
	}

	@Override public File getAppDirectory(String id) {
		return new File(getAppDirectory(), id);
	}

	@Override public File getLogDirectory() {
		return getMountPoint().getDirectory(LOG_DIRECTORY, true);
	}

	@Override public Shell getShell() {
		return shell;
	}

}
