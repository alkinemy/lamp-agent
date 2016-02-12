package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class AppUpdateFileForm {

	private MultipartFile installFile;
	private String commands;

	private boolean forceStop;

}
