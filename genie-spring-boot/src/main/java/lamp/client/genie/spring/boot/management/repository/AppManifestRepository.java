package lamp.client.genie.spring.boot.management.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.context.LampContext;
import lamp.client.genie.core.context.MountPoint;
import lamp.client.genie.spring.boot.base.impl.AppManifestImpl;
import lamp.client.genie.utils.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import lamp.client.genie.spring.boot.base.exception.ErrorCode;
import lamp.client.genie.spring.boot.base.exception.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AppManifestRepository {

	private static final String MANIFEST_JSON = "manifest.json";

	@Autowired
	private LampContext lampContext;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void save(AppManifest appManifest) {
		File directory = lampContext.getAppDirectory(appManifest.getId());
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(directory, MANIFEST_JSON);
		try {
			objectMapper.writeValue(file, appManifest);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_MANIFEST_SAVE_FAILED, e);
		}
	}

	public List<AppManifest> findAll() {
		List<AppManifest> appManifestList = new ArrayList<>();
		File directory = lampContext.getAppDirectory();
		File[] dirs = directory.listFiles(MountPoint.DIRECTORY);
		if (ArrayUtils.isNotEmpty(dirs)) {
			for (File dir : dirs) {
				String appId = dir.getName();
				try {
					AppManifest appManifest = findOne(appId);
					if (appManifest != null) {
						appManifestList.add(appManifest);
					}
				} catch (Exception e) {
					log.info("AppManifest load failed", e);
				}
			}
		}
		return appManifestList;
	}

	public AppManifest findOne(String id) {
		File directory = lampContext.getAppDirectory(id);
		File file = new File(directory, MANIFEST_JSON);

		if (!file.exists()) {
			return null;
		}
		try {
			return objectMapper.readValue(file, AppManifestImpl.class);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_MANIFEST_READ_FAILED, e);
		}
	}

	public void delete(AppManifest appManifest) {
		File directory = lampContext.getAppDirectory(appManifest.getId());
		File file = new File(directory, MANIFEST_JSON);
		file.delete();
	}

}
