package lamp.agent.genie.core.script;

public enum ScriptCommandType {

	EXECUTE(Values.EXECUTE),

	FILE_CREATE(Values.FILE_CREATE),
	FILE_REMOVE(Values.FILE_REMOVE);

	private String typeName;

	ScriptCommandType(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public static final class Values {
		public static final String EXECUTE = "EXECUTE";
		public static final String FILE_CREATE = "FILE_CREATE";
		public static final String FILE_REMOVE = "FILE_REMOVE";

		private Values() {
		}
	}

}
