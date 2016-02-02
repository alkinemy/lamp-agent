package lamp.agent.genie.spring.boot.register.support;

import lamp.agent.genie.spring.boot.config.LampAgentProperties;
import lamp.agent.genie.spring.boot.config.LampServerProperties;
import lamp.agent.genie.spring.boot.register.AgentEventPublisher;
import lamp.agent.genie.spring.boot.register.AgentRegistrator;
import lamp.agent.genie.spring.boot.register.model.AgentEvent;
import lamp.agent.genie.spring.boot.register.model.AgentRegisterForm;
import lamp.agent.genie.spring.boot.register.model.AgentRegisterResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ApiAgentEventPublisher implements AgentEventPublisher {

	private final LampServerProperties lampServerProperties;
	private final LampAgentProperties lampClientProperties;

	private final RestTemplate restTemplate;

	public ApiAgentEventPublisher(
			LampServerProperties lampServerProperties, LampAgentProperties lampClientProperties,
			RestTemplate restTemplate) {
		this.lampServerProperties = lampServerProperties;
		this.lampClientProperties = lampClientProperties;

		this.restTemplate = restTemplate;
	}

	@Override
	@Async
	public void publish(AgentEvent agentEvent) {
		String url = lampServerProperties.getUrl() + "/api/agent/" + lampClientProperties.getId() + "/event";
		log.debug("eventUrl = {}, event = {}", url, agentEvent);

		restTemplate.postForObject(url, agentEvent, Void.class);
	}

}
