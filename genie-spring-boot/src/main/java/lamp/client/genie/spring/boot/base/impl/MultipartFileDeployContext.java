package lamp.client.genie.spring.boot.base.impl;

import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.context.DeployContext;
import lamp.client.genie.core.deploy.DeployManifest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor(staticName = "of")
public class MultipartFileDeployContext implements DeployContext {

	@Getter @NonNull
	private DeployManifest deployManifest;
	@Getter @NonNull
	private AppManifest appManifest;
	@Getter @NonNull
	private MultipartFile multipartFile;

	@Override public void transferTo(File dest) throws IOException {
		multipartFile.transferTo(dest);
	}

}
