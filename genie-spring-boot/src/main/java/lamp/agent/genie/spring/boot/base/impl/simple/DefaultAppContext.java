package lamp.agent.genie.spring.boot.base.impl.simple;

import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcess;
import lamp.agent.genie.core.app.simple.runtime.process.exec.foreground.DefaultProcess;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultAppContext extends AbstractSimpleAppContext {

	private DefaultProcess process;

	public DefaultAppContext(LampContext lampContext, String id, SimpleAppContainer appContainer) {
		super(lampContext, id, appContainer);
	}

	@Override
	public void doCreateProcess() {
		this.process = new DefaultProcess(this);
	}

	@Override
	public void doTerminateProcess() {
		this.process.terminate();
		this.process = null;
	}

	@Override
	public AppProcess getProcess() {
		return this.process;
	}

}
