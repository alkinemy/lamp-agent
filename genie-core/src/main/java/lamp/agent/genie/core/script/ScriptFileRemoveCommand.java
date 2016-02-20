package lamp.agent.genie.core.script;

import lamp.agent.genie.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
@Getter
@Setter
public class ScriptFileRemoveCommand extends ScriptFileCommand {

	private String filename;

	@Override
	public void execute(CommandExecutionContext context) {
		log.info("File Remove : {}", filename);
		File file = getFile(context.getAppContext(), filename);
		FileUtils.deleteQuietly(file);
	}

}
