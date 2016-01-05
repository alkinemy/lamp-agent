package lamp.agent.genie.metrics.sigar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hyperic.sigar.CpuPerc;

@Getter
@RequiredArgsConstructor
public class CpuUsage {

	private final double user;
	private final double sys;
	private final double nice;
	private final double waiting;
	private final double idle;
	private final double irq;

	public static CpuUsage from(CpuPerc cp) {
		return new CpuUsage(
			cp.getUser(), cp.getSys(),
			cp.getNice(), cp.getWait(),
			cp.getIdle(), cp.getIrq());
	}

}
