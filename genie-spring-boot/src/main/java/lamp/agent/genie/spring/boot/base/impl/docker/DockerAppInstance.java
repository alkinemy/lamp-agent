package lamp.agent.genie.spring.boot.base.impl.docker;

import lamp.agent.genie.core.AppInstance;
import lamp.agent.genie.core.AppInstanceContext;
import lamp.agent.genie.core.AppInstanceStatus;
import lamp.agent.genie.spring.boot.management.model.DockerAppContainer;

import java.io.File;
import java.util.Date;

public class DockerAppInstance implements AppInstance {

	private DockerAppContainer container;

	public DockerAppInstance(DockerAppContainer container) {
		this.container = container;
	}

	@Override public String getId() {
		return null;
	}

	@Override public DockerAppC getSpec() {
		return null;
	}

	@Override public AppInstanceContext getContext() {
		return null;
	}

	@Override public AppInstanceStatus getStatus() {
		return null;
	}

	@Override public AppInstanceStatus getCorrectStatus() {
		return null;
	}

	@Override public void start() {

	}

	@Override public void stop() {

	}

	@Override public boolean isRunning() {
		return false;
	}

	@Override public boolean monitored() {
		return false;
	}

	@Override public File getStdOutFile() {
		return null;
	}

	@Override public File getStdErrFile() {
		return null;
	}

	@Override public Date getStartTime() {
		return null;
	}

	@Override public Date getStopTime() {
		return null;
	}
}
