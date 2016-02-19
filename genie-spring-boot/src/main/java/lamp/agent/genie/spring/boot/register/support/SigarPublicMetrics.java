package lamp.agent.genie.spring.boot.register.support;

import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.runtime.shell.SigarShell;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.sigar.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.LinkedHashSet;

@Slf4j
public class SigarPublicMetrics implements PublicMetrics, Ordered {

	@Autowired
	private LampContext lampContext;

	private Sigar sigar;

	@PostConstruct
	public void init() {
		if (lampContext.getShell() instanceof SigarShell) {
			sigar = ((SigarShell) lampContext.getShell()).getSigar();
		}
	}

	@Override public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1000;
	}

	@Override public Collection<Metric<?>> metrics() {
		Collection<Metric<?>> result = new LinkedHashSet<Metric<?>>();

		addMemMetrics(result);
		addSwapMetrics(result);
		addCpuMetrics(result);
		addResourceLimitMetrics(result);
		addFileSystemMetrics(result);
		addDiskUsageMetrics(result);
		addNetStatMetrics(result);
		addNetInterfaceMetrics(result);

		return result;
	}

	//	protected void addCpuPercMetrics(Collection<Metric<?>> result) {
	//		List<CpuUsage> cpuUsageList = cpuMetrics.getCpuUsages();
	//		for (int i = 0; i < cpuUsageList.size(); i++) {
	//			CpuUsage cpuUsage = cpuUsageList.get(i);
	//			result.add(new Metric<Double>("server.cpu" + i + ".user", cpuUsage.getUser()));
	//			result.add(new Metric<Double>("server.cpu" + i + ".sys", cpuUsage.getSys()));
	//			result.add(new Metric<Double>("server.cpu" + i + ".nice", cpuUsage.getNice()));
	//			result.add(new Metric<Double>("server.cpu" + i + ".waiting", cpuUsage.getWaiting()));
	//			result.add(new Metric<Double>("server.cpu" + i + ".idle", cpuUsage.getIdle()));
	//			result.add(new Metric<Double>("server.cpu" + i + ".irq", cpuUsage.getIrq()));
	//		}
	//	}

	protected void addMemMetrics(Collection<Metric<?>> result) {
		try {
			Mem mem = sigar.getMem();

			result.add(new Metric<>("server.mem.total", mem.getTotal()));
			result.add(new Metric<>("server.mem.ram", mem.getRam()));
			result.add(new Metric<>("server.mem.used", mem.getUsed()));
			result.add(new Metric<>("server.mem.free", mem.getFree()));
			result.add(new Metric<>("server.mem.actualUsed", mem.getActualUsed()));
			result.add(new Metric<>("server.mem.actualFree", mem.getActualFree()));
			result.add(new Metric<>("server.mem.usedPercent", mem.getUsedPercent()));
			result.add(new Metric<>("server.mem.freePercent", mem.getFreePercent()));
		} catch (SigarException e) {
			log.warn("addMemMetrics Failed", e);
		}
	}

	protected void addSwapMetrics(Collection<Metric<?>> result) {
		try {
			Swap swap = sigar.getSwap();

			result.add(new Metric<>("server.swap.total", swap.getTotal()));
			result.add(new Metric<>("server.swap.used", swap.getUsed()));
			result.add(new Metric<>("server.swap.free", swap.getFree()));
			result.add(new Metric<>("server.swap.pageIn", swap.getPageIn()));
			result.add(new Metric<>("server.swap.pageOut", swap.getPageOut()));
		} catch (SigarException e) {
			log.warn("addSwapMetrics Failed", e);
		}
	}

	protected void addCpuMetrics(Collection<Metric<?>> result) {
		try {
			CpuPerc cpuPerc = sigar.getCpuPerc();

			result.add(new Metric<>("server.cpu.user", cpuPerc.getUser() * 100));
			result.add(new Metric<>("server.cpu.sys", cpuPerc.getSys() * 100));
			result.add(new Metric<>("server.cpu.nice", cpuPerc.getNice() * 100));
			result.add(new Metric<>("server.cpu.idle", cpuPerc.getIdle() * 100));
			result.add(new Metric<>("server.cpu.wait", cpuPerc.getWait() * 100));
			result.add(new Metric<>("server.cpu.irq", cpuPerc.getIrq() * 100));
			//			result.add(new Metric<>("server.cpu.softIrq", cpuPerc.getSoftIrq()));
			//			result.add(new Metric<>("server.cpu.stolen", cpuPerc.getStolen()));
			//			result.add(new Metric<>("server.cpu.combined", cpuPerc.getCombined()));
		} catch (SigarException e) {
			log.warn("addMemMetrics Failed", e);
		}
	}

	protected void addResourceLimitMetrics(Collection<Metric<?>> result) {
		try {
			ResourceLimit resourceLimit = sigar.getResourceLimit();

			//			result.add(new Metric<>("server.resource.cpuCur", resourceLimit.getCpuCur()));
			//			result.add(new Metric<>("server.resource.cpuMax", resourceLimit.getCpuMax()));
			//			result.add(new Metric<>("server.resource.fileSizeCur", resourceLimit.getFileSizeCur()));
			//			result.add(new Metric<>("server.resource.fileSizeMax", resourceLimit.getFileSizeMax()));
			//			result.add(new Metric<>("server.resource.pipeSizeMax", resourceLimit.getPipeSizeMax()));
			//			result.add(new Metric<>("server.resource.pipeSizeCur", resourceLimit.getPipeSizeCur()));
			//			result.add(new Metric<>("server.resource.dataCur", resourceLimit.getDataCur()));
			//			result.add(new Metric<>("server.resource.dataMax", resourceLimit.getDataMax()));
			//			result.add(new Metric<>("server.resource.stackCur", resourceLimit.getStackCur()));
			//			result.add(new Metric<>("server.resource.stackMax", resourceLimit.getStackMax()));
			//			result.add(new Metric<>("server.resource.coreCur", resourceLimit.getCoreCur()));
			//			result.add(new Metric<>("server.resource.coreMax", resourceLimit.getCoreMax()));
			//			result.add(new Metric<>("server.resource.memoryCur", resourceLimit.getMemoryCur()));
			//			result.add(new Metric<>("server.resource.memoryMax", resourceLimit.getMemoryMax()));
			result.add(new Metric<>("server.resource.processesCur", resourceLimit.getProcessesCur()));
			result.add(new Metric<>("server.resource.processesMax", resourceLimit.getProcessesMax()));
			result.add(new Metric<>("server.resource.openFilesCur", resourceLimit.getOpenFilesCur()));
			result.add(new Metric<>("server.resource.openFilesMax", resourceLimit.getOpenFilesMax()));
			result.add(new Metric<>("server.resource.virtualMemoryCur", resourceLimit.getVirtualMemoryCur()));
			result.add(new Metric<>("server.resource.virtualMemoryMax", resourceLimit.getVirtualMemoryMax()));

		} catch (SigarException e) {
			log.warn("addResourceLimitMetrics Failed", e);
		}
	}

	protected void addFileSystemMetrics(Collection<Metric<?>> result) {
		try {
			FileSystem[] fileSystems = sigar.getFileSystemList();

			for (FileSystem fileSystem : fileSystems) {
				if (fileSystem.getType() == FileSystem.TYPE_LOCAL_DISK
					|| fileSystem.getType() == FileSystem.TYPE_NETWORK) {
					String name = fileSystem.getDevName();
					FileSystemUsage fileSystemUsage = sigar.getFileSystemUsage(name);
//					log.debug("fileSystemUsage = {}, {}", fileSystem.toMap(), fileSystemUsage);
					result.add(new Metric<>("server.fileSystem." + name + ".total", fileSystemUsage.getTotal()));
					result.add(new Metric<>("server.fileSystem." + name + ".free", fileSystemUsage.getFree()));
					result.add(new Metric<>("server.fileSystem." + name + ".used", fileSystemUsage.getUsed()));
					result.add(new Metric<>("server.fileSystem." + name + ".avail", fileSystemUsage.getAvail()));
					result.add(new Metric<>("server.fileSystem." + name + ".files", fileSystemUsage.getFiles()));
					result.add(new Metric<>("server.fileSystem." + name + ".freeFiles", fileSystemUsage.getFreeFiles()));
					result.add(new Metric<>("server.fileSystem." + name + ".diskReads", fileSystemUsage.getDiskReads()));
					result.add(new Metric<>("server.fileSystem." + name + ".diskWrites", fileSystemUsage.getDiskWrites()));
					result.add(new Metric<>("server.fileSystem." + name + ".diskReadBytes", fileSystemUsage.getDiskReadBytes()));
					result.add(new Metric<>("server.fileSystem." + name + ".diskWriteBytes", fileSystemUsage.getDiskWriteBytes()));
					result.add(new Metric<>("server.fileSystem." + name + ".diskQueue", fileSystemUsage.getDiskQueue()));
					result.add(new Metric<>("server.fileSystem." + name + ".diskServiceTime", fileSystemUsage.getDiskServiceTime()));
					result.add(new Metric<>("server.fileSystem." + name + ".usePercent", fileSystemUsage.getUsePercent()));
				}

			}

		} catch (SigarException e) {
			log.warn("addFileSystemMetrics Failed", e);
		}
	}

	protected void addDiskUsageMetrics(Collection<Metric<?>> result) {
		try {
			FileSystem[] fileSystems = sigar.getFileSystemList();

			for (FileSystem fileSystem : fileSystems) {
				if (fileSystem.getType() == FileSystem.TYPE_LOCAL_DISK
					|| fileSystem.getType() == FileSystem.TYPE_NETWORK) {
					String name = fileSystem.getDevName();
					DiskUsage diskUsage = sigar.getDiskUsage(name);
//					log.debug("diskUsage = {}, {}", fileSystem.toMap(), diskUsage);
					result.add(new Metric<>("server.disk." + name + ".reads", diskUsage.getReads()));
					result.add(new Metric<>("server.disk." + name + ".writes", diskUsage.getWrites()));
					result.add(new Metric<>("server.disk." + name + ".readBytes", diskUsage.getReadBytes()));
					result.add(new Metric<>("server.disk." + name + ".writeBytes", diskUsage.getWriteBytes()));
					result.add(new Metric<>("server.disk." + name + ".queue", diskUsage.getQueue()));
					result.add(new Metric<>("server.disk." + name + ".serviceTime", diskUsage.getServiceTime()));
				}
			}
		} catch (SigarException e) {
			log.warn("addDiskUsageMetrics Failed", e);
		}
	}

	protected void addNetStatMetrics(Collection<Metric<?>> result) {
		try {
			NetStat netStat = sigar.getNetStat();

			result.add(new Metric<>("server.netStat.tcpInboundTotal", netStat.getTcpInboundTotal()));
			result.add(new Metric<>("server.netStat.tcpOutboundTotal", netStat.getTcpOutboundTotal()));
			result.add(new Metric<>("server.netStat.allInboundTotal", netStat.getAllInboundTotal()));
			result.add(new Metric<>("server.netStat.allOutboundTotal", netStat.getAllOutboundTotal()));
		} catch (SigarException e) {
			log.warn("addNetStatMetrics Failed", e);
		}
	}

	protected void addNetInterfaceMetrics(Collection<Metric<?>> result) {
		try {
			String[] names = sigar.getNetInterfaceList();

			for (String name : names) {
				NetInterfaceStat netInterfaceStat = sigar.getNetInterfaceStat(name);

				result.add(new Metric<>("server.netInterface." + name + ".rxBytes", netInterfaceStat.getRxBytes()));
				result.add(new Metric<>("server.netInterface." + name + ".rxPackets", netInterfaceStat.getRxPackets()));
				result.add(new Metric<>("server.netInterface." + name + ".rxErrors", netInterfaceStat.getRxErrors()));
				result.add(new Metric<>("server.netInterface." + name + ".rxDropped", netInterfaceStat.getRxDropped()));
				result.add(new Metric<>("server.netInterface." + name + ".rxOverruns", netInterfaceStat.getRxOverruns()));
				result.add(new Metric<>("server.netInterface." + name + ".txBytes", netInterfaceStat.getTxBytes()));
				result.add(new Metric<>("server.netInterface." + name + ".txPackets", netInterfaceStat.getTxPackets()));
				result.add(new Metric<>("server.netInterface." + name + ".txErrors", netInterfaceStat.getTxErrors()));
				result.add(new Metric<>("server.netInterface." + name + ".txDropped", netInterfaceStat.getTxDropped()));
				result.add(new Metric<>("server.netInterface." + name + ".txOverruns", netInterfaceStat.getTxOverruns()));
				result.add(new Metric<>("server.netInterface." + name + ".txCollisions", netInterfaceStat.getTxCollisions()));
				result.add(new Metric<>("server.netInterface." + name + ".txCarrier", netInterfaceStat.getTxCarrier()));
				result.add(new Metric<>("server.netInterface." + name + ".speed", netInterfaceStat.getSpeed()));

			}

		} catch (SigarException e) {
			log.warn("addFileSystemMetrics Failed", e);
		}
	}
}
