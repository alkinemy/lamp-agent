package lamp.agent.genie.spring.boot.register;

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

	public AgentRegistrationApplicationListener(AgentRegistrator lampClientRegistrator) {
		this.lampClientRegistrator = lampClientRegistrator;
	}

	@Override public void onApplicationEvent(ApplicationEvent event) {
		log.debug("Event : {}", event);
		if (event instanceof EmbeddedServletContainerInitializedEvent) {
			lampClientRegistrator.register();
		} else if (event instanceof ContextClosedEvent) {
			lampClientRegistrator.deregister();
		}
	}

}
