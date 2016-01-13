package lamp.agent.genie.spring.boot.register.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor(staticName = "of")
public class AgentEvent {

	private String eventName;

	private AgentEventLevel eventLevel;

	private Date eventTime = new Date();

	private String appId;

	private String message;

	public static AgentEvent of(AgentEventName eventName, String appId) {
		return of(eventName.name(), eventName.getEventLevel(), new Date(), appId, null);
	}
}
