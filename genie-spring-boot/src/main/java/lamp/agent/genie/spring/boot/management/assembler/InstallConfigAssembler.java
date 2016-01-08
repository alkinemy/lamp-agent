package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.management.form.AppRegisterForm;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class InstallConfigAssembler extends AbstractListAssembler<AppRegisterForm, InstallConfig> {

	@Override protected InstallConfig doAssemble(AppRegisterForm form) {
		InstallConfig installConfig = new InstallConfig();
		BeanUtils.copyProperties(form, installConfig, InstallConfig.class);

		return installConfig;
	}

}
