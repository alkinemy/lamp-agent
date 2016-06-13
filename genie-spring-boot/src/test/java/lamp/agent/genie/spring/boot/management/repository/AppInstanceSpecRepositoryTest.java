package lamp.agent.genie.spring.boot.management.repository;

import lamp.agent.genie.core.AppInstanceSpec;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.spring.boot.base.impl.AppInstanceSpecImpl;
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
public class AppInstanceSpecRepositoryTest {

	@InjectMocks AppSpecRepository appSpecRepository;

	@Mock LampContext lampContext;

	String artifactId = "test-app";
	File appDirectory;
	File manifestFile;

	@Before
	public void setUp() throws Exception {
		appDirectory = new File(System.getProperty("java.io.tmpdir"), "lamp-test/" + System.currentTimeMillis() + "/apps");
		manifestFile = new File(appDirectory, "manifest.json");

		when(lampContext.getAppMetaInfoDirectory(artifactId)).thenReturn(appDirectory);
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(appDirectory);
	}

	@Test
	public void testSave() throws Exception {
//		assertThat(manifestFile).doesNotExist();
//
//		AppSpecImpl appManifest = saveAppManifest();
//
//		assertThat(manifestFile).exists();
	}

	@Test
	public void testFindAll() throws Exception {

	}

	@Test
	public void testFindOne() throws Exception {
		AppInstanceSpecImpl testAppManifest = saveAppManifest();

		AppInstanceSpec appInstanceSpec = appSpecRepository.findOne(artifactId);
		assertThat(appInstanceSpec.getId()).isEqualTo(testAppManifest.getId());
	}


	@Test
	public void testFindOne_Null() throws Exception {
		AppInstanceSpec appInstanceSpec = appSpecRepository.findOne(artifactId);
		assertThat(appInstanceSpec).isNull();
	}

	@Test
	public void testDelete() throws Exception {
//		AppSpecImpl testAppManifest = saveAppManifest();
//
//		assertThat(manifestFile).exists();
//		appConfigRepository.delete(testAppManifest);
//		assertThat(manifestFile).doesNotExist();
	}


	private AppInstanceSpecImpl saveAppManifest() {
		AppInstanceSpecImpl testAppManifest = new AppInstanceSpecImpl();
		testAppManifest.setId(artifactId);
		appSpecRepository.save(testAppManifest);
		return testAppManifest;
	}
}