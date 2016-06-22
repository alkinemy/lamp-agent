package lamp.agent.genie.spring.boot.management.controller;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import lamp.agent.genie.spring.boot.management.model.*;
import lamp.agent.genie.spring.boot.management.service.DockerClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/container")
public class ContainerController {

	@Autowired(required = false)
	private DockerClientService dockerClientService;


	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Container> list(@RequestParam(name = "showAll", defaultValue = "false") Boolean showAll) {
		return dockerClientService.listContainers(showAll);
	}

//	@RequestMapping(value = "/{id:.+}", method = RequestMethod.GET)
//	public AppDto loadApp(@PathVariable("id") String id) {
//		App app = appManagementService.loadApp(id);
//		return smartAssembler.assemble(app, App.class, AppDto.class);
//	}

//	@RequestMapping(value = "", method = RequestMethod.POST)
//	@ResponseBody
//	public InspectContainerResponse runContainer(@RequestBody DockerApp form) {
//		log.error("form = {}", form);
//		return dockerClientService.runContainer(form);
//	}
//
//	@RequestMapping(value = "/{id}/stop", method = RequestMethod.GET)
//	public void stopContainer(@PathVariable("id") String id) {
//		log.info("[App] '{}' Stopping", id);
//		dockerClientService.stopContainer(id);
//		log.info("[App] '{}' Stopped", id);
//	}
//
//	@RequestMapping(value = "/{id}/stats", method = RequestMethod.GET)
//	public Statistics stats(@PathVariable("id") String id) {
//		return dockerClientService.getStats(id);
//	}
	//
//
//	@RequestMapping(value = "/{id:.+}", method = RequestMethod.POST)
//	public void update(@PathVariable("id") String id, AppUpdateForm form) {
//		appManagementService.update(id, form);
//	}
//
//	@RequestMapping(value = "/{id}/file", method = RequestMethod.POST)
//	public void updateFile(@PathVariable("id") String id, AppFileUpdateForm form) {
//		appManagementService.updateFile(id, form);
//	}
//
//	@RequestMapping(value = "/{id:.+}", method = RequestMethod.DELETE)
//	public void deregister(@PathVariable("id") String id, @RequestParam(name = "forceStop", defaultValue = "false") Boolean forceStop) {
//		appManagementService.deregister(id, forceStop);
//	}
//
//	@RequestMapping(value = "/{id}/start", method = RequestMethod.GET)
//	public void start(@PathVariable("id") String id) {
//		log.info("[App] '{}' Starting", id);
//		appManagementService.start(id);
//		log.info("[App] '{}' Started", id);
//	}
//
//	@RequestMapping(value = "/{id}/stop", method = RequestMethod.GET)
//	public void stop(@PathVariable("id") String id) {
//		log.info("[App] '{}' Stopping", id);
//		appManagementService.stop(id);
//		log.info("[App] '{}' Stopped", id);
//	}
//
//	@RequestMapping(value = "/{id}/status", method = RequestMethod.GET)
//	public AppStatus status(@PathVariable("id") String id) {
//		return appManagementService.status(id);
//	}
//
//	@RequestMapping(value = "/{id}/log", method = RequestMethod.GET)
//	public List<LogFile> logFiles(@PathVariable("id") String id) {
//		return appManagementService.getLogFiles(id);
//	}
//
//	@RequestMapping(value = "/{id}/log/{filename:.+}", method = RequestMethod.GET)
//	public Resource logFiles(@PathVariable("id") String id, @PathVariable("filename") String filename) {
//		return appManagementService.getLogFileResource(id, filename);
//	}
//
//	@RequestMapping(value = "/{id}/stdOutFile", method = RequestMethod.GET)
//	public Resource stdOutFile(@PathVariable("id") String id) {
//		Resource resource = appManagementService.getStdOutFileResource(id);
//		return resource;
//	}
//
//	@RequestMapping(value = "/{id}/stdErrFile", method = RequestMethod.GET)
//	@ResponseBody
//	public Resource stdErrFile(@PathVariable("id") String id) {
//		Resource resource = appManagementService.getStdErrFileResource(id);
//		return resource;
//	}

}
