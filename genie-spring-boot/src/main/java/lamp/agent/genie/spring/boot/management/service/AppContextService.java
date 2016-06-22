package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.app.AppContainer;
import lamp.agent.genie.core.app.docker.DockerAppContainer;
import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcessType;
import lamp.agent.genie.spring.boot.base.impl.simple.DaemonAppContext;
import lamp.agent.genie.spring.boot.base.impl.simple.DefaultAppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppContextService {

    @Autowired
    private LampContext lampContext;

    public AppContext createAppContext(String id, AppContainer appContainer) {
        AppContext appContext = null;
        if (appContainer instanceof SimpleAppContainer) {
            AppProcessType processType = ((SimpleAppContainer) appContainer).getProcessType();
            if (AppProcessType.DAEMON.equals(processType)) {
                appContext = new DaemonAppContext(lampContext, id, (SimpleAppContainer) appContainer);
            } else {
                appContext = new DefaultAppContext(lampContext, id, (SimpleAppContainer) appContainer);
            }
        } else if (appContainer instanceof DockerAppContainer) {

        }
        return appContext;

    }

}
