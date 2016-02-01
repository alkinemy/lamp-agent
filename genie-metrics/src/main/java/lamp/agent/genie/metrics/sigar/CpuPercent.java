package lamp.agent.genie.metrics.sigar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hyperic.sigar.CpuPerc;

@Getter
@RequiredArgsConstructor
public class CpuPercent {

	private final double user;
	private final double sys;
	private final double nice;
	private final double waiting;
	private final double idle;
	private final double irq;

	public static CpuPercent from(CpuPerc cp) {
		return new CpuPercent(
			cp.getUser(), cp.getSys(),
			cp.getNice(), cp.getWait(),
			cp.getIdle(), cp.getIrq());
	}

}
