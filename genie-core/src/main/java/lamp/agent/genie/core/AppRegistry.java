package lamp.agent.genie.core;


import lamp.agent.genie.core.App;

import java.util.List;

public interface AppRegistry {

	void bind(String id, App app);

	void unbind(String id);

	App lookup(String id);

	boolean exists(String id);

	List<App> list();

}
