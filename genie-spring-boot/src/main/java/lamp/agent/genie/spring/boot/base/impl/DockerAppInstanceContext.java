package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppInstanceSpec;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.exec.background.DaemonProcess;

public class DockerAppInstanceContext extends AbstractAppInstanceContext {

	private DaemonProcess process;

	public DockerAppInstanceContext(LampContext lampContext, AppInstanceSpec appInstanceSpec) {
		super(lampContext, appInstanceSpec);

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
