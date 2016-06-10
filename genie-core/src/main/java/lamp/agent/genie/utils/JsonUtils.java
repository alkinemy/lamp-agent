package lamp.agent.genie.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class JsonUtils {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String stringify(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Json process failed", e);
		}
	}

	public static <T> T parse(String content, Class<T> valueType) {
		try {
			return objectMapper.readValue(content, valueType);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Json process failed", e);
		} catch (IOException e) {
			throw new RuntimeException("Json process failed", e);
		}
	}
}
