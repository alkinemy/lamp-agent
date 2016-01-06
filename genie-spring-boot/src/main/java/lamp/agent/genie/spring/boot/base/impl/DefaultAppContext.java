package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.context.LampContext;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.core.runtime.process.exec.background.DaemonProcess;
import lamp.agent.genie.core.runtime.process.exec.foreground.DefaultProcess;

public class DefaultAppContext extends AbstractAppContext {


	private DefaultProcess process;

	public DefaultAppContext(LampContext lampContext, AppManifest appManifest) {
		super(lampContext, appManifest);
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
