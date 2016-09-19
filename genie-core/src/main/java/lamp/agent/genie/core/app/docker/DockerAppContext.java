package lamp.agent.genie.core.app.docker;

import lamp.agent.genie.core.AppContext;

public interface DockerAppContext extends AppContext {

	String getContainerId();

	String getImageId();

}
