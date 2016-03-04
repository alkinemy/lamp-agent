package lamp.agnet.test.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApp {

	private static final String PID_FILE_NAME = "test-app.pid";

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(TestApp.class);
		springApplication.addListeners(new ApplicationPidFileWriter(PID_FILE_NAME));
		springApplication.run(args);
	}

}
