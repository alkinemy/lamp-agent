package lamp.agent.genie.core.runtime.shell;

import org.junit.Test;


public class SigarShellTest {

	@Test
	public void testGetProcessState() throws Exception {
		SigarShell sigarShell = new SigarShell();
		Long pid = sigarShell.getProcessId("State.Name.eq=java,Args.*.ct=1QuorumPeerMain");
		System.out.println(sigarShell.getSigar().getProcTime(1313123123));
//		System.out.println("State=" + sigarShell.getSigar().getProcState(pid));
	}
}