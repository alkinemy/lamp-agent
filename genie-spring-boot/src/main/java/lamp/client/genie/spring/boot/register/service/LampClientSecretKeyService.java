package lamp.client.genie.spring.boot.register.service;

import lamp.client.genie.spring.boot.base.LampClientConstants;
import lamp.client.genie.spring.boot.base.exception.ErrorCode;
import lamp.client.genie.spring.boot.base.exception.Exceptions;
import lamp.client.genie.utils.FileUtils;
import lamp.client.genie.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lamp.client.genie.spring.boot.config.LampClientProperties;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Slf4j
public class LampClientSecretKeyService {

	@Getter
	private String secretKey;

	public LampClientSecretKeyService(LampClientProperties lampClientProperties) {
		String key = lampClientProperties.getSecretKey();
		File secretKeyFile = lampClientProperties.getSecretKeyFile();
		if (StringUtils.isBlank(key)) {
			if (secretKeyFile.exists()) {
				try {
					key = FileUtils.readFileToString(secretKeyFile);
				} catch (IOException e) {
					log.warn("Can't read from the secretKey file (" + secretKeyFile.getAbsolutePath() + ")", e);
				}
			}
		}
		if (StringUtils.isBlank(key)) {
			key = randomSecretKey();
			try {
				FileUtils.writeStringToFile(secretKeyFile, key, LampClientConstants.DEFAULT_CHARSET);
			} catch (IOException e) {
				log.warn("Can't write to the secretKey file (" + secretKeyFile.getAbsolutePath() + ")", e);
			}
		}
		this.secretKey = key;
	}


	protected String randomSecretKey() {
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			byte[] bytes = new byte[24];
			secureRandom.nextBytes(bytes);
			return new String(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw Exceptions.newException(ErrorCode.SECRET_KEY_GENERATION_FAILED, e);
		}
	}

}
