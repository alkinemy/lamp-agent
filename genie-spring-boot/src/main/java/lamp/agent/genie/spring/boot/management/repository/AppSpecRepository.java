package lamp.agent.genie.spring.boot.management.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.MountPoint;
import lamp.agent.genie.spring.boot.base.impl.AppSpecImpl;
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
public class AppSpecRepository {

	private static final String SPEC_FILE = "app-spec.json";

	@Autowired
	private LampContext lampContext;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void save(AppSpec appSpec) {
		File directory = lampContext.getAppMetaInfoDirectory(appSpec.getId());
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(directory, SPEC_FILE);
		try {
			objectMapper.writeValue(file, appSpec);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_SAVE_FAILED, e);
		}
	}

	public List<AppSpec> findAll() {
		List<AppSpec> appSpecList = new ArrayList<>();
		File directory = lampContext.getAppDirectory();
		File[] dirs = directory.listFiles(MountPoint.DIRECTORY);
		if (ArrayUtils.isNotEmpty(dirs)) {
			for (File dir : dirs) {
				String artifactId = dir.getName();
				try {
					AppSpec appSpec = findOne(artifactId);
					if (appSpec != null) {
						appSpecList.add(appSpec);
					}
				} catch (Exception e) {
					log.info("AppSpec load failed", e);
				}
			}
		}
		return appSpecList;
	}

	public AppSpec findOne(String id) {
		File directory = lampContext.getAppMetaInfoDirectory(id);
		File file = new File(directory, SPEC_FILE);

		if (!file.exists()) {
			return null;
		}
		try {
			return objectMapper.readValue(file, AppSpecImpl.class);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_READ_FAILED, e);
		}
	}

	public void delete(AppSpec appSpec) {
		File directory = lampContext.getAppMetaInfoDirectory(appSpec.getId());
		File file = new File(directory, SPEC_FILE);
		file.delete();
	}

}
