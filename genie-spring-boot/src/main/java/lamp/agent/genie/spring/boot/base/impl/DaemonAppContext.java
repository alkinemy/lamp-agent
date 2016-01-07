package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.context.LampContext;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.exec.background.DaemonProcess;

public class DaemonAppContext extends AbstractAppContext {

	private DaemonProcess process;

	public DaemonAppContext(LampContext lampContext, AppManifest appManifest) {
		super(lampContext, appManifest);

		this.process = new DaemonProcess(this);

//		AppProcessState status = this.process.getStatus();
//		if (AppProcessState.NOT_RUNNING.equals(status)) {
//			createProcess();
//		}
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
