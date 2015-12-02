package lamp.client.genie.core.runtime.shell;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SigarShell {

	private Sigar sigar = new Sigar();

	public void test() throws SigarException {
		sigar.getCpu();
	}
}
