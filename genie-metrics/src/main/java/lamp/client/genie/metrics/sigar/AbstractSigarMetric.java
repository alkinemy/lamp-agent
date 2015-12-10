package lamp.client.genie.metrics.sigar;

import org.hyperic.sigar.Sigar;

public abstract class AbstractSigarMetric {

	protected final Sigar sigar;

	protected AbstractSigarMetric(Sigar sigar) {
		this.sigar = sigar;
	}

}
