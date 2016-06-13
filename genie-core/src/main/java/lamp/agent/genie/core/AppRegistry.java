package lamp.agent.genie.core;


import java.util.List;

public interface AppRegistry {

	void bind(String id, AppInstance appInstance);

	void unbind(String id);

	AppInstance lookup(String id);

	boolean exists(String id);

	List<AppInstance> list();

}
