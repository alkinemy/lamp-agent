package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class AppFileUpdateForm {

	private String version;
	private MultipartFile installFile;
	private String commands;

	private boolean forceStop;

}
