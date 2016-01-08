package lamp.agent.genie.core.install;

public interface AppInstaller {

	void install(InstallContext context);

	void uninstall(UninstallContext appContext);
}
