package lamp.agent.genie.core.app.simple.install;

import lamp.agent.genie.core.app.simple.SimpleAppContext;

public interface UninstallContext {

	SimpleAppContext getAppInstanceContext();

	InstallSpec getInstallSpec();

}
