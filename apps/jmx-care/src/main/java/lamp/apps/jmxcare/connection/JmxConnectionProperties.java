package lamp.apps.jmxcare.connection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class JmxConnectionProperties {

	private static final String SERVICE_URL_PREFIX = "service:jmx:rmi:///jndi/rmi://";
	private static final String SERVICE_URL_POSTFIX = "/jmxrmi";

	private boolean isLocal;

	private String pid;

	private String url;
	private String host;
	private String port;
	private Map<String, Object> environment;


	public String getUrl() {
		if (this.url == null) {
			if ((this.host == null) || (this.port == null)) {
				return null;
			}
			return SERVICE_URL_PREFIX + this.host + ":" + this.port + SERVICE_URL_POSTFIX;
		}
		return this.url;
	}

}
