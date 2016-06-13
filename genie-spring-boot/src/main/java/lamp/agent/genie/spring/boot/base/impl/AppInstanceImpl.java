package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppInstance;
import lamp.agent.genie.core.AppInstanceContext;
import lamp.agent.genie.core.AppInstanceSpec;
import lamp.agent.genie.core.AppInstanceStatus;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.utils.StringUtils;
import lombok.Getter;

import java.io.File;
import java.util.Date;

public class AppInstanceImpl implements AppInstance {

	@Getter
	private final AppInstanceContext context;

	@Getter
	private Date startTime;
	@Getter
	private Date stopTime;

	private AppInstanceStatus correctStatus;

	public AppInstanceImpl(AppInstanceContext context, AppInstanceStatus correctStatus) {
		this.context = context;
		this.correctStatus = correctStatus;
	}

	@Override
	public String getId() {
		return context.getId();
	}

	@Override public AppInstanceSpec getSpec() {
		return context.getAppInstanceSpec();
	}

	@Override
	public synchronized AppInstanceStatus getStatus() {
		return context.getStatus();
	}

	@Override public AppInstanceStatus getCorrectStatus() {
		return correctStatus;
	}

	@Override public boolean isRunning() {
		return AppInstanceStatus.RUNNING.equals(getStatus());
	}

	@Override public boolean monitored() {
		return context.getAppInstanceSpec().isMonitor();
	}

	@Override public File getStdOutFile() {
		String logFile = context.getParsedAppInstanceSpec().getStdOutFile();
		return StringUtils.isNotBlank(logFile) ? new File(logFile) : null;
	}

	@Override public File getStdErrFile() {
		String logFile = context.getParsedAppInstanceSpec().getStdErrFile();
		return StringUtils.isNotBlank(logFile) ? new File(logFile) : null;
	}

	@Override
	public synchronized void start() {
		AppInstanceStatus currentStatus = context.getStatus();
		boolean canStart = AppInstanceStatus.STOPPED.equals(currentStatus);
		if (!canStart) {
			throw Exceptions.newException(ErrorCode.APP_IS_ALREADY_RUNNING);
		}

		try {
			context.updateStatus(AppInstanceStatus.STARTING);
			this.startTime = new Date();
			this.stopTime = null;

			context.createProcess();

			correctStatus = AppInstanceStatus.RUNNING;
		} catch (Exception e) {
			context.updateStatus(AppInstanceStatus.STOPPED);
			throw Exceptions.newException(ErrorCode.APP_START_FAILED, e);
		}
	}

	@Override
	public synchronized void stop() {
		correctStatus = AppInstanceStatus.STOPPED;

		AppInstanceStatus currentStatus = getStatus();
		boolean canStop = AppInstanceStatus.RUNNING.equals(currentStatus);
		if (!canStop) {
			throw Exceptions.newException(ErrorCode.APP_IS_NOT_RUNNING);
		}

		try {
			context.updateStatus(AppInstanceStatus.STOPPING);
			this.stopTime = new Date();

			context.terminateProcess();

		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_STOP_FAILED, e);
		}
	}

}
