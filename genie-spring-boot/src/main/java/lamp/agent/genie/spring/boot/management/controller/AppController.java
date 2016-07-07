package lamp.agent.genie.spring.boot.management.controller;

import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.management.model.AppDeployForm;
import lamp.agent.genie.spring.boot.management.model.AppDto;
import lamp.agent.genie.spring.boot.management.model.AppRedeployForm;
import lamp.agent.genie.spring.boot.management.service.AppLogService;
import lamp.agent.genie.spring.boot.management.service.AppManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/app")
public class AppController {

	@Autowired
	private AppManagementService appManagementService;

	@Autowired
	private AppLogService appLogService;

	@Autowired
	private SmartAssembler smartAssembler;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<AppDto> list() {
		List<App> appInstances = appManagementService.getApps();
		return smartAssembler.assemble(appInstances, App.class, AppDto.class);
	}

	@RequestMapping(value = "/{id:.+}", method = RequestMethod.GET)
	public AppDto get(@PathVariable("id") String id) {
		App appInstance = appManagementService.getApp(id);
		return smartAssembler.assemble(appInstance, App.class, AppDto.class);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public void deploy(AppDeployForm form) {
		appManagementService.deploy(form);
	}

	@RequestMapping(value = "/{id:.+}", method = RequestMethod.POST)
	public void redeploy(@PathVariable("id") String id, AppRedeployForm form) {
		appManagementService.redeploy(id, form);
	}

//	@RequestMapping(value = "/{id}/file", method = RequestMethod.POST)
//	public void updateFile(@PathVariable("id") String id, AppFileUpdateForm form) {
//		appManagementService.updateFile(id, form);
//	}

	@RequestMapping(value = "/{id:.+}", method = RequestMethod.DELETE)
	public void undeploy(@PathVariable("id") String id, @RequestParam(name = "forceStop", defaultValue = "false") Boolean forceStop) {
		appManagementService.undeploy(id, forceStop);
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

//	@RequestMapping(value = "/{id}/log", method = RequestMethod.GET)
//	public List<LogFile> logFiles(@PathVariable("id") String id) {
//		return appManagementService.getLogFiles(id);
//	}
//
//	@RequestMapping(value = "/{id}/log/{filename:.+}", method = RequestMethod.GET)
//	public Resource logFiles(@PathVariable("id") String id, @PathVariable("filename") String filename) {
//		return appLogService.getLogFileResource(id, filename);
//	}

	@RequestMapping(value = "/{id}/stdout", method = RequestMethod.GET)
	public Resource stdOutFile(@PathVariable("id") String id) throws IOException {
		Resource resource = appLogService.getStdOutFileResource(id);
		return resource;
	}

	@RequestMapping(value = "/{id}/stderr", method = RequestMethod.GET)
	@ResponseBody
	public Resource stdErrFile(@PathVariable("id") String id) throws IOException {
		Resource resource = appLogService.getStdErrFileResource(id);
		return resource;
	}

}
