package lamp.client.genie.metrics.sigar;

import lamp.client.genie.utils.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CpuMetrics extends AbstractSigarMetric {

	private static final long DELAY_MILLIS = 1000;

	private final CpuInfo info;

	protected CpuMetrics(Sigar sigar) {
		super(sigar);
		info = cpuInfo();
	}

	public int getTotalCores() {
		if (info == null) {
			return -1;
		}
		return info.getTotalCores();
	}

	public int getTotalSockets() {
		if (info == null) {
			return -1;
		}
		return info.getTotalSockets();
	}

	public List<CpuUsage> getCpuUsages() {
		List<CpuUsage> result = new ArrayList<>();
		CpuPerc[] cpus = cpuPercList();
		if (cpus == null) {
			return result;
		}

		if (Double.isNaN(cpus[0].getIdle())) {
			try {
				Thread.sleep(DELAY_MILLIS);
			} catch (InterruptedException e) {
				return result;
			}
			cpus = cpuPercList();
			if (cpus == null) {
				return result;
			}
		}

		for (CpuPerc cp : cpus) {
			result.add(CpuUsage.from(cp));
		}
		return result;
	}

	protected CpuInfo cpuInfo() {
		try {
			CpuInfo[] cpuInfos = sigar.getCpuInfoList();
			if (ArrayUtils.isEmpty(cpuInfos)) {
				return null;
			}
			return cpuInfos[0];
		} catch (SigarException e) {
			log.warn("Cannot get CpuInfo", e);
			return null;
		}
	}

	protected CpuPerc[] cpuPercList() {
		try {
			CpuPerc[] cpuPercs = sigar.getCpuPercList();
			if (ArrayUtils.isEmpty(cpuPercs)) {
				return null;
			}
			return cpuPercs;
		} catch (SigarException e) {
			log.warn("Cannot get CpuPerc", e);
			return null;
		}
	}

}
