package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.spring.boot.register.AgentEventPublisher;
import lamp.agent.genie.spring.boot.register.model.AgentEvent;
import lamp.agent.genie.spring.boot.register.support.DiscardAgentEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class AgentEventPublishService {

	@Autowired(required = false)
	private AgentEventPublisher agentEventPublisher;

	@PostConstruct
	public void init() {
		if (agentEventPublisher == null) {
			agentEventPublisher = new DiscardAgentEventPublisher();
		}
	}

	public void publish(AgentEvent event) {
		try {
			agentEventPublisher.publish(event);
		} catch (Throwable t) {
			log.warn("AgentEvent Publish failed", t);
		}

	}

}
