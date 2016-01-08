package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.exec.foreground.DefaultProcess;

public class DefaultAppContext extends AbstractAppContext {


	private DefaultProcess process;

	public DefaultAppContext(LampContext lampContext, AppConfig appConfig, InstallConfig installConfig) {
		super(lampContext, appConfig, installConfig);
	}


	@Override
	public void createProcess() {
		this.process = new DefaultProcess(this);
	}

	@Override
	public void terminateProcess() {
		this.process.terminate();
		this.process = null;
	}

	@Override
	public AppProcess getProcess() {
		return process;
	}



}
