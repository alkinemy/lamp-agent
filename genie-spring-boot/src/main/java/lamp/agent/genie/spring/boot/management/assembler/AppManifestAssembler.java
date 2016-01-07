package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.context.LampContext;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.base.impl.AppManifestImpl;
import lamp.agent.genie.spring.boot.management.form.AppRegisterForm;
import lamp.agent.genie.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AppManifestAssembler extends AbstractListAssembler<AppRegisterForm, AppManifest> {

	@Autowired
	private LampContext lampContext;

	@Override protected AppManifest doAssemble(AppRegisterForm form) {
		AppManifestImpl manifest = new AppManifestImpl();
		BeanUtils.copyProperties(form, manifest, AppManifestImpl.class);

		if (StringUtils.isBlank(manifest.getHomeDirectoryPath())) {
			File homeDirectory = new File(lampContext.getAppDirectory(), manifest.getId() + "/app");
			manifest.setHomeDirectoryPath(homeDirectory.getAbsolutePath());
		}

		if (StringUtils.isBlank(manifest.getWorkDirectoryPath())) {
			manifest.setWorkDirectoryPath(manifest.getHomeDirectory().getAbsolutePath());
		}

		if (StringUtils.isBlank(manifest.getLogDirectoryPath())) {
			manifest.setLogDirectoryPath(new File(manifest.getHomeDirectory(), "/logs").getAbsolutePath());
		}

		return manifest;
	}

}
