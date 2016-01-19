package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.management.model.AppDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AppDtoAssembler extends AbstractListAssembler<App, AppDto> {

	@Override protected AppDto doAssemble(App app) {
		AppConfig appConfig = app.getConfig();
		AppContext appContext = app.getContext();

		AppDto appDto = new AppDto();
		appDto.setId(app.getId());
		appDto.setName(appConfig.getName());
		appDto.setDescription(appConfig.getDescription());

		appDto.setAppId(appConfig.getAppId());
		appDto.setAppName(appConfig.getAppName());
		appDto.setAppVersion(appConfig.getAppVersion());

		appDto.setStatus(app.getStatus());
		appDto.setCorrectStatus(app.getCorrectStatus());
		appDto.setMonitor(app.isMonitor());

		appDto.setProcessType(appConfig.getProcessType());
		AppProcess appProcess = appContext.getProcess();
		if (appProcess != null) {
			appDto.setPid(appProcess.getId());
		}

		return appDto;
	}

}
