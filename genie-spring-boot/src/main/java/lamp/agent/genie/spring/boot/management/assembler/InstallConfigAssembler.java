package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.management.model.AppRegisterForm;
import org.springframework.stereotype.Component;

@Component
public class InstallConfigAssembler extends AbstractListAssembler<AppRegisterForm, InstallSpec> {

	@Override protected InstallSpec doAssemble(AppRegisterForm form) {
		InstallSpec installSpec = new InstallSpec();
		installSpec.setId(form.getId());
		installSpec.setDirectory(null);
		installSpec.setFilename(form.getFilename());
		installSpec.setCommands(form.getCommands());

		return installSpec;
	}



}
