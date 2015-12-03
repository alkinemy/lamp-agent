package lamp.client.genie.spring.boot.management.controller;

import lamp.client.genie.spring.boot.management.form.AppRegistrationForm;
import lamp.client.genie.spring.boot.management.service.AppManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("/api/apps")
public class AppController {

	@Autowired
	private AppManagementService appManagementService;

	@RequestMapping(value = "", method = RequestMethod.POST)
	public void register(@RequestBody AppRegistrationForm form) {
		appManagementService.register(form);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deregister(@PathVariable("id") String id, @RequestParam(name = "forceStop", defaultValue = "false") Boolean forceStop) {
		appManagementService.deregister(id, forceStop);
	}

//
//	@RequestMapping(value = "/{id}/start", method = RequestMethod.POST)
//	public void start(@PathVariable("id") String id) {
//		log.info("[App] '{}' Starting", id);
//
//		log.info("[App] '{}' Started", id);
//	}
//
//	@RequestMapping(value = "/{id}/stop", method = RequestMethod.POST)
//	public void stop(@PathVariable("id") String id) {
//		log.info("[App] '{}' Stopping", id);
//
//		log.info("[App] '{}' Stopped", id);
//	}
//
//	@RequestMapping(value = "/{id}/systemLog", method = RequestMethod.GET)
//	@ResponseBody
//	public File systemLog(@PathVariable("id") String id) {
//		log.info("[App] '{}' systemLog", id);
//		File file = null; // agentManagementService.getSystemLogFile(id);
//		// FIXME 파일 다운드로 구현????
//		log.info("[App] '{}' systemLog", id);
//		return file;
//	}
}
