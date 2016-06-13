package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.AppInstanceSpec;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.base.impl.AppInstanceSpecImpl;
import lamp.agent.genie.spring.boot.management.model.SimpleAppContainer;
import lamp.agent.genie.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AppSpecAssembler extends AbstractListAssembler<SimpleAppContainer, AppInstanceSpec> {

	@Autowired
	private LampContext lampContext;

	@Override protected AppInstanceSpec doAssemble(SimpleAppContainer form) {
		AppInstanceSpecImpl appConfig = new AppInstanceSpecImpl();
		BeanUtils.copyProperties(form, appConfig, AppInstanceSpecImpl.class);

		if (StringUtils.isBlank(appConfig.getAppDirectory())) {
			File appDirectory = new File(lampContext.getAppDirectory(), appConfig.getId() + "/app");
			appConfig.setAppDirectory(appDirectory.getAbsolutePath());
		}

		return appConfig;
	}

}
