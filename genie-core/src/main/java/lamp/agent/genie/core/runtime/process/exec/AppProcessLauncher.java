package lamp.agent.genie.core.runtime.process.exec;


import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.exception.UnsupportedProcessTypeException;
import lamp.agent.genie.core.runtime.process.exec.foreground.DefaultProcess;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.core.runtime.process.exec.background.DaemonProcess;

public class AppProcessLauncher {

	public AppProcess launch(AppContext appContext) {
		AppProcessType appProcessType = appContext.getAppConfig().getProcessType();
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
