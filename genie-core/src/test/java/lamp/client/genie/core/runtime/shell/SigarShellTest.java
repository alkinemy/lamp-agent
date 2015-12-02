package lamp.client.genie.core.runtime.shell;

import org.hyperic.sigar.Sigar;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kangwoo on 2015. 12. 2..
 */
public class SigarShellTest {

	@Test
	public void testTest1() throws Exception {
		SigarShell sigarShell = new SigarShell();
		sigarShell.test();
	}
}