package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lombok.Getter;

import java.io.File;
import java.util.Date;


public class AppImpl implements App {

	@Getter
	private final AppContext context;

	@Getter
	private Date startTime;
	@Getter
	private Date stopTime;

	private AppStatus correctStatus;

	public AppImpl(AppContext context, AppStatus correctStatus) {
		this.context = context;
		this.correctStatus = correctStatus;
	}

	@Override
	public String getId() {
		return context.getId();
	}

	@Override public AppSpec getConfig() {
		return context.getAppSpec();
	}

	@Override
	public synchronized AppStatus getStatus() {
		return context.getStatus();
	}

	@Override public AppStatus getCorrectStatus() {
		return correctStatus;
	}

	@Override public boolean isRunning() {
		return AppStatus.RUNNING.equals(getStatus());
	}

	@Override public boolean isMonitor() {
		return context.getAppSpec().isMonitor();
	}

	@Override public File getLogFile() {
		String logFile = context.getParsedAppSpec().getLogFile();
		return logFile != null ? new File(logFile) : null;
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

			context.createProcess();

			correctStatus = AppStatus.RUNNING;
		} catch (Exception e) {
			context.updateStatus(AppStatus.NOT_RUNNING);
			throw Exceptions.newException(ErrorCode.APP_START_FAILED, e);
		}
	}

	@Override
	public synchronized void stop() {
		correctStatus = AppStatus.NOT_RUNNING;

		AppStatus currentStatus = getStatus();
		boolean canStop = AppStatus.RUNNING.equals(currentStatus);
		if (!canStop) {
			throw Exceptions.newException(ErrorCode.APP_IS_NOT_RUNNING);
		}

		try {
			context.updateStatus(AppStatus.STOPPING);
			this.stopTime = new Date();

			context.terminateProcess();

		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_STOP_FAILED, e);
		}
	}

}
