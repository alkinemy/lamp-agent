package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.core.runtime.process.exec.background.DaemonProcess;

public class DaemonAppContext extends AbstractAppContext {

	private DaemonProcess process;

	public DaemonAppContext(LampContext lampContext, AppSpec appSpec, InstallSpec installSpec) {
		super(lampContext, appSpec, installSpec);

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

	@Override
	public AppProcessState getProcessStatus() {
		AppProcess process = getProcess();
		return process != null ? process.getStatus() : AppProcessState.UNKNOWN;
	}

}
