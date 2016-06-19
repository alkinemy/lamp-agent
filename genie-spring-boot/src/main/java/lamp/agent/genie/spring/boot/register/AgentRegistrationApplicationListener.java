package lamp.agent.genie.spring.boot.register;

import lamp.agent.genie.spring.boot.register.model.AgentEvent;
import lamp.agent.genie.spring.boot.register.model.AgentEventName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class AgentRegistrationApplicationListener implements ApplicationListener<ApplicationEvent> {

	private final AgentRegistrator lampClientRegistrator;
	private final AgentEventPublisher agentEventPublisher;

	public AgentRegistrationApplicationListener(AgentRegistrator lampClientRegistrator
		, AgentEventPublisher agentEventPublisher) {
		this.lampClientRegistrator = lampClientRegistrator;
		this.agentEventPublisher = agentEventPublisher;
	}

	@Override public void onApplicationEvent(ApplicationEvent event) {
		log.debug("Event : {}", event);
		if (event instanceof EmbeddedServletContainerInitializedEvent) {
			try {
				lampClientRegistrator.register();
				agentEventPublisher.publish(AgentEvent.of(AgentEventName.AGENT_STARTED, null));
			} catch (Exception e) {
				log.error("LampClient register failed");
			}
		} else if (event instanceof ContextClosedEvent) {
			lampClientRegistrator.deregister();
			agentEventPublisher.publish(AgentEvent.of(AgentEventName.AGENT_STOPPED, null));
		}
	}

}
