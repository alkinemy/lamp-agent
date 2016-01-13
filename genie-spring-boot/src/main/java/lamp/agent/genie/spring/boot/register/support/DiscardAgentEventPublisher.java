package lamp.agent.genie.spring.boot.register.support;

import lamp.agent.genie.spring.boot.register.AgentEventPublisher;
import lamp.agent.genie.spring.boot.register.model.AgentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class DiscardAgentEventPublisher implements AgentEventPublisher {


	public DiscardAgentEventPublisher() {
	}

	@Override
	@Async
	public void publish(AgentEvent agentEvent) {
		log.debug("discard event = {}", agentEvent);
	}

}
