package lamp.agent.genie.core;


import lamp.agent.genie.core.app.AppContainer;

import java.util.Map;

public interface App {

	String getId();
	String getName();
	String getDescription();

	AppStatus getCorrectStatus();
	AppStatus getStatus();

	AppContainer getAppContainer();
	Map<String, Object> getParameters();

	AppContext getAppContext();

	void start();

	void stop();

	boolean isRunning();

}
