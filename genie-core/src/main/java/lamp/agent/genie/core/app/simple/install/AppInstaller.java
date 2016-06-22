package lamp.agent.genie.core.app.simple.install;

public interface AppInstaller {

	void install(InstallContext context);

	void uninstall(UninstallContext appContext);
}
