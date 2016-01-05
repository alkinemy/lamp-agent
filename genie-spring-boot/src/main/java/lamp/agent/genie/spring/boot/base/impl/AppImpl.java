package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppManifest;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.context.AppContext;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lombok.Getter;


import java.util.Date;

public class AppImpl implements App {

	private final AppContext context;

	@Getter
	private AppProcess process;
	@Getter
	private Date startTime;
	@Getter
	private Date stopTime;

	public AppImpl(AppContext context) {
		this.context = context;
	}

	@Override
	public String getId() {
		return context.getId();
	}

	@Override public AppManifest getManifest() {
		return context.getAppManifest();
	}

	@Override
	public AppStatus getStatus() {
		return context.getStatus();
	}

	@Override public boolean isRunning() {
		return AppStatus.RUNNING.equals(getStatus());
	}

	@Override
	public synchronized void start() {
		AppStatus currentStatus = context.getStatus();
		boolean canStart = AppStatus.NOT_RUNNING.equals(currentStatus);
		if (!canStart) {
			throw Exceptions.newException(ErrorCode.APP_IS_ALREADY_RUNNING);
		}

		try {
			context.updateStatus(AppStatus.STARTING);
			this.startTime = new Date();
			this.stopTime = null;

			process = context.createProcess();

		} catch (Exception e) {
			context.updateStatus(AppStatus.NOT_RUNNING);
			throw Exceptions.newException(ErrorCode.APP_START_FAILED, e);
		}
	}

	@Override
	public synchronized void stop() {
		AppStatus currentStatus = getStatus();
		boolean canStop = AppStatus.RUNNING.equals(currentStatus);
		if (!canStop) {
			throw Exceptions.newException(ErrorCode.APP_IS_NOT_RUNNING);
		}

		try {
			context.updateStatus(AppStatus.STOPPING);
			this.stopTime = new Date();

			context.getP.terminate();
		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_STOP_FAILED, e);
		}
	}

}
