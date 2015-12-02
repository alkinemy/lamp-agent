package lamp.client.genie.core.exception;

import lamp.client.genie.core.runtime.process.AppProcessType;

public class UnsupportedProcessTypeException extends AppException {

	public UnsupportedProcessTypeException(AppProcessType appProcessType) {
		super("Unsupported process type : " + appProcessType);
	}

}
