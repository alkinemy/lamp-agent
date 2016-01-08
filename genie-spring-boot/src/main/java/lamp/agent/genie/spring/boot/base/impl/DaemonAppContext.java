package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.exec.background.DaemonProcess;

public class DaemonAppContext extends AbstractAppContext {

	private DaemonProcess process;

	public DaemonAppContext(LampContext lampContext, AppConfig appConfig, InstallConfig installConfig) {
		super(lampContext, appConfig, installConfig);

		this.process = new DaemonProcess(this);
	}


	@Override
	public void createProcess() {
		process.start();
	}

	@Override
	public void terminateProcess() {
		process.terminate();
	}

	@Override
	public AppProcess getProcess() {
		return process;
	}



}
