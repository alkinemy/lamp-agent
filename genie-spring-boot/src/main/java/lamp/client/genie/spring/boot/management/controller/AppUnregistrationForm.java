package lamp.client.genie.spring.boot.management.controller;

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
