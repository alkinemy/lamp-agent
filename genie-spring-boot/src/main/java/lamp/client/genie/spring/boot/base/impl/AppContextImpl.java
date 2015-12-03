package lamp.client.genie.spring.boot.base.impl;

import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.AppStatus;
import lamp.client.genie.core.context.AppContext;
import lamp.client.genie.core.context.LampContext;
import lamp.client.genie.core.runtime.process.AppProcess;
import lamp.client.genie.core.runtime.process.AppProcessState;
import lamp.client.genie.core.runtime.process.exec.AppProcessLauncher;
import lamp.client.genie.core.runtime.shell.Shell;
import lombok.Getter;

import org.apache.commons.collections.map.HashedMap;

import java.io.File;
import java.util.Map;

public class AppContextImpl implements AppContext {

	private AppProcessLauncher processLauncher = new AppProcessLauncher();
	@Getter
	private final LampContext lampContext;
	@Getter
	private final AppManifest appManifest;

	private AppStatus appStatus = AppStatus.NOT_RUNNING;
	private long lastCheckTimeMillis;
	private AppProcess process;
	private File systemLogFile;

	public AppContextImpl(LampContext lampContext, AppManifest appManifest) {
		this.lampContext = lampContext;
		this.appManifest = appManifest;

		this.systemLogFile = new File(lampContext.getLogDirectory(), appManifest.getId() + ".log");
	}

	public AppManifest getAppManifest() {
		return appManifest;
	}

	public Map<String, Object> getParameters() {
		Map<String, Object> parameters = new HashedMap();
		parameters.put("agentId", appManifest.getId());
		parameters.put("agentName", appManifest.getName());
		parameters.put("agentVersion", appManifest.getVersion());
		parameters.put("mountPoint", appManifest.getHomeDirectory().getAbsolutePath());
		parameters.put("workDirectory", appManifest.getWorkDirectory().getAbsolutePath());

//		Environment environment = lampContext.getEnvironment();
//		parameters.put("activeProfiles", environment.getActiveProfiles());
//		parameters.put("env", environment);
		if (appManifest.getParameters() != null) {
			parameters.putAll(appManifest.getParameters());
		}
		return parameters;
	}

	public String getId() {
		return appManifest.getId();
	}

	@Override public Shell getShell() {
		return lampContext.getShell();
	}

	@Override public File getPidFile() {
		return appManifest.getPidFile();
	}

	@Override public AppStatus getStatus() {
		if (System.currentTimeMillis() - lastCheckTimeMillis > appManifest.getCheckStatusInterval()) {
			return checkAndUpdateStatus();
		}

		return appStatus;
	}

	@Override public AppStatus updateStatus(AppStatus status) {
		this.appStatus = status;
		return this.appStatus;
	}

	@Override public AppStatus checkAndUpdateStatus() {
		lastCheckTimeMillis = System.currentTimeMillis();
		AppProcessState processStatus = getProcessStatus();
		if (AppProcessState.RUNNING.equals(processStatus)) {
			return updateStatus(AppStatus.RUNNING);
		} else {
			return updateStatus(AppStatus.NOT_RUNNING);
		}
	}

	@Override public AppProcess createProcess() {
		return processLauncher.launch(this);
	}


	public AppProcess getProcess() {
		return process;
	}

	public AppProcessState getProcessStatus() {
		AppProcess process = getProcess();
		return process != null ? process.getStatus() : AppProcessState.NOT_RUNNING;
	}


}
