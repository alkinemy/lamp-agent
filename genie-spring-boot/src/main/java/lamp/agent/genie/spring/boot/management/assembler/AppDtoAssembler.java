package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.app.AppContainer;
import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lamp.agent.genie.core.app.simple.SimpleAppContext;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcess;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.management.model.AppDto;
import org.springframework.stereotype.Component;

@Component
public class AppDtoAssembler extends AbstractListAssembler<App, AppDto> {

	@Override protected AppDto doAssemble(App app) {
		AppContainer appContainer = app.getAppContainer();
		AppContext appContext = app.getAppContext();

		AppDto appDto = new AppDto();
		appDto.setId(app.getId());
		appDto.setName(app.getName());
		appDto.setDescription(app.getDescription());

		appDto.setStatus(app.getStatus());
//		appDto.setMonitored(app.monitored());

		if (appContainer instanceof SimpleAppContainer) {
			appDto.setProcessType(((SimpleAppContainer) appContainer).getProcessType());
			AppProcess appProcess = ((SimpleAppContext) appContext).getProcess();
			if (appProcess != null) {
				appDto.setPid(appProcess.getId());
			}
		}


		return appDto;
	}

}
