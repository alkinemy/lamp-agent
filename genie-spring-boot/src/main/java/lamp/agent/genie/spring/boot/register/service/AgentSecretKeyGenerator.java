package lamp.agent.genie.spring.boot.register.service;

import lamp.agent.genie.spring.boot.base.LampClientConstants;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.config.LampAgentProperties;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.utils.FileUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Slf4j
public class AgentSecretKeyGenerator {

	public AgentSecretKeyGenerator(LampAgentProperties lampClientProperties) {
		String key = lampClientProperties.getSecretKey();
		if (StringUtils.isBlank(key)) {
			File secretKeyFile = lampClientProperties.getSecretKeyFile();
			if (secretKeyFile.exists()) {
				try {
					key = FileUtils.readFileToString(secretKeyFile);
				} catch (IOException e) {
					log.warn("Can't read from the secretKey file (" + secretKeyFile.getAbsolutePath() + ")", e);
				}
			} else {
				key = randomSecretKey();
				try {
					FileUtils.writeStringToFile(secretKeyFile, key, LampClientConstants.DEFAULT_CHARSET);
				} catch (IOException e) {
					log.warn("Can't write to the secretKey file (" + secretKeyFile.getAbsolutePath() + ")", e);
				}
			}
			lampClientProperties.setSecretKey(key);
		}
	}


	protected String randomSecretKey() {
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			byte[] bytes = new byte[24];
			secureRandom.nextBytes(bytes);
			return Base64Utils.encodeToString(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw Exceptions.newException(ErrorCode.SECRET_KEY_GENERATION_FAILED, e);
		}
	}

}
