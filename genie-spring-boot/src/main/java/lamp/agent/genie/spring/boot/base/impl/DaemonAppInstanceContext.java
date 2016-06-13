package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppInstanceSpec;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.exec.background.DaemonProcess;

public class DaemonAppInstanceContext extends AbstractAppInstanceContext {

	private DaemonProcess process;

	public DaemonAppInstanceContext(LampContext lampContext, AppInstanceSpec appInstanceSpec, InstallSpec installSpec) {
		super(lampContext, appInstanceSpec, installSpec);

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
