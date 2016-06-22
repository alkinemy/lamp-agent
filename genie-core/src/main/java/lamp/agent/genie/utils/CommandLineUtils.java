package lamp.agent.genie.utils;

import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import org.apache.commons.exec.CommandLine;

public class CommandLineUtils {

	public static CommandLine parse(SimpleAppContainer appContainer, String command) {
		String commandShell = appContainer.getCommandShell();
		if (StringUtils.isNotBlank(commandShell)) {
			String[] commandShellArray = StringUtils.split(commandShell, " ");
			CommandLine commandLine = new CommandLine(commandShellArray[0]);
			if (commandShellArray.length > 1) {
				for (int i = 1; i < commandShellArray.length; i++) {
					commandLine.addArgument(commandShellArray[i], false);
				}
			}
			commandLine.addArgument(command, false);

			return commandLine;
		} else {
			return CommandLine.parse(command);
		}
	}

}
