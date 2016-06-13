package lamp.agent.genie.core.install;


import lamp.agent.genie.core.AppInstanceContext;
import lamp.agent.genie.core.exception.AppException;
import lamp.agent.genie.core.exception.InstallException;
import lamp.agent.genie.core.script.CommandExecutionContext;
import lamp.agent.genie.core.script.ScriptCommand;
import lamp.agent.genie.core.script.SimpleCommandExecutionContext;
import lamp.agent.genie.utils.CollectionUtils;
import lamp.agent.genie.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Slf4j
public class SimpleAppInstaller implements AppInstaller {

	public SimpleAppInstaller() {
	}

	@Override public void install(InstallContext context) {
		AppInstanceContext appInstanceContext = context.getAppInstanceContext();

		File installLogFile = context.getInstallLogFile();
		if (!installLogFile.getParentFile().exists()) {
			installLogFile.getParentFile().mkdirs();
		}
		try (CommandExecutionContext commandExecutionContext
				= new SimpleCommandExecutionContext(appInstanceContext, new BufferedOutputStream(new FileOutputStream(installLogFile)), context.getExpressionParser())) {
			InstallSpec installSpec = appInstanceContext.getInstallSpec();

			File file = new File(installSpec.getDirectory(), installSpec.getFilename());
			context.transferTo(file);

			List<ScriptCommand> commands = context.getCommands();

			if (CollectionUtils.isNotEmpty(commands)) {
				for (ScriptCommand command : commands) {
					command.execute(commandExecutionContext);
				}
			}
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new InstallException("Install failed", e);
		}
	}

	@Override public void uninstall(UninstallContext context) {
		AppInstanceContext appInstanceContext = context.getAppInstanceContext();
		InstallSpec installSpec = appInstanceContext.getInstallSpec();

		File file = new File(installSpec.getDirectory(), installSpec.getFilename());

		log.info("[App:{}] file({}) deleting", appInstanceContext.getId(), file.getAbsolutePath());
		FileUtils.deleteQuietly(file);
	}

}
