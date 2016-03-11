package lamp.agent.genie.spring.boot.register.support;

import lamp.agent.genie.metrics.Disk;
import lamp.agent.genie.spring.boot.config.MetricsDiskSpaceProperties;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
public class DiskSpacePublicMetrics implements PublicMetrics, Ordered {

	@Autowired
	private MetricsDiskSpaceProperties metricsDiskSpaceProperties;

	private List<Disk> disks;

	@PostConstruct
	public void init() {
		this.disks = new ArrayList<>();

		String[] names = StringUtils.split(metricsDiskSpaceProperties.getName(), ',');
		String[] paths = StringUtils.split(metricsDiskSpaceProperties.getPath(), ',');

		if (names == null) {
			names = paths;
		}
		if (paths != null) {
			for (int i = 0; i < paths.length; i++) {
				File path = new File(StringUtils.trim(paths[i]));
				if (path.exists() && path.canRead()) {
					Disk disk = new Disk();
					disk.setName(StringUtils.trim(names[i]));
					disk.setPath(path);

					disks.add(disk);
				} else {
					log.warn("path({}) not exist", paths[i]);
				}
			}

		}
	}

	@Override public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 2000;
	}

	@Override public Collection<Metric<?>> metrics() {
		Collection<Metric<?>> result = new LinkedHashSet<Metric<?>>();

		addDiskSpace(result);

		return result;
	}

	protected void addDiskSpace(Collection<Metric<?>> result) {
		for (Disk disk : disks) {
			File path = disk.getPath();
			result.add(new Metric<>(name("server.diskspace", disk.getName(), "total"), path.getTotalSpace()));
			result.add(new Metric<>(name("server.diskspace", disk.getName(), "free"), path.getFreeSpace()));
			result.add(new Metric<>(name("server.diskspace", disk.getName(), "usable"), path.getUsableSpace()));
		}
	}

	protected String name(String... nameArray) {
		StringBuilder sb = new StringBuilder();
		for (String name : nameArray) {
			if (sb.length() > 0) {
				sb.append(".");
			}
			sb.append(name);
		}
		return sb.toString();
	}

}
