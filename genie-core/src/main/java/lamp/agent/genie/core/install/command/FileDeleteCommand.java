package lamp.agent.genie.core.install.command;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.utils.FileUtils;
import lamp.agent.genie.utils.StringUtils;

import java.io.File;
import java.util.Map;

public class FileDeleteCommand extends AbstractCommand {

	private File file;

	public FileDeleteCommand(Map<String, Object> parameters) {
		String filnename = (String) parameters.get("file");
		if (StringUtils.isBlank(filnename)) {
			throw new IllegalArgumentException("filename must be not null");
		}
		this.file = new File(filnename);
	}

	public FileDeleteCommand(File file) {
		this.file = file;
	}

	@Override
	public void execute(AppContext context) {
		FileUtils.deleteQuietly(file);
	}

}
