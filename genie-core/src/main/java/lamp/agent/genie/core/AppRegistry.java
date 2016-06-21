package lamp.agent.genie.core;


import java.util.List;

public interface AppRegistry {

	void bind(String id, AppInstance appInstance);

	void unbind(String id);

	SimpleAppInstance lookup(String id);

	boolean exists(String id);

	List<SimpleAppInstance> list();

}
