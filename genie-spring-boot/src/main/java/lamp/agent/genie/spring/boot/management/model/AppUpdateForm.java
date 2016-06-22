package lamp.agent.genie.spring.boot.management.model;

import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppUpdateForm extends SimpleAppContainer {

	private boolean forceStop;

}
