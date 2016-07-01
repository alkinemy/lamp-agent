package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.app.simple.SimpleAppContext;
import lamp.agent.genie.spring.boot.management.model.LogFile;
import lamp.agent.genie.utils.FilenameUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AppLogService {


	@Autowired
	private AppService appService;


	public List<LogFile> getLogFiles(String id) {
//		App app = appService.getApp(id);
//		String logDirectory = app.getAppContext().getLogDirectory();
//		if (StringUtils.isNotBlank(logDirectory)) {
//			List<LogFile> logFiles = new ArrayList<>();
//			File dir = new File(logDirectory);
//			File[] files = dir.listFiles();
//			if (files != null) {
//				for (File file : files) {
//					LogFile logFile = new LogFile();
//					logFile.setName(file.getName());
//					logFile.setSize(file.length());
//					logFile.setLastModified(new Date(file.lastModified()));
//
//					logFiles.add(logFile);
//				}
//			}
//			return logFiles;
//		}
		return Collections.emptyList();
	}

	public Resource getLogFileResource(String id, String filename) {
		App app = appService.getApp(id);
		AppContext appContext = app.getAppContext();
		if (appContext instanceof SimpleAppContext) {
			String logDirectory = ((SimpleAppContext) appContext).getParsedAppContainer().getLogDirectory();
			if (StringUtils.isNotBlank(logDirectory)) {
				filename = FilenameUtils.getName(filename);
				File file = new File(logDirectory, filename);
				if (file.exists()) {
					return new FileSystemResource(file);
				}
			}
		}

		return null;
	}

	public Resource getStdOutFileResource(String id) throws IOException {
		App app = appService.getApp(id);
		AppContext appContext = app.getAppContext();
		InputStream inputStream = appContext.getStdOutInputStream();
		if (inputStream != null) {
			return new InputStreamResource(inputStream);
		}
		return null;
	}

	public Resource getStdErrFileResource(String id) throws IOException {
		App app = appService.getApp(id);
		AppContext appContext = app.getAppContext();
		InputStream inputStream = appContext.getStdErrInputStream();
		if (inputStream != null) {
			return new InputStreamResource(inputStream);
		}
		return null;
	}




}
