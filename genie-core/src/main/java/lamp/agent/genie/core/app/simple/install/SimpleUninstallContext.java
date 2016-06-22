package lamp.agent.genie.core.app.simple.install;

import lamp.agent.genie.core.app.simple.SimpleAppContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(staticName = "of")
public class SimpleUninstallContext implements UninstallContext {

	@NonNull
	private SimpleAppContext appInstanceContext;

	@NonNull
	private InstallSpec installSpec;

}
