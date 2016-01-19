package lamp.agent.genie.utils;

import java.nio.charset.Charset;

public abstract class StringUtils extends org.apache.commons.lang3.StringUtils { //NOSONAR

	public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
	public static final Charset CHARSET_ISO_8859_1 = Charset.forName("ISO-8859-1");

	public static String utf8ToIso88591(String str) {
		if (str == null) {
			return str;
		}
		return new String(str.getBytes(CHARSET_UTF_8), CHARSET_ISO_8859_1);
	}

}
