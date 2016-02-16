package lamp.apps.jmxcare.connection;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.io.Closeable;
import java.io.IOException;

@AllArgsConstructor
public class JmxConnection implements Closeable {

	private final JMXConnector connector;
	@Getter
	private final MBeanServerConnection mBeanServerConnection;

	@Override
	public void close() throws IOException {
		if (connector != null) {
			connector.close();
		}
	}
}
