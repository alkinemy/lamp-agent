package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.core.runtime.process.exec.foreground.DefaultProcess;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultAppContext extends AbstractAppContext {

	private DefaultProcess process;

	public DefaultAppContext(LampContext lampContext, AppSpec appSpec, InstallSpec installSpec) {
		super(lampContext, appSpec, installSpec);
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
		return this.process;
	}

}
