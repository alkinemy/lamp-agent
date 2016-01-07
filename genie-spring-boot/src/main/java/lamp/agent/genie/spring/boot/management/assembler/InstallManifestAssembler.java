package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.deploy.InstallManifest;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.management.form.AppRegisterForm;
import lamp.agent.genie.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class InstallManifestAssembler extends AbstractListAssembler<AppRegisterForm, InstallManifest> {

	@Override protected InstallManifest doAssemble(AppRegisterForm form) {
		InstallManifest manifest = new InstallManifest();
		BeanUtils.copyProperties(form, manifest, InstallManifest.class);

		if (StringUtils.isBlank(manifest.getFilename())) {
			manifest.setFilename(form.getInstallFile().getOriginalFilename());
		}

		return manifest;
	}

}
