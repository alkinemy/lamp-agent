package lamp.agent.genie.core.runtime.shell;

import lamp.agent.genie.core.support.sigar.SigarNativeLoader;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.core.exception.ShellException;
import lamp.agent.genie.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.jni.ArchNotSupportedException;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

@Slf4j
public class SigarShell implements Shell {

	static {
		try {
			SigarNativeLoader.loadNativeLibrary();
		} catch (ArchNotSupportedException e) {
			throw new ShellException("Unsupported platform", e);
		}
	}

	@Getter
	private Sigar sigar = new Sigar();

	@Override
	public AppProcessState getProcessState(String pid) {
		try {
			ProcState procState = sigar.getProcState(StringUtils.trim(pid));
			if (procState.getState() == ProcState.SLEEP
					|| procState.getState() == ProcState.RUN
					|| procState.getState() == ProcState.STOP
					|| procState.getState() == ProcState.ZOMBIE
					|| procState.getState() == ProcState.IDLE) {
				return AppProcessState.RUNNING;
			}
		} catch (SigarException e) {
			throw new ShellException("Unable to get process status : " + pid, e);
		}
		return AppProcessState.NOT_RUNNING;
	}

	@Override
	public void kill(String pid, Shell.Signal signal) {
		try {
			sigar.kill(pid, Sigar.getSigNum(signal.name()));
		} catch (SigarException e) {
			throw new ShellException("kill " + pid + " failed", e);
		}
	}

	@Override public void close() {
		sigar.close();
	}

}
