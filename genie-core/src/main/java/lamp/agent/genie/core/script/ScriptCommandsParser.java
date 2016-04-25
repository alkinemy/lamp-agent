package lamp.agent.genie.core.script;

import com.fasterxml.jackson.databind.ObjectMapper;

import lamp.agent.genie.core.script.exception.InvalidCommandException;
import lamp.agent.genie.core.script.exception.CommandException;
import lamp.agent.genie.core.script.exception.UnsupportedCommandTypeException;
import lamp.agent.genie.utils.BooleanUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ScriptCommandsParser {

	private ObjectMapper objectMapper;

	public ScriptCommandsParser() {
		objectMapper = new ObjectMapper();
	}

	public List<ScriptCommand> parse(String commandsStr) {
		log.info("commandsStr = {}", commandsStr);
		List<ScriptCommand> scriptCommands = new ArrayList<>();
		if (StringUtils.isNotBlank(commandsStr)) {
			try {
				log.info("commands = {}", commandsStr);
				Map<String, Object>[] commandMaps = objectMapper.readValue(commandsStr, Map[].class);
				for (Map<String, Object> command : commandMaps) {
					ScriptCommandType type = ScriptCommandType.valueOf((String) command.get("type"));
					ScriptCommand scriptCommand;
					switch (type) {
						case EXECUTE:
							scriptCommand = new ScriptExecuteCommand();
							((ScriptExecuteCommand) scriptCommand).setCommandShell((String) command.get("commandShell"));
							((ScriptExecuteCommand) scriptCommand).setCommandLine((String) command.get("commandLine"));
							break;
						case FILE_CREATE:
							scriptCommand = new ScriptFileCreateCommand();
							((ScriptFileCreateCommand) scriptCommand).setFilename((String) (command.get("filename")));
							((ScriptFileCreateCommand) scriptCommand).setContent((String) command.get("content"));
							//							((ScriptFileCreateCommand)scriptCommand).setRead(BooleanUtils.toBoolean(command.get("readable")));
							//							((ScriptFileCreateCommand)scriptCommand).setWrite(BooleanUtils.toBoolean(command.get("writable")));
							((ScriptFileCreateCommand) scriptCommand).setExecutable(BooleanUtils.toBoolean(String.valueOf(command.get("executable"))));
							break;
						case FILE_REMOVE:
							scriptCommand = new ScriptFileRemoveCommand();
							((ScriptFileRemoveCommand) scriptCommand).setFilename((String) command.get("filename"));
							break;
						default:
							throw new UnsupportedCommandTypeException(type);
					}
					if (scriptCommand instanceof AbstractScriptCommand) {
						((AbstractScriptCommand) scriptCommand).setName((String) command.get("name"));
						((AbstractScriptCommand) scriptCommand).setDescription((String) command.get("description"));
					}

					scriptCommands.add(scriptCommand);
				}
			} catch (CommandException e) {
				throw e;
			} catch (Exception e) {
				log.warn("INVALID_SCRIPT_COMMANDS (" + commandsStr + ")", e);
				throw new InvalidCommandException(e, commandsStr);
			}
		}
		return scriptCommands;
	}

}
