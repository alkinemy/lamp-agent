package lamp.agent.genie.spring.boot.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.spring.boot.LampAgent;
import lamp.agent.genie.spring.boot.management.model.AppDto;
import lamp.agent.genie.utils.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(value = LampAgent.class)
@WebIntegrationTest("server.port:0")
public class AppControllerTest {

	@Autowired
	EmbeddedWebApplicationContext server;

	@Value("${local.server.port}")
	int port;

	private Authentication authentication;

	RestTemplate template = new TestRestTemplate();

	private String getBaseUrl() {
		return "http://localhost:" + port;
	}


	@Before
	public void setUp() throws Exception {
		AuthenticationManager authenticationManager = this.server
				.getBean(AuthenticationManager.class);
		this.authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken("user", "password"));
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testList() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(this.authentication);

		ResponseEntity<List<AppDto>> responseEntity = template.exchange(getBaseUrl() + "/api/app", HttpMethod.GET, null, new ParameterizedTypeReference<List<AppDto>>() {});
		assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();

		List<AppDto> appDtoList = responseEntity.getBody();
		for (AppDto appDto : appDtoList) {
			System.out.println(appDto);
		}

	}

	@Test
	public void testRegister_PreInstalled() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(this.authentication);

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

		parts.add("id", "test-1");
		parts.add("name", "test");
		parts.add("artifactId", "test-app");
		parts.add("version", "0.0.1-SNAPSHOT");
		parts.add("processType", AppProcessType.DAEMON.name());
		parts.add("appDirectory", "/Users/kangwoo/Applications/zookeeper-3.4.7");
		parts.add("workDirectory", "${appDirectory}");

		parts.add("pidFile", "/tmp/zookeeper/zookeeper_server.pid");
		parts.add("logFile", "${workDirectory}/zookeeper.out");
		parts.add("startCommandLine", "./bin/zkServer.sh start");
		parts.add("stopCommandLine", "./bin/zkServer.sh stop");
		parts.add("preInstalled", true);


		ResponseEntity<Void> responseEntity = template.postForEntity(getBaseUrl() + "/api/app", parts, Void.class);
//		assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
	}

	@Test
	public void testRegister_Install_Default() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(this.authentication);

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

		parts.add("id", "test-2");
		parts.add("name", StringUtils.utf8ToIso88591("테스트 2번 이다"));
		parts.add("artifactId", "test-app");
		parts.add("version", "0.0.1-SNAPSHOT");
		parts.add("processType", AppProcessType.DEFAULT.name());
		parts.add("pidFile", "${workDirectory}/test-app.pid");
		parts.add("startCommandLine", "java -jar ${filename} --server.port=19092");
		parts.add("stopCommandLine", "");
		parts.add("preInstalled", false);
		parts.add("installFile", new ClassPathResource("apps/test-app-0.0.1-SNAPSHOT.jar"));
		parts.add("filename", "test-app.jar");

		URI location = template.postForLocation(getBaseUrl() + "/api/app", parts);

//		assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
	}

	@Test
	public void testRegister_Install_Daemon() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(this.authentication);

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

		parts.add("id", "test-3");
		parts.add("name", "test");
		parts.add("artifactId", "test-app");
		parts.add("version", "0.0.1-SNAPSHOT");
		parts.add("processType", AppProcessType.DAEMON.name());
		parts.add("pidFile", "${workDirectory}/${artifactId}.pid");
		parts.add("startCommandLine", "./${artifactId}.sh start");
//		parts.add("commandShell", "/bin/bash -c");
//		parts.add("startCommandLine", "nohup java -jar ${filename} --server.port=19093 1>nohup.out 2>&1 &");
		parts.add("stopCommandLine", "");
		parts.add("preInstalled", false);
		parts.add("installFile", new ClassPathResource("apps/test-app-0.0.1-SNAPSHOT.jar"));
		parts.add("filename", "${artifactId}.jar");
		parts.add("monitor", "true");

		Map<String, Object> commandsHashMap = new LinkedHashMap<>();
		{
			Map<String, Object> commandParameters = new HashMap<>();
			commandParameters.put("launchScriptFilename", "");
			commandParameters.put("launchScript", "");
			commandParameters.put("jvmOpts", "-Xms128m -Xmx256m");
			commandParameters.put("springOpts", "");

			commandsHashMap.put("SpringBootInstallCommand", commandParameters);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		parts.add("commands", objectMapper.writeValueAsString(commandsHashMap));

		URI location = template.postForLocation(getBaseUrl() + "/api/app", parts);

//		assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
	}

	@Test
	public void testDeregister() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(this.authentication);

		Map<String, Object> urlVariables = new HashMap<>();

		urlVariables.put("id", "test-2");

		template.delete(getBaseUrl() + "/api/app/{id}", urlVariables);
	}
}