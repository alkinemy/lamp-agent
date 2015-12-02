package lamp.client.genie.core.runtime.process.exec;


import lamp.client.genie.core.context.AppContext;
import lamp.client.genie.core.exception.UnsupportedProcessTypeException;
import lamp.client.genie.core.runtime.process.exec.foreground.ForegroundProcess;
import lamp.client.genie.core.runtime.process.AppProcess;
import lamp.client.genie.core.runtime.process.AppProcessType;
import lamp.client.genie.core.runtime.process.exec.background.BackgroundProcess;

public class AppProcessLauncher {

	public AppProcess launch(AppContext appContext) {
		AppProcessType appProcessType = appContext.getAppManifest().getProcessType();
		AppProcess process;
		if (AppProcessType.FOREGROUND.equals(appProcessType)) {
			process = new ForegroundProcess(appContext);
		} else if (AppProcessType.BACKGROUND.equals(appProcessType)) {
			process = new BackgroundProcess(appContext);
		} else {
			throw new UnsupportedProcessTypeException(appProcessType);
		}
		return process;
	}

}
