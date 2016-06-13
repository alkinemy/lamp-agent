package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppInstanceContext;
import lamp.agent.genie.core.install.InstallContext;
import lamp.agent.genie.core.script.ScriptCommand;
import lamp.agent.genie.utils.ExpressionParser;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor(staticName = "of")
public class MultipartFileInstallContext implements InstallContext {

	@Getter @NonNull
	private AppInstanceContext appInstanceContext;

	@Getter
	private ExpressionParser expressionParser = new SpringExpressionParser();

	@Getter @NonNull
	private MultipartFile multipartFile;

	@Getter @NonNull
	private List<ScriptCommand> commands;

	@Getter @NonNull
	private File installLogFile;

	@Override public void transferTo(File dest) throws IOException {
		multipartFile.transferTo(dest);
	}

}
