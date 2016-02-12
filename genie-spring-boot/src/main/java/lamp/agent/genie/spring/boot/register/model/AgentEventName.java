package lamp.agent.genie.spring.boot.register.model;

import lombok.Getter;

public enum AgentEventName {

	AGENT_STARTED(AgentEventLevel.INFO, "Agent Start4ed")
	, AGENT_STOPPED(AgentEventLevel.INFO, "Agent Stopped")
	, APP_INSTALLED(AgentEventLevel.INFO, "App Installed")
	, APP_UPDATED(AgentEventLevel.INFO, "App Updated")
	, APP_UNINSTALLED(AgentEventLevel.INFO, "App Uninstalled")
	, APP_REGISTERED(AgentEventLevel.INFO, "App Registered")
	, APP_UNREGISTERED(AgentEventLevel.INFO, "App Unregistered")
	, APP_STARTED(AgentEventLevel.INFO, "App Started")
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
