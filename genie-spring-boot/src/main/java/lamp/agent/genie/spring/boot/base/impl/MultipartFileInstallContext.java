package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.context.InstallContext;
import lamp.agent.genie.core.deploy.InstallManifest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor(staticName = "of")
public class MultipartFileInstallContext implements InstallContext {

	@Getter @NonNull
	private InstallManifest installManifest;
	@Getter @NonNull
	private AppManifest appManifest;
	@Getter @NonNull
	private MultipartFile multipartFile;
	@Getter @Setter
	private File installFile;

	@Override public void transferTo(File dest) throws IOException {
		multipartFile.transferTo(dest);
	}

}
