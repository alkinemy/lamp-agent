package lamp.agent.genie.core;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lamp.agent.genie.core.app.AppContainer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
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

	@JsonIgnore
	public boolean isRunning() {
		return appContext.isProcessRunning();
	}


}
