package lamp.agent.genie.spring.boot.management.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class InstallConfigRepository {

	private static final String MANIFEST_JSON = "install.json";

	@Autowired
	private LampContext lampContext;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void save(InstallConfig manifest) {
		File directory = lampContext.getAppDirectory(manifest.getId());
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(directory, MANIFEST_JSON);
		try {
			objectMapper.writeValue(file, manifest);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_SAVE_FAILED, e);
		}
	}
	public InstallConfig findOne(String id) {
		File directory = lampContext.getAppDirectory(id);
		File file = new File(directory, MANIFEST_JSON);

		if (!file.exists()) {
			return null;
		}
		try {
			return objectMapper.readValue(file, InstallConfig.class);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_READ_FAILED, e);
		}
	}

	public void delete(InstallConfig manifest) {
		File directory = lampContext.getAppDirectory(manifest.getId());
		File file = new File(directory, MANIFEST_JSON);
		file.delete();
	}

}
