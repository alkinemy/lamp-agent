package lamp.agent.genie.core.script;

import lamp.agent.genie.core.LampCoreConstants;
import lamp.agent.genie.core.script.exception.CommandExecuteException;
import lamp.agent.genie.utils.ExpressionParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class ScriptFileCreateCommand extends ScriptFileCommand {

	private String filename;
	private String content;

	private boolean readable = true;
	private boolean writable = true;
	private boolean executable = false;

	private String charset = LampCoreConstants.DEFAULT_CHARSET;

	@Override
	public void execute(CommandExecutionContext context) {
		log.info("File Create (0) : {}", filename);
		ExpressionParser expressionParser = context.getExpressionParser();
		Map<String, Object> parameters = context.getAppContext().getParameters();
		try {
			File file = getFile(context.getAppContext(), filename);

			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			String lineSeparator = System.getProperty("line.separator");
			try (BufferedReader reader = new BufferedReader(new StringReader(getContent()));
					BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				String line;
				for (int i = 0; (line = reader.readLine()) != null; i++) {
					if (i > 0) {
						writer.write(lineSeparator);
					}
					String parsedLine = expressionParser.getValue(line, parameters);
					writer.write(parsedLine);
				}
				writer.flush();
			}
			log.info("File Created : {}", file.getAbsolutePath());

			if (executable) {
				file.setExecutable(executable);
			}
		} catch (IOException e) {
			throw new CommandExecuteException(e, "File Create Failed : " + filename);
		}
	}

}
