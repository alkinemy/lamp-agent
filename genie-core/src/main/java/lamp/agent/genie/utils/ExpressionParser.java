package lamp.agent.genie.utils;

import java.util.Map;

public interface ExpressionParser {

	String getValue(String value, Map<String, Object> parameters);

}
