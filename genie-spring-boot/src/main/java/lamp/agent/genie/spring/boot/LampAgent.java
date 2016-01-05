package lamp.agent.genie.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LampAgent {

	private static final String PID_FILE_NAME = "lamp-agent.pid";

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(LampAgent.class);
		springApplication.setBanner(new LampAgentBanner());
		springApplication.addListeners(new ApplicationPidFileWriter(PID_FILE_NAME));
		springApplication.run(args);
	}

}
