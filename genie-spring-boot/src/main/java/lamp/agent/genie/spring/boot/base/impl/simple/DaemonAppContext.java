package lamp.agent.genie.spring.boot.base.impl.simple;

import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcess;
import lamp.agent.genie.core.app.simple.runtime.process.exec.background.DaemonProcess;

public class DaemonAppContext extends AbstractSimpleAppContext {

	private DaemonProcess process;

	public DaemonAppContext(LampContext lampContext, SimpleAppContainer appContainer) {
		super(lampContext, appContainer);

		this.process = new DaemonProcess(this);
	}

	@Override
	public void doCreateProcess() {
		process.start();
	}

	@Override
	public void doTerminateProcess() {
		process.terminate();
	}

	@Override
	public AppProcess getProcess() {
		return process;
	}

}
