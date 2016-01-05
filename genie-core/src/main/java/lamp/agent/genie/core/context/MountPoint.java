package lamp.agent.genie.core.context;



import java.io.File;
import java.io.FileFilter;

public class MountPoint {

	public static final FileFilter DIRECTORY = new DirectoryFileFilter();

	public static final MountPoint ROOT = new MountPoint();

	private final MountPoint parent;
	private final String name;

	public MountPoint(String name, MountPoint parent) {
//		Exceptions.throwsException(name == null || name.contains("/"), ErrorCode.INVALID_MOUNT_POINT_NAME, name);
//		Exceptions.throwsException(parent == null, ErrorCode.INVALID_MOUNT_POINT_PARENT, name);

		this.name = name;
		this.parent = parent;
	}

	public MountPoint(String name) {
		this(name, ROOT);
	}

	private MountPoint() {
		this.name = "";
		this.parent = null;
	}

	public MountPoint getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		if (parent == null) {
			return "/";
		}
		String path = parent.getPath();
		if (path.equals("/")) {
			return "/" + name;
		} else {
			return path + "/" + name;
		}
	}

	public String getPath(boolean mkdirs) {
		String path = getPath();
		if (mkdirs) {
			File dir = new File(path);
			dir.mkdirs();
		}
		return path;
	}

	public static MountPoint fromPath(String path) {
		if (path == null) {
			return null;
		}

		path = path.trim();

		if (path.equals("/")) {
			return ROOT;
		}

		if (!path.startsWith("/")) {
			return new MountPoint(path);
		}

		MountPoint mountPoint = null;

		String[] paths = path.split("/");

		for (String p : paths) {
			if (mountPoint != null) {
				mountPoint = new MountPoint(p, mountPoint);
			} else {
				mountPoint = new MountPoint(p);
			}
		}

		return mountPoint;
	}

	public File getFile(String filename) {
		return new File(getPath(), filename);
	}

	public File getDirectory(String filename, boolean mkdirs) {
		File dir = new File(getPath(), filename);
		if (mkdirs && !dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public File[] listFiles() {
		File file = new File(getPath());
		return file.listFiles();
	}

	public File[] listFiles(FileFilter filter) {
		File file = new File(getPath());
		return file.listFiles(filter);
	}

	static class DirectoryFileFilter implements FileFilter {
		@Override public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	}

}
