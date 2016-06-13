package lamp.agent.genie.spring.boot.base.impl;

import com.google.common.collect.ImmutableList;
import lamp.agent.genie.core.AppInstance;
import lamp.agent.genie.core.AppRegistry;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppRegistryImpl implements AppRegistry {

	private Map<String, AppInstance> appMap = Collections.synchronizedMap(new HashMap<String, AppInstance>());

	public void bind(String id, AppInstance appInstance) {
		appMap.put(id, appInstance);
	}

	public void unbind(String id) {
		appMap.remove(id);
	}

	public AppInstance lookup(String id) {
		AppInstance appInstance = appMap.get(id);
		Exceptions.throwsException(appInstance == null, ErrorCode.APP_INSTANCE_NOT_FOUND, id);
		return appInstance;
	}

	public boolean exists(String id) {
		AppInstance appInstance = appMap.get(id);
		return appInstance != null;
	}

	public List<AppInstance> list() {
		return ImmutableList.copyOf(appMap.values());
	}

}
