package lamp.apps.jmxcare.connection;

import com.sun.tools.attach.VirtualMachine;
import sun.management.ConnectorAddressLink;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.util.Map;

public class JmxConnectionFactory {

	public JmxConnection getJmxConnection(JmxConnectionProperties properties) throws IOException {
		if (properties.isLocal()) {
			return new JmxConnection(null, getLocalMBeanServer());
		} else {
			JMXServiceURL serviceURL;
			if (properties.getPid() != null) {
				serviceURL = extractJMXServiceURLFromId(properties.getPid());
			} else {
				serviceURL = new JMXServiceURL(properties.getUrl());
			}
			JMXConnector connector = getServerConnector(serviceURL, properties.getEnvironment());
			return new JmxConnection(connector, connector.getMBeanServerConnection());
		}
	}

	protected MBeanServer getLocalMBeanServer() {
		return ManagementFactory.getPlatformMBeanServer();
	}

	public JMXServiceURL extractJMXServiceURLFromId(String id) throws IOException {
		try {
			VirtualMachine vm = VirtualMachine.attach(id);
			try {
				int pid = Integer.parseInt(id);
				String serviceUrl = ConnectorAddressLink.importFrom(pid);

				if (serviceUrl == null) {
					String agent = Paths.get(vm.getSystemProperties().getProperty("java.home"), "lib", "management-agent.jar").toString();
					vm.loadAgent(agent);
					serviceUrl = ConnectorAddressLink.importFrom(pid);
				}

				return new JMXServiceURL(serviceUrl);
			} finally {
				vm.detach();
			}
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}

	protected JMXConnector getServerConnector(JMXServiceURL serviceURL, Map<String, Object> environment) throws IOException {
		return JMXConnectorFactory.connect(serviceURL, environment);
	}



}
