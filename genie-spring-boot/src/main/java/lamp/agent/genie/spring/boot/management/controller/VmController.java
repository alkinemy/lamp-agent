package lamp.agent.genie.spring.boot.management.controller;

import lamp.agent.genie.core.support.vm.JavaVirtualMachine;
import lamp.agent.genie.core.support.vm.JavaVirtualMachineTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vm")
public class VmController {

	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<JavaVirtualMachine> list() {
		return JavaVirtualMachineTools.vmList();
	}

}
