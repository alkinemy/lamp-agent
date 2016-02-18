package lamp.agent.genie.core.command;

import lamp.agent.genie.core.AppContext;
import lombok.Getter;

import java.io.File;

@Getter
public class FileSetExecutableCommand extends AbstractCommand {

	private File file;

	private boolean executable = true;

	public FileSetExecutableCommand(File file) {
		this.file = file;
	}

	@Override
	public void execute(AppContext context) {
		file.setExecutable(executable);
	}

}
