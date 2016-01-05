package lamp.agent.genie.core.external.sigar;

import lamp.agent.genie.core.exception.SigarException;
import lamp.agent.genie.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.jni.ArchName;
import org.hyperic.jni.ArchNotSupportedException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class SigarNativeLoader {

	private static final String osName = System.getProperty("os.name");
	public static final boolean IS_WIN32;
	public static final boolean IS_AIX;
	public static final boolean IS_HPUX;
	public static final boolean IS_SOLARIS;
	public static final boolean IS_LINUX;
	public static final boolean IS_DARWIN;
	public static final boolean IS_OSF1;
	public static final boolean IS_FREEBSD;
	public static final boolean IS_NETWARE;

	static {
		IS_WIN32 = osName.startsWith("Windows");
		IS_AIX = osName.equals("AIX");
		IS_HPUX = osName.equals("HP-UX");
		IS_SOLARIS = osName.equals("SunOS");
		IS_LINUX = osName.equals("Linux");
		IS_DARWIN = osName.equals("Mac OS X") || osName.equals("Darwin");
		IS_OSF1 = osName.equals("OSF1");
		IS_FREEBSD = osName.equals("FreeBSD");
		IS_NETWARE = osName.equals("NetWare");
	}

	public static void loadNativeLibrary() throws ArchNotSupportedException {
		String nativeName = getLibraryName();
		log.info("NativeLibraryFile =  {}", nativeName);

		File userDir = new File(System.getProperty("user.dir"));
		File nativeLibraryBasePath = new File(userDir, "/native");
		File nativeLibraryFile = new File(nativeLibraryBasePath, nativeName);

		if (!nativeLibraryFile.exists()) {
			try {
				log.info("NativeLibraryFile not exist : {}", nativeLibraryFile.getAbsolutePath());
				if (!nativeLibraryBasePath.exists()) {
					nativeLibraryBasePath.mkdir();
				}
				String path = "/native/" + nativeName;
				InputStream is = SigarNativeLoader.class.getResourceAsStream("/native/" + nativeName);
				if (is == null) {
					throw new SigarException("File '" + path + "' not found.");
				}

				FileUtils.copyInputStreamToFile(is, nativeLibraryFile);
				log.info("NativeLibraryFile Copied : {}", nativeLibraryFile.getAbsolutePath());
			} catch (IOException e) {
				throw new SigarException("Failed to copy file from jar");
			}
		}


		System.setProperty("java.library.path", nativeLibraryBasePath.getAbsolutePath());
		log.info("java.library.path = {}", System.getProperty("java.library.path"));

		System.load(nativeLibraryFile.getAbsolutePath());
	}

	public static String getLibraryName() throws ArchNotSupportedException {
		String libName = getArchLibName();

		String prefix = getLibraryPrefix();
		String ext = getLibraryExtension();
		return prefix + libName + ext;
	}

	public static String getArchLibName() throws ArchNotSupportedException {
		return "sigar-" + ArchName.getName();
	}

	public static String getLibraryPrefix() {
		return !IS_WIN32 && !IS_NETWARE?"lib":"";
	}

	public static String getLibraryExtension() {
		return IS_WIN32?".dll":(IS_NETWARE?".nlm":(IS_DARWIN?".dylib":(IS_HPUX?".sl":".so")));
	}
}
