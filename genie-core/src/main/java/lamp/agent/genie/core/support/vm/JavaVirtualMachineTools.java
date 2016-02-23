package lamp.agent.genie.core.support.vm;

import lombok.extern.slf4j.Slf4j;

import javax.tools.ToolProvider;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JavaVirtualMachineTools {

	public static List<JavaVirtualMachine> vmList() {
		List<JavaVirtualMachine> javaVirtualMachineList = new ArrayList();
		try {
			List<com.sun.tools.attach.VirtualMachineDescriptor> vms = com.sun.tools.attach.VirtualMachine.list();
			for (com.sun.tools.attach.VirtualMachineDescriptor desc : vms) {
				JavaVirtualMachine vmd = new JavaVirtualMachine();
				vmd.setId(desc.id());
				vmd.setDisplayName(desc.displayName());

				javaVirtualMachineList.add(vmd);
			}
		} catch (Throwable t) {
			log.warn("Cannot read VirtualMachine list", t);
		}
		return javaVirtualMachineList;
	}

	public static String getPidByDisplayName(String displayName) {
		try {
			List<com.sun.tools.attach.VirtualMachineDescriptor> vms = com.sun.tools.attach.VirtualMachine.list();
			for (com.sun.tools.attach.VirtualMachineDescriptor desc : vms) {
				if (desc.displayName().startsWith(displayName)) {
					return desc.id();
				}
			}
		} catch (Throwable t) {
			log.warn("Cannot read VirtualMachine list", t);
		}
		return null;
	}
}

