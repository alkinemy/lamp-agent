package lamp.agent.genie.spring.boot.register.model;

import lombok.Getter;

public enum AgentEventName {

	APP_STARTED(AgentEventLevel.INFO, "App Started")
	, APP_STOPPED(AgentEventLevel.INFO, "App Stopped")
	, APP_STARTING_BY_MONITOR(AgentEventLevel.WARN, "App Staring by MONITOR");

	@Getter
	private AgentEventLevel eventLevel;
	@Getter
	private String defaultMessage;

	AgentEventName(AgentEventLevel eventLevel, String defaultMessage) {
		this.eventLevel = eventLevel;
		this.defaultMessage = defaultMessage;
	}

}
