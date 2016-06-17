package lamp.agent.genie.spring.boot.config;

import com.amazonaws.util.EC2MetadataUtils;
import lamp.agent.genie.core.MountPoint;
import lamp.agent.genie.utils.BooleanUtils;
import lamp.agent.genie.utils.HostUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Getter
@Setter
@ConfigurationProperties(prefix = "lamp.agent", ignoreUnknownFields = false)
public class LampAgentProperties implements ApplicationListener<ApplicationEvent> {

	private Boolean ec2 = Boolean.FALSE;

	private String groupId;
	private String artifactId;
	private String version;

	private String id;
	private String name;

	private String mountPointPath;
	private String secretKey;

	private int serverPort = -1;
	private int managementPort = -1;
	private String hostname;
	private String address;

	private String healthType = "SpringBoot";
	private String healthPath = "/health";
	private String metricsType = "SpringBoot";
	private String metricsPath = "/metrics";
	private Boolean metricsSigarEnabled = Boolean.FALSE;

	private boolean monitor;
	private long monitorPeriod;

	@Autowired
	private ManagementServerProperties managementServerProperties;

	@Autowired
	private ServerProperties serverProperties;

	private boolean initialized;

	@PostConstruct
	public void init() throws UnknownHostException {
		if (BooleanUtils.isTrue(ec2)) {
			hostname = EC2MetadataUtils.getLocalHostName();
			address = EC2MetadataUtils.getPrivateIpAddress();
		} else {
			InetAddress inetAddress = InetAddress.getLocalHost();
			hostname = inetAddress.getHostName();
			address = inetAddress.getHostAddress();
		}

		if ("localhost".equals(hostname)) {
			String localHostName = HostUtils.getLocalHostName();
			hostname = StringUtils.defaultString(localHostName, hostname);
		}

		if (StringUtils.isBlank(version)) {
			version = LampAgentProperties.class.getPackage().getImplementationVersion();
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof EmbeddedServletContainerInitializedEvent) {
			this.initialized = true;
			EmbeddedServletContainerInitializedEvent initEvent = (EmbeddedServletContainerInitializedEvent) event;
			if ("management".equals(initEvent.getApplicationContext().getNamespace())) {
				managementPort = initEvent.getEmbeddedServletContainer().getPort();
			} else {
				serverPort = initEvent.getEmbeddedServletContainer().getPort();
			}
		}
	}

	public String getId() {
		if (StringUtils.isBlank(id)) {
			return getHostname() + "-" + getPort();
		} else {
			return id;
		}
	}

	public String getName() {
		if (StringUtils.isBlank(name)) {
			return getId();
		} else {
			return name;
		}
	}

	public String getProtocol() {
		return serverProperties.getSsl() != null && serverProperties.getSsl().isEnabled() ? "https" : "http";
	}

	public int getPort() {
		return managementPort != -1 ? managementPort : serverPort;
	}

	public String getManagementContextPath() {
		if (managementPort != -1) {
			return managementServerProperties.getContextPath();
		} else {
			return serverProperties.getContextPath();
		}
	}

	public MountPoint getMountPoint() {
		return MountPoint.fromPath(getMountPointPath());
	}

	public File getConfigDirectory() {
		return getMountPoint().getDirectory("/conf", true);
	}

	public File getSecretKeyFile() {
		return new File(getConfigDirectory(), "secret.key");
	}
}
