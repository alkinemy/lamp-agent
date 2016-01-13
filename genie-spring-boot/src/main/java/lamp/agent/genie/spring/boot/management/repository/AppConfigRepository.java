package lamp.agent.genie.spring.boot.management.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.MountPoint;
import lamp.agent.genie.spring.boot.base.impl.AppConfigImpl;
import lamp.agent.genie.utils.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AppConfigRepository {

	private static final String CONFIG_JSON = "config.json";

	@Autowired
	private LampContext lampContext;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void save(AppConfig appConfig) {
		File directory = lampContext.getAppDirectory(appConfig.getId());
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(directory, CONFIG_JSON);
		try {
			objectMapper.writeValue(file, appConfig);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_SAVE_FAILED, e);
		}
	}

	public List<AppConfig> findAll() {
		List<AppConfig> appConfigList = new ArrayList<>();
		File directory = lampContext.getAppDirectory();
		File[] dirs = directory.listFiles(MountPoint.DIRECTORY);
		if (ArrayUtils.isNotEmpty(dirs)) {
			for (File dir : dirs) {
				String appId = dir.getName();
				try {
					AppConfig appConfig = findOne(appId);
					if (appConfig != null) {
						appConfigList.add(appConfig);
					}
				} catch (Exception e) {
					log.info("AppConfig load failed", e);
				}
			}
		}
		return appConfigList;
	}

	public AppConfig findOne(String id) {
		File directory = lampContext.getAppDirectory(id);
		File file = new File(directory, CONFIG_JSON);

		if (!file.exists()) {
			return null;
		}
		try {
			return objectMapper.readValue(file, AppConfigImpl.class);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_READ_FAILED, e);
		}
	}

	public void delete(AppConfig appConfig) {
		File directory = lampContext.getAppDirectory(appConfig.getId());
		File file = new File(directory, CONFIG_JSON);
		file.delete();
	}

}
