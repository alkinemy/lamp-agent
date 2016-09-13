package lamp.agent.genie.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class HostUtils {

	public static String getLocalHostName() {
		try {
			return StringUtils.trim(execReadToString("hostname"));
		} catch (IOException e) {
			return null;
		}
	}

	protected static String execReadToString(String execCommand) throws IOException {
		Process proc = Runtime.getRuntime().exec(execCommand);
		try (InputStream stream = proc.getInputStream()) {
			try (Scanner s = new Scanner(stream).useDelimiter("\\A")) {
				return s.hasNext() ? s.next() : "";
			}
		}
	}

}
