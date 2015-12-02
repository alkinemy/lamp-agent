package lamp.client.genie.spring.boot.base.register.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class LampClientApiRegisterForm {

	private String id;
	private String name;

	private String type;
	private String version;

	private String protocol;
	private String hostname;
	private String address;
	private int port = -1;

	private String secretKey;
	private Date time;

	private String healthPath;
	private String metricsPath;


}
