package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.SimpleAppInstance;
import lamp.agent.genie.core.SimpleAppInstanceContext;
import lamp.agent.genie.core.AppInstanceSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.management.model.AppInstanceDto;
import org.springframework.stereotype.Component;

@Component
public class AppInstanceDtoAssembler extends AbstractListAssembler<SimpleAppInstance, AppInstanceDto> {

	@Override protected AppInstanceDto doAssemble(SimpleAppInstance appInstance) {
		AppInstanceSpec appInstanceSpec = appInstance.getSpec();
		SimpleAppInstanceContext appInstanceContext = appInstance.getContext();

		AppInstanceDto appInstanceDto = new AppInstanceDto();
		appInstanceDto.setId(appInstance.getId());
		appInstanceDto.setName(appInstanceSpec.getName());
		appInstanceDto.setDescription(appInstanceSpec.getDescription());

		appInstanceDto.setStatus(appInstance.getStatus());
		appInstanceDto.setMonitored(appInstance.monitored());

		appInstanceDto.setProcessType(appInstanceSpec.getProcessType());
		AppProcess appProcess = appInstanceContext.getProcess();
		if (appProcess != null) {
			appInstanceDto.setPid(appProcess.getId());
		}

		return appInstanceDto;
	}

}
