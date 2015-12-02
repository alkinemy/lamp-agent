package lamp.client.genie.spring.boot.management.service;

import lamp.client.genie.core.AppManifest;

import java.util.List;

public class AppManifestService {

	private List<AppManifest> appManifests;

	public List<AppManifest> getAppManifests() {
		return appManifests;
	}

	public AppManifest save(AppManifest appManifest) {
		return appManifest;
	}

	public void delete(AppManifest appManifest) {

	}
}
