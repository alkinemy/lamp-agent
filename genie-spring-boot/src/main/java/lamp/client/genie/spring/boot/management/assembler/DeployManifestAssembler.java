package lamp.client.genie.spring.boot.management.assembler;

import lamp.client.genie.core.deploy.DeployManifest;
import lamp.client.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.client.genie.spring.boot.management.form.AppRegistrationForm;
import lamp.client.genie.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DeployManifestAssembler extends AbstractListAssembler<AppRegistrationForm, DeployManifest> {

	@Override protected DeployManifest doAssemble(AppRegistrationForm form) {
		DeployManifest manifest = new DeployManifest();
		BeanUtils.copyProperties(form, manifest, DeployManifest.class);

		if (StringUtils.isBlank(manifest.getFilename())) {
			manifest.setFilename(form.getDeployFile().getOriginalFilename());
		}

		return manifest;
	}

}
