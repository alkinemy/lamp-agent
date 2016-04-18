package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.AppSpec;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallSpec;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.core.runtime.shell.Shell;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
import lamp.agent.genie.utils.FilenameUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractAppContext implements AppContext {

	@Getter
	private final LampContext lampContext;
	@Getter
	private final AppSpec appSpec;
	@Getter
	private final InstallSpec installSpec;

	private ExpressionParser parser = new SpelExpressionParser();

	private AppStatus appStatus = AppStatus.NOT_RUNNING;
	private long lastCheckTimeMillis = 1000;

	public AbstractAppContext(LampContext lampContext, AppSpec appSpec, InstallSpec installSpec) {
		this.lampContext = lampContext;
		this.appSpec = appSpec;
		this.installSpec = installSpec;
	}

	public File getAppMetaInfoDirectory() {
		return getLampContext().getAppMetaInfoDirectory(getAppSpec().getId());
	}

	public File getStdOutFile() {
		String name = appSpec.getStdOutFile();
		if (StringUtils.isNotBlank(name)) {
			return new File(name);
		}
		return null;
	}

	public File getStdErrFile() {
		String name = appSpec.getStdErrFile();
		if (StringUtils.isNotBlank(name)) {
			return new File(name);
		}
		return null;
	}

	public AppSpec getParsedAppSpec() {
		Map<String, Object> parameters = getParameters();

		try {
			AppSpecImpl parsedConfig = new AppSpecImpl();
			BeanUtils.populate(parsedConfig, parameters);

			return parsedConfig;
		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_PARSE_FAILED, e);
		}
	}

	public Map<String, Object> getParameters() {

		try {
			Map<String, Object> parameters = new LinkedHashMap<>();
			parameters.put("id", appSpec.getId());
			parameters.put("name", appSpec.getName());
			parameters.put("artifactId", appSpec.getArtifactId());
			parameters.put("version", appSpec.getVersion());
			parameters.put("processType", appSpec.getProcessType());
			parameters.put("checkStatusInterval", appSpec.getCheckStatusInterval());
			parameters.put("preInstalled", appSpec.isPreInstalled());
			parameters.put("appDirectory", appSpec.getAppDirectory());
			parameters.put("workDirectory", appSpec.getWorkDirectory());
			parameters.put("logDirectory", appSpec.getLogDirectory());
			parameters.put("pidFile", appSpec.getPidFile());
			parameters.put("ptql", appSpec.getPtql());
			parameters.put("stdOutFile", appSpec.getStdOutFile());
			parameters.put("stdErrFile", appSpec.getStdErrFile());

			parameters.put("commandShell", appSpec.getCommandShell());
			parameters.put("startCommandLine", appSpec.getStartCommandLine());
			parameters.put("startTimeout", appSpec.getStartTimeout());
			parameters.put("stopCommandLine", appSpec.getStopCommandLine());
			parameters.put("stopTimeout", appSpec.getStopTimeout());
			parameters.put("monitor", appSpec.isMonitor());

			parameters.put("hostname", lampContext.getHostname());
			parameters.put("address", lampContext.getAddress());

			if (installSpec != null) {
				parameters.put("filename", installSpec.getFilename());
			}

			if (lampContext instanceof EnvironmentCapable) {
				Environment environment = ((EnvironmentCapable) lampContext).getEnvironment();
				parameters.put("activeProfiles", environment.getActiveProfiles());
				parameters.put("env", environment);
			}

			if (appSpec.getParameters() != null) {
				parameters.putAll(appSpec.getParameters());
			}

			String filename = (String) parameters.get("filename");
			if (StringUtils.isNotBlank(filename)) {
				filename = getValue(filename, parameters);
				parameters.put("filename", filename);
				parameters.put("fileBaseName", FilenameUtils.getBaseName(filename));
				parameters.put("fileExtension", FilenameUtils.getExtension(filename));
			}

			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				Object value = entry.getValue();
				if (value instanceof String) {

					String expValue = getValue((String) value, parameters);
					log.info("{} = {}", entry.getKey(), expValue);
					parameters.put(entry.getKey(), expValue);
				}
			}


			return parameters;
		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_PARSE_FAILED, e);
		}
	}

	public String getValue(String value, Map<String, Object> parameters) {
		Expression expression = parser.parseExpression(value, new TemplateParserContext("${", "}"));
		StandardEvaluationContext context = new StandardEvaluationContext(parameters);
		context.addPropertyAccessor(new MapAccessor());
		return expression.getValue(context, value.getClass());
	}

	public String getId() {
		return appSpec.getId();
	}

	@Override public Shell getShell() {
		return lampContext.getShell();
	}

	@Override public AppStatus getStatus() {
		if (System.currentTimeMillis() - lastCheckTimeMillis > appSpec.getCheckStatusInterval()) {
			return checkAndUpdateStatus();
		}

		return appStatus;
	}

	@Override public AppStatus updateStatus(AppStatus status) {
		this.lastCheckTimeMillis = System.currentTimeMillis();
		this.appStatus = status;
		return this.appStatus;
	}

	@Override public AppStatus checkAndUpdateStatus() {
		AppProcess process = getProcess();
		if (process != null) {
			AppProcessState processStatus = process.getStatus();
			if (AppProcessState.RUNNING.equals(processStatus)) {
				return updateStatus(AppStatus.RUNNING);
			} else if (AppProcessState.NOT_RUNNING.equals(processStatus)) {
				return updateStatus(AppStatus.NOT_RUNNING);
			} else {
				return updateStatus(AppStatus.UNKNOWN);
			}
		}
		return appStatus;
	}


	public abstract AppProcess getProcess();

}
