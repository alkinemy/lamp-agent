package lamp.agent.genie.spring.boot.register.model;

import lombok.*;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@ToString
@AllArgsConstructor(staticName = "of")
public class AgentEvent {

	private static final AtomicLong EVENT_SEQUENCE = new AtomicLong();

	private String eventName;

	private AgentEventLevel eventLevel;

	private Date eventTime = new Date();
	private Long eventSequence;

	private String appId;

	private String message;

	public static AgentEvent of(AgentEventName eventName, String appId) {
		return of(eventName.name(), eventName.getEventLevel(), new Date(), EVENT_SEQUENCE.incrementAndGet(), appId, null);
	}
}
