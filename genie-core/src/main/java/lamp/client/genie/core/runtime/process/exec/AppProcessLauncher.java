package lamp.client.genie.core.runtime.process.exec;


import lamp.client.genie.core.context.AppContext;
import lamp.client.genie.core.exception.UnsupportedProcessTypeException;
import lamp.client.genie.core.runtime.process.exec.foreground.DefaultProcess;
import lamp.client.genie.core.runtime.process.AppProcess;
import lamp.client.genie.core.runtime.process.AppProcessType;
import lamp.client.genie.core.runtime.process.exec.background.DaemonProcess;

public class AppProcessLauncher {

	public AppProcess launch(AppContext appContext) {
		AppProcessType appProcessType = appContext.getAppManifest().getProcessType();
		AppProcess process;
		if (AppProcessType.DEFAULT.equals(appProcessType)) {
			process = new DefaultProcess(appContext);
		} else if (AppProcessType.DAEMON.equals(appProcessType)) {
			process = new DaemonProcess(appContext);
		} else {
			throw new UnsupportedProcessTypeException(appProcessType);
		}
		return process;
	}

}
