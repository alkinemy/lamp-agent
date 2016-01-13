package lamp.agent.genie.spring.boot.management.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.core.App;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class AppCorrectStatusRepository {

	private static final String STATUS_JSON = "status.json";

	@Autowired
	private LampContext lampContext;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void save(String id, AppStatus correctStatus) {
		File directory = lampContext.getAppDirectory(id);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(directory, STATUS_JSON);
		try {
			objectMapper.writeValue(file, correctStatus);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CORRECT_STATUS_SAVE_FAILED, e);
		}
	}


	public AppStatus findOne(String id) {
		File directory = lampContext.getAppDirectory(id);
		File file = new File(directory, STATUS_JSON);

		if (!file.exists()) {
			return null;
		}
		try {
			return objectMapper.readValue(file, AppStatus.class);
		} catch (IOException e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_READ_FAILED, e);
		}
	}

	public void delete(String id) {
		File directory = lampContext.getAppDirectory(id);
		File file = new File(directory, STATUS_JSON);
		file.delete();
	}

}
