package lamp.agent.genie.spring.boot.management.service.install;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.exception.CommandExecuteException;
import lamp.agent.genie.core.command.FileCreateCommand;
import lamp.agent.genie.core.command.FileSetExecutableCommand;
import lamp.agent.genie.spring.boot.management.support.ExpressionParser;
import lamp.agent.genie.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class SpringBootInstallCommand implements ExtendedCommand {

	private static final String DEFAULT_JVM_OPTS = "-Xms64m -Xmx128m";
	private static final String DEFAULT_SPRING_OPTS = "--spring.profiles.active=${activeProfiles}";
	//	private static final String DEFAULT_SPRING_OPTS = "--spring.profiles.active=${activeProfiles} --spring.config.name=${artifactId}";

	private ExpressionParser parser = new ExpressionParser();

	@Getter
	private String launchScriptFilename;
	@Getter
	private String launchScript;

	@Getter
	private String jvmOpts;
	@Getter
	private String springOpts;

	public SpringBootInstallCommand() {
		this(null, null, DEFAULT_JVM_OPTS, DEFAULT_SPRING_OPTS);
	}

	public SpringBootInstallCommand(String launchScriptFilename, String launchScript, String jvmOpts, String springOpts) {
		this.launchScriptFilename = launchScriptFilename;
		this.launchScript = launchScript;
		this.jvmOpts = jvmOpts;
		this.springOpts = springOpts;
	}

	public SpringBootInstallCommand(Map<String, Object> parameters) {
		this.launchScriptFilename = (String) parameters.get("launchScriptFilename");
		this.launchScript = (String) parameters.get("launchScript");
		this.jvmOpts = (String) parameters.get("jvmOpts");
		this.springOpts = (String) parameters.get("springOpts");
	}

	@Override public void execute(AppContext appContext) {
		AppSpec appSpec = appContext.getParsedAppSpec();
		Map<String, Object> parameters = appContext.getParameters();
		String scriptFilename = launchScriptFilename;
		if (StringUtils.isBlank(scriptFilename)) {
			scriptFilename = appSpec.getArtifactId() + ".sh"; // TODO OS Type?
		}
		String script = launchScript;
		if (StringUtils.isBlank(script)) {
			ClassPathResource resource = new ClassPathResource("script/default-spring-boot.sh");
			try (InputStream inputStream = resource.getInputStream()) {
				script = IOUtils.toString(inputStream, "UTF-8");
			} catch (Exception e) {
				throw new CommandExecuteException("SpringBootInstallCommand", e);
			}
		}

		File scriptFile = new File(appSpec.getWorkDirectory(), scriptFilename);
		log.debug("scriptFile = {}", scriptFile.getAbsolutePath());

		parameters.put("JVM_OPTS", getValue(StringUtils.defaultIfBlank(jvmOpts, DEFAULT_JVM_OPTS), parameters));
		parameters.put("SPRING_OPTS", getValue(StringUtils.defaultIfBlank(springOpts, DEFAULT_SPRING_OPTS), parameters));

		log.debug("parameters = {}", parameters);
		script = getValue(script, parameters);

		new FileCreateCommand(scriptFile, script).execute(appContext);
		new FileSetExecutableCommand(scriptFile).execute(appContext);
	}

	protected String getValue(String value, Map<String, Object> parameters) {
		return parser.getValue(value, parameters);
	}

}
