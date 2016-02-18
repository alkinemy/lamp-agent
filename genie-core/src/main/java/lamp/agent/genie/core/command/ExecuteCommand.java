package lamp.agent.genie.core.command;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.exception.CommandExecuteException;
import lamp.agent.genie.utils.FileUtils;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
public class ExecuteCommand extends AbstractCommand {

	private File file;
	private String content;

	private String charset = "UTF-8";

	public ExecuteCommand(File file, String content) {
		this.file = file;
		this.content = content;
	}

	@Override
	public void execute(AppContext context) {
		try {
			FileUtils.writeStringToFile(file, content, charset);
		} catch (IOException e) {
			throw new CommandExecuteException("FileCreateCommand", e);
		}
	}

}
