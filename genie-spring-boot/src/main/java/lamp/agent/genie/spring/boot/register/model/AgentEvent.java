package lamp.agent.genie.spring.boot.register.model;

import lombok.*;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@ToString
@AllArgsConstructor(staticName = "of")
public class AgentEvent {

	private static final long INSTANCE_ID = System.currentTimeMillis();
	private static final AtomicLong EVENT_SEQUENCE = new AtomicLong();


	private Long instanceId = INSTANCE_ID;
	private Long instanceEventSequence;

	private String eventName;

	private AgentEventLevel eventLevel;

	private Date eventTime = new Date();


	private String appId;

	private String message;

	public static AgentEvent of(AgentEventName eventName, String artifactId) {
		return of(INSTANCE_ID, EVENT_SEQUENCE.incrementAndGet(),  eventName.name(), eventName.getEventLevel(), new Date(), artifactId, null);
	}
}
