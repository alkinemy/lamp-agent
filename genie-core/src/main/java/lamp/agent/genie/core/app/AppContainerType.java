package lamp.agent.genie.core.app;

import lombok.Getter;

public enum AppContainerType {

	SIMPLE(Names.SIMPLE),
	DOCKER(Names.DOCKER);

	@Getter
	private String typeName;

	AppContainerType(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public static final class Names {
		public static final String SIMPLE = "simple";
		public static final String DOCKER = "docker";

		private Names() {
		}
	}


}
