package lamp.agent.genie.spring.boot.management.repository;

import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.spring.boot.base.impl.AppConfigImpl;
import lamp.agent.genie.utils.FileUtils;
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
public class AppConfigRepositoryTest {

	@InjectMocks AppConfigRepository appConfigRepository;

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

		AppConfigImpl appManifest = saveAppManifest();

		assertThat(manifestFile).exists();
	}

	@Test
	public void testFindAll() throws Exception {

	}

	@Test
	public void testFindOne() throws Exception {
		AppConfigImpl testAppManifest = saveAppManifest();

		AppConfig appConfig = appConfigRepository.findOne(appId);
		assertThat(appConfig.getId()).isEqualTo(testAppManifest.getId());
	}


	@Test
	public void testFindOne_Null() throws Exception {
		AppConfig appConfig = appConfigRepository.findOne(appId);
		assertThat(appConfig).isNull();
	}

	@Test
	public void testDelete() throws Exception {
		AppConfigImpl testAppManifest = saveAppManifest();

		assertThat(manifestFile).exists();
		appConfigRepository.delete(testAppManifest);
		assertThat(manifestFile).doesNotExist();
	}


	private AppConfigImpl saveAppManifest() {
		AppConfigImpl testAppManifest = new AppConfigImpl();
		testAppManifest.setId(appId);
		appConfigRepository.save(testAppManifest);
		return testAppManifest;
	}
}