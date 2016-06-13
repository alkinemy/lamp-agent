package lamp.agent.genie.core.script;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lamp.agent.genie.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
@Getter
@Setter
@JsonTypeName(ScriptCommandType.Values.FILE_REMOVE)
public class ScriptFileRemoveCommand extends ScriptFileCommand {

	private String filename;

	@Override
	public void execute(CommandExecutionContext context) {
		log.info("File Remove : {}", filename);
		File file = getFile(context.getAppInstanceContext(), filename);
		FileUtils.deleteQuietly(file);
	}

}
