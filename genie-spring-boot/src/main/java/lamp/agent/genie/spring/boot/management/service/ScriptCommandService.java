package lamp.agent.genie.spring.boot.management.service;

import lamp.agent.genie.core.script.ScriptCommand;
import lamp.agent.genie.core.script.ScriptCommandsParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ScriptCommandService {

	private ScriptCommandsParser scriptCommandsParser = new ScriptCommandsParser();

	public List<ScriptCommand> parse(String commandsString) {
		return scriptCommandsParser.parse(commandsString);
	}

}
