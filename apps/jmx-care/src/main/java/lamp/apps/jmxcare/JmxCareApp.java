package lamp.apps.jmxcare;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.management.ConnectorAddressLink;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Slf4j
@SpringBootApplication
public class JmxCareApp {

	private static final String PID_FILE_NAME = "jmx-beat.pid";

	public static void main(String[] args) throws Exception {
		List<VirtualMachineDescriptor> vms = VirtualMachine.list();
		for (VirtualMachineDescriptor desc : vms) {
			log.error("vm (id={}, name={})", desc.id(), desc.displayName());
		}
		SpringApplication springApplication = new SpringApplication(JmxCareApp.class);
		springApplication.addListeners(new ApplicationPidFileWriter(PID_FILE_NAME));
		springApplication.run(args);
	}

}
