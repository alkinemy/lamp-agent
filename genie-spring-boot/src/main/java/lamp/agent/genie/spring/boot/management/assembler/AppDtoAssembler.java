package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.management.model.AppDto;
import org.springframework.stereotype.Component;

@Component
public class AppDtoAssembler extends AbstractListAssembler<App, AppDto> {

	@Override protected AppDto doAssemble(App app) {
		AppSpec appSpec = app.getSpec();
		AppContext appContext = app.getContext();

		AppDto appDto = new AppDto();
		appDto.setId(app.getId());
		appDto.setName(appSpec.getName());
		appDto.setDescription(appSpec.getDescription());

		appDto.setGroupId(appSpec.getGroupId());
		appDto.setArtifactId(appSpec.getArtifactId());
		appDto.setVersion(appSpec.getVersion());

		appDto.setStatus(app.getStatus());
		appDto.setCorrectStatus(app.getCorrectStatus());
		appDto.setMonitor(app.isMonitor());

		appDto.setProcessType(appSpec.getProcessType());
		AppProcess appProcess = appContext.getProcess();
		if (appProcess != null) {
			appDto.setPid(appProcess.getId());
		}

		return appDto;
	}

}
