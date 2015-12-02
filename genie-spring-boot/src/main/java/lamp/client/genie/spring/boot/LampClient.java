package lamp.client.genie.spring.boot;

import lamp.client.genie.spring.boot.config.LampClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({ LampClientProperties.class })
@SpringBootApplication
public class LampClient {

	private static final String PID_FILE_NAME = "lamp-client.pid";

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(LampClient.class);
		springApplication.setBanner(new LampClientBanner());
		springApplication.addListeners(new ApplicationPidFileWriter(PID_FILE_NAME));
		springApplication.run(args);
	}

}
