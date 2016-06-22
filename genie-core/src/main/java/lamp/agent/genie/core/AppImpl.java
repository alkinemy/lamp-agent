package lamp.agent.genie.core;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lamp.agent.genie.core.app.AppContainer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppImpl implements App {

	private String id;
	private String name;
	private String description;

	private int cpu;
	private int memory;
	private int diskSpace;

	private AppStatus correctStatus;
	private AppStatus status;
	private String health;

	private AppContainer appContainer;
	private Map<String, Object> parameters;

	@JsonIgnore
	private AppContext appContext;

	@JsonIgnore
	public void start() {
		appContext.startProcess();
		correctStatus = AppStatus.RUNNING;
	}

	@JsonIgnore
	public void stop() {
		correctStatus = AppStatus.STOPPED;

		appContext.stopProcess();
	}

	public AppStatus getStatus() {
		return appContext.getStatus();
	}

	@JsonIgnore
	public boolean isRunning() {
		return appContext.isProcessRunning();
	}


}
