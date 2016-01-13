package lamp.agent.genie.spring.boot.register;

import lamp.agent.genie.spring.boot.register.model.AgentEvent;


public interface AgentEventPublisher {

	void publish(AgentEvent agentEvent);

}
