package lamp.agent.genie.core.script;

import lamp.agent.genie.core.SimpleAppInstanceContext;
import lamp.agent.genie.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@AllArgsConstructor
public abstract class ScriptFileCommand extends AbstractScriptCommand {

	protected File getFile(SimpleAppInstanceContext context, String filename) {
		String workDir = context.getParsedAppInstanceSpec().getWorkDirectory();
		String targetFilename = context.getValue(filename, context.getParameters());
		Path filepath;
		if (StringUtils.startsWith(targetFilename, "/")) {
			filepath = Paths.get(targetFilename).normalize();
		} else {
			filepath = Paths.get(workDir, targetFilename).normalize();
		}


		File parent = new File(workDir);
		File file = new File(filepath.toString());

		if (!file.getAbsolutePath().startsWith(parent.getAbsolutePath())) {
			throw new IllegalArgumentException("Cannot access " + targetFilename);
		}
		return file;
	}

}
