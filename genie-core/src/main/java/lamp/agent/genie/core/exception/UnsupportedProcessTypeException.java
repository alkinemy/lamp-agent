package lamp.agent.genie.core.exception;

import lamp.agent.genie.core.runtime.process.AppProcessType;

public class UnsupportedProcessTypeException extends AppException {

	public UnsupportedProcessTypeException(AppProcessType appProcessType) {
		super("Unsupported process type : " + appProcessType, "UNSUPPORTED_PROCESS_TYPE",  appProcessType);
	}

}
