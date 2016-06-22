package lamp.agent.genie.spring.boot.management.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppImpl;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.MountPoint;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.utils.ArrayUtils;
import lamp.agent.genie.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AppRepository {

	private static final String SPEC_FILE = "app-spec.json";

	@Autowired
	private LampContext lampContext;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void save(App app) {
		File directory = lampContext.getAppMetaInfoDirectory(app.getId());
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(directory, SPEC_FILE);
		try {
			objectMapper.writeValue(file, app);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_SAVE_FAILED, e);
		}
	}

	public List<App> findAll() {
		List<App> appList = new ArrayList<>();
		File directory = lampContext.getAppDirectory();
		File[] dirs = directory.listFiles(MountPoint.DIRECTORY);
		if (ArrayUtils.isNotEmpty(dirs)) {
			for (File dir : dirs) {
				String id = dir.getName();
				try {
					App app = findOne(id);
					if (app != null) {
						appList.add(app);
					}
				} catch (Exception e) {
					log.info("App loadApp failed", e);
				}
			}
		}
		return appList;
	}

	public App findOne(String id) {
		File directory = lampContext.getAppMetaInfoDirectory(id);
		File file = new File(directory, SPEC_FILE);

		if (!file.exists()) {
			return null;
		}
		try {
			return objectMapper.readValue(file, AppImpl.class);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_READ_FAILED, e);
		}
	}

	public void delete(App app) {
		File directory = lampContext.getAppMetaInfoDirectory(app.getId());
		FileUtils.deleteQuietly(directory);
	}

}
