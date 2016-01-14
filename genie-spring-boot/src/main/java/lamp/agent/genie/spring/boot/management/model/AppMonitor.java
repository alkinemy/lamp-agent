package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppMonitor {

	private int retryCount;
	private long lastRetryTimeMillis;
}
