package lamp.agent.genie.spring.boot.management.service.install;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.exception.CommandExecuteException;
import lamp.agent.genie.core.install.command.Command;
import lamp.agent.genie.core.install.command.FileCreateCommand;
import lamp.agent.genie.core.install.command.FileSetExecutableCommand;
import lamp.agent.genie.spring.boot.management.support.ExpressionParser;
import lamp.agent.genie.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.Expression;

import org.springframework.expression.common.TemplateParserContext;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class SpringBootInstallCommand implements Command {

	private static final String DEFAULT_JVM_OPTS = "-Xms64m -Xmx128m";
	private static final String DEFAULT_SPRING_OPTS = "--spring.profiles.active=${activeProfiles} --spring.config.name=${appName}";

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
	}

	public SpringBootInstallCommand(String launchScriptFilename, String launchScript, String jvmOpts, String springOpts) {
		this.launchScriptFilename = launchScriptFilename;
		this.launchScript = launchScript;
		this.jvmOpts = jvmOpts;
		this.springOpts = springOpts;
	}


	@Override public void execute(AppContext appContext) {
		AppConfig appConfig = appContext.getParsedAppConfig();
		Map<String, Object> parameters = appContext.getParameters();
		String scriptFilename = launchScriptFilename;
		if (StringUtils.isBlank(scriptFilename)) {
			scriptFilename = appConfig.getAppName() + ".sh"; // TODO OS Type?
		}
		String script = launchScript;
		if (StringUtils.isBlank(script)) {
			ClassPathResource resource = new ClassPathResource("script/launch.sh");
			try (InputStream inputStream = resource.getInputStream()) {
				script = IOUtils.toString(inputStream, "UTF-8");
			} catch (Exception e) {
				throw new CommandExecuteException("SpringBootInstallCommand", e);
			}
		}

		File scriptFile = new File(appConfig.getWorkDirectory(), scriptFilename);
		log.info("scriptFile = {}", scriptFile.getAbsolutePath());

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
