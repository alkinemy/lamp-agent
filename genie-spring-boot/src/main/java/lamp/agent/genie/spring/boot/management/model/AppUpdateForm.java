package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppUpdateForm extends AppRegisterForm {

	private boolean forceStop;

}
