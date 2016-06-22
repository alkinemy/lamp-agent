package lamp.agent.genie.spring.boot.base.impl;

import com.google.common.collect.ImmutableList;
import lamp.agent.genie.core.App;
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

	private Map<String, App> appMap = Collections.synchronizedMap(new HashMap<String, App>());

	public void bind(String id, App appInstance) {
		appMap.put(id, appInstance);
	}

	public void unbind(String id) {
		appMap.remove(id);
	}

	public App lookup(String id) {
		App appInstance = appMap.get(id);
		Exceptions.throwsException(appInstance == null, ErrorCode.APP_NOT_FOUND, id);
		return appInstance;
	}

	public boolean exists(String id) {
		App appInstance = appMap.get(id);
		return appInstance != null;
	}

	public List<App> list() {
		return ImmutableList.copyOf(appMap.values());
	}

}
