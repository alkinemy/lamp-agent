package lamp.client.genie.spring.boot.management.repository;

import lamp.client.genie.core.AppManifest;
import lamp.client.genie.core.context.LampContext;
import lamp.client.genie.spring.boot.base.impl.AppManifestImpl;
import lamp.client.genie.utils.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppManifestRepositoryTest {

	@InjectMocks AppManifestRepository appManifestRepository;

	@Mock LampContext lampContext;

	String appId = "test-app";
	File appDirectory;
	File manifestFile;

	@Before
	public void setUp() throws Exception {
		appDirectory = new File(System.getProperty("java.io.tmpdir"), "lamp-test/" + System.currentTimeMillis() + "/apps");
		manifestFile = new File(appDirectory, "manifest.json");

		when(lampContext.getAppDirectory(appId)).thenReturn(appDirectory);
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(appDirectory);
	}

	@Test
	public void testSave() throws Exception {
		assertThat(manifestFile).doesNotExist();

		AppManifestImpl appManifest = saveAppManifest();

		assertThat(manifestFile).exists();
	}

	@Test
	public void testFindAll() throws Exception {

	}

	@Test
	public void testFindOne() throws Exception {
		AppManifestImpl testAppManifest = saveAppManifest();

		AppManifest appManifest = appManifestRepository.findOne(appId);
		assertThat(appManifest.getId()).isEqualTo(testAppManifest.getId());
	}


	@Test
	public void testFindOne_Null() throws Exception {
		AppManifest appManifest = appManifestRepository.findOne(appId);
		assertThat(appManifest).isNull();
	}

	@Test
	public void testDelete() throws Exception {
		AppManifestImpl testAppManifest = saveAppManifest();

		assertThat(manifestFile).exists();
		appManifestRepository.delete(testAppManifest);
		assertThat(manifestFile).doesNotExist();
	}


	private AppManifestImpl saveAppManifest() {
		AppManifestImpl testAppManifest = new AppManifestImpl();
		testAppManifest.setId(appId);
		appManifestRepository.save(testAppManifest);
		return testAppManifest;
	}
}