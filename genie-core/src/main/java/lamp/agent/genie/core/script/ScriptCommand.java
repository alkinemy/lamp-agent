package lamp.agent.genie.core.script;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
	@JsonSubTypes.Type(value = ScriptExecuteCommand.class, name = ScriptCommandType.Values.EXECUTE),
	@JsonSubTypes.Type(value = ScriptFileCreateCommand.class, name = ScriptCommandType.Values.FILE_CREATE),
	@JsonSubTypes.Type(value = ScriptFileRemoveCommand.class, name = ScriptCommandType.Values.FILE_REMOVE)
})
public interface ScriptCommand {

	void execute(CommandExecutionContext context);

}
