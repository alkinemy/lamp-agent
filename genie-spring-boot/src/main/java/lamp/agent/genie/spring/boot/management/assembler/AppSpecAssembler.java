package lamp.agent.genie.spring.boot.management.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.spring.boot.base.impl.AppSpecImpl;
import lamp.agent.genie.spring.boot.management.model.AppRegisterForm;
import lamp.agent.genie.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class AppSpecAssembler extends AbstractListAssembler<AppRegisterForm, AppSpec> {

	@Autowired
	private LampContext lampContext;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override protected AppSpec doAssemble(AppRegisterForm form) {
		AppSpecImpl appConfig = new AppSpecImpl();
		BeanUtils.copyProperties(form, appConfig, AppSpecImpl.class);

		if (StringUtils.isNotBlank(form.getParameters())) {
			if ("JSON".equalsIgnoreCase(form.getParametersType())) {
				try {
					LinkedHashMap<String, Object> parameters = objectMapper.readValue(form.getParameters(), LinkedHashMap.class);
					appConfig.setParameters(parameters);
				} catch (IOException e) {
					throw Exceptions.newException(ErrorCode.INVALID_PARAMETERS);
				}
			} else {
				Properties properties = new Properties();
				try (Reader reader = new StringReader(form.getParameters())) {
					properties.load(reader);
					Map<String, Object> parameters = new LinkedHashMap<>();
					for (String key : properties.stringPropertyNames()) {
						parameters.put(key, properties.getProperty(key));
					}
					appConfig.setParameters(parameters);
				} catch (IOException e) {
					throw Exceptions.newException(ErrorCode.INVALID_PARAMETERS);
				}
			}
		}

		if (StringUtils.isBlank(appConfig.getAppDirectory())) {
			File appDirectory = new File(lampContext.getAppDirectory(), appConfig.getId() + "/app");
			appConfig.setAppDirectory(appDirectory.getAbsolutePath());
		}

		return appConfig;
	}

}
