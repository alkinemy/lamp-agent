package lamp.agent.genie.spring.boot.management.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class AppUnregistrationForm {

	@NotEmpty
	private String id;
	@NotEmpty
	private String name;


}
