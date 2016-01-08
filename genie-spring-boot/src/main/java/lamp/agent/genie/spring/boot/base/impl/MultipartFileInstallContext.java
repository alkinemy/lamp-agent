package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.install.InstallContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor(staticName = "of")
public class MultipartFileInstallContext implements InstallContext {

	@Getter @NonNull
	private AppContext appContext;

	@Getter @NonNull
	private MultipartFile multipartFile;


	@Override public void transferTo(File dest) throws IOException {
		multipartFile.transferTo(dest);
	}

}
