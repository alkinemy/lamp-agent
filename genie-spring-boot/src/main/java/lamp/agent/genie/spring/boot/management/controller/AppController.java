package lamp.agent.genie.spring.boot.management.controller;

import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.management.model.AppDto;
import lamp.agent.genie.spring.boot.management.model.AppRegisterForm;
import lamp.agent.genie.spring.boot.management.model.AppUpdateFileForm;
import lamp.agent.genie.spring.boot.management.model.AppUpdateSpecForm;
import lamp.agent.genie.spring.boot.management.service.AppManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/app")
public class AppController {

	@Autowired
	private AppManagementService appManagementService;

	@Autowired
	private SmartAssembler smartAssembler;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<AppDto> list() {
		List<App> apps = appManagementService.getApps();
		return smartAssembler.assemble(apps, App.class, AppDto.class);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public AppDto get(@PathVariable("id") String id) {
		App app = appManagementService.getApp(id);
		return smartAssembler.assemble(app, App.class, AppDto.class);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public void register(AppRegisterForm form) {
		appManagementService.register(form);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public void updateSpec(@PathVariable("id") String id, AppUpdateSpecForm form) {
		appManagementService.updateSpec(id, form);
	}

	@RequestMapping(value = "/{id}/file", method = RequestMethod.POST)
	public void updateFile(@PathVariable("id") String id, AppUpdateFileForm form) {
		appManagementService.updateFile(id, form);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deregister(@PathVariable("id") String id, @RequestParam(name = "forceStop", defaultValue = "false") Boolean forceStop) {
		appManagementService.deregister(id, forceStop);
	}

	@RequestMapping(value = "/{id}/start", method = RequestMethod.GET)
	public void start(@PathVariable("id") String id) {
		log.info("[App] '{}' Starting", id);
		appManagementService.start(id);
		log.info("[App] '{}' Started", id);
	}

	@RequestMapping(value = "/{id}/stop", method = RequestMethod.GET)
	public void stop(@PathVariable("id") String id) {
		log.info("[App] '{}' Stopping", id);
		appManagementService.stop(id);
		log.info("[App] '{}' Stopped", id);
	}

	@RequestMapping(value = "/{id}/status", method = RequestMethod.GET)
	public AppStatus status(@PathVariable("id") String id) {
		return appManagementService.status(id);
	}

	@RequestMapping(value = "/{id}/logfile", method = RequestMethod.GET, produces = "text/plain")
	public Resource logfile(@PathVariable("id") String id) {
		Resource resource = appManagementService.getLogFileResource(id);
		return resource;
	}

	@RequestMapping(value = "/{id}/systemLogfile", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	public Resource systemLogfile(@PathVariable("id") String id) {
		Resource resource = appManagementService.getSystemLogFileResource(id);
		return resource;
	}

}
