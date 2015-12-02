package lamp.client.genie.core.runtime.shell.sigar;

import static org.apache.commons.lang.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang.SystemUtils.IS_OS_MAC_OSX;

public class NativeLoader {
	private static Logger logger = LoggerFactory.getLogger(NativeLoader.class);

	public static void loadSigarNative() {
		try {
			if (IS_OS_MAC_OSX) {
				NativeUtils.loadLibraryFromJar("/native/libsigar-universal64-macosx.dylib");
			} else if (IS_OS_LINUX) {
				NativeUtils.loadLibraryFromJar("/native/libsigar-amd64-linux.so");
			} else {
				NativeUtils.loadLibraryFromJar("/native/sigar-amd64-winnt.dll");
			}
		} catch (IOException e) {
			e.printStackTrace(); // This is probably not the best way to handle exception :-)
		}
	}
}
