package lamp.agent.genie.spring.boot.management.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.core.exception.CommandException;
import lamp.agent.genie.core.exception.UnknownCommandException;
import lamp.agent.genie.core.install.command.Command;
import lamp.agent.genie.spring.boot.management.service.install.ExtendedCommand;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommandService {

	private ObjectMapper objectMapper = new ObjectMapper();

	public List<Command> createCommands(String commandsString) {
		List<Command> commands = new ArrayList();
		if (StringUtils.isNotBlank(commandsString)) {
			try {
				Map<String, Object> commandsMap = objectMapper.readValue(commandsString, LinkedHashMap.class);
				if (commandsMap != null) {
					for (Map.Entry<String, Object> commandMap : commandsMap.entrySet()) {
						Command command = createCommand(commandMap.getKey(), commandMap.getValue());
						commands.add(command);
					}
				}
			} catch (CommandException e) {
				throw e;
			} catch (Exception e) {
				throw new CommandException("Command parsing failed", e);
			}

		}
		return commands;
	}

	protected Command createCommand(String key, Object value) {
		try {
			Class<? extends Command> commandClass = getCommandClass(key);
			Constructor<? extends Command> constructor;
			if (value == null) {
				constructor = commandClass.getConstructor();
			} else {
				Class<?> parameterType = value.getClass();
				if (Map.class.isAssignableFrom(parameterType)) {
					parameterType = Map.class;
				}
				constructor = commandClass.getConstructor(parameterType);
			}
			return constructor.newInstance(value);
		} catch(Exception e) {
			throw new CommandException("Command creation failed (" + key + ")", e);
		}
	}

	protected Class<? extends Command> getCommandClass(String name) {
		try {
			return (Class<? extends Command>) Class.forName(name);
		} catch (ClassNotFoundException e) {
			try {
				return (Class<? extends Command>) Class.forName(Command.class.getPackage().getName() + "." + name);
			} catch (ClassNotFoundException e2) {
				try {
					return (Class<? extends Command>) Class.forName(ExtendedCommand.class.getPackage().getName() + "." + name);
				} catch (ClassNotFoundException e3) {
					throw new UnknownCommandException(name, e3);
				}
			}
		}
	}

}
