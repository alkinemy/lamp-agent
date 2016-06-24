package lamp.agent.genie.spring.boot.management.service;

import com.github.dockerjava.api.DockerClient;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.app.AppContainer;
import lamp.agent.genie.core.app.docker.DockerAppContainer;
import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcessType;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.base.impl.docker.DockerAppContextImpl;
import lamp.agent.genie.spring.boot.base.impl.simple.DaemonAppContext;
import lamp.agent.genie.spring.boot.base.impl.simple.DefaultAppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppContextService {

    @Autowired
    private LampContext lampContext;

    @Autowired(required = false)
    private DockerClientService dockerClientService;

    public AppContext createAppContext(AppContainer appContainer) {
        AppContext appContext = null;
        if (appContainer instanceof SimpleAppContainer) {
            AppProcessType processType = ((SimpleAppContainer) appContainer).getProcessType();
            if (AppProcessType.DAEMON.equals(processType)) {
                appContext = new DaemonAppContext(lampContext, (SimpleAppContainer) appContainer);
            } else {
                appContext = new DefaultAppContext(lampContext, (SimpleAppContainer) appContainer);
            }
        } else if (appContainer instanceof DockerAppContainer) {
            Exceptions.throwsException(dockerClientService == null, ErrorCode.DOCKER_NOT_SUPPORTED);

            DockerClient dockerClient = dockerClientService.getDockerClient();
            appContext = new DockerAppContextImpl(lampContext, (DockerAppContainer) appContainer, dockerClient);
        }
        return appContext;

    }

}
