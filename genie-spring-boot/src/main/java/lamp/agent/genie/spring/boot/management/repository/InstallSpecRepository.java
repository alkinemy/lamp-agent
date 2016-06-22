package lamp.agent.genie.spring.boot.management.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.app.simple.install.InstallSpec;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class InstallSpecRepository {

	private static final String SPEC_FILE = "install-spec.json";

	@Autowired
	private LampContext lampContext;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void save(InstallSpec installSpec) {
		File directory = lampContext.getAppMetaInfoDirectory(installSpec.getId());
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(directory, SPEC_FILE);
		try {
			objectMapper.writeValue(file, installSpec);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_SAVE_FAILED, e);
		}
	}

	public InstallSpec findOne(String id) {
		File directory = lampContext.getAppMetaInfoDirectory(id);
		File file = new File(directory, SPEC_FILE);

		if (!file.exists()) {
			return null;
		}
		try {
			return objectMapper.readValue(file, InstallSpec.class);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_READ_FAILED, e);
		}
	}

	public void delete(InstallSpec manifest) {
		File directory = lampContext.getAppMetaInfoDirectory(manifest.getId());
		File file = new File(directory, SPEC_FILE);
		file.delete();
	}

}
