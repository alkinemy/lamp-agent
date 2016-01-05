package lamp.agent.genie.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class VariableReplaceUtils {

	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
	private static final String EMPTY = StringUtils.EMPTY;

	public static String replaceVariables(String template, Object variables) {
		if (template == null) {
			return null;
		}

		StringBuilder stringBuilder = new StringBuilder();

		Matcher m = NAMES_PATTERN.matcher(template);
		int end = 0;
		while (m.find()) {
			stringBuilder.append(substring(template, end, m.start()));
			String match = m.group(1);
			String value = getProperty(variables, match);
			stringBuilder.append(value);
			end = m.end();
		}
		stringBuilder.append(substring(template, end, template.length()));
		return stringBuilder.toString();
	}

	protected static String getProperty(Object bean, String name) {
		try {
			return BeanUtils.getProperty(bean, name);
		} catch (Exception e) {
			// Ignore
			log.debug("BeanUtils.getProperty() failed", e);
		}
		return EMPTY;
	}

	protected static String substring(String string, int start, int end) {
		if (start == end) {
			return EMPTY;
		}
		return string.substring(start, end);
	}

}
