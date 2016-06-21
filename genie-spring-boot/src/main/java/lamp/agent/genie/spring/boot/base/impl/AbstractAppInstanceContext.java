package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.SimpleAppInstanceContext;
import lamp.agent.genie.core.AppInstanceSpec;
import lamp.agent.genie.core.AppInstanceStatus;
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
public abstract class AbstractAppInstanceContext implements SimpleAppInstanceContext {

	@Getter
	private final LampContext lampContext;
	@Getter
	private final AppInstanceSpec appInstanceSpec;
	@Getter
	private final InstallSpec installSpec;

	private ExpressionParser parser = new SpelExpressionParser();

	private AppInstanceStatus appInstanceStatus = AppInstanceStatus.STOPPED;
	private long lastCheckTimeMillis = 1000;

	public AbstractAppInstanceContext(LampContext lampContext, AppInstanceSpec appInstanceSpec, InstallSpec installSpec) {
		this.lampContext = lampContext;
		this.appInstanceSpec = appInstanceSpec;
		this.installSpec = installSpec;
	}

	public File getAppMetaInfoDirectory() {
		return getLampContext().getAppMetaInfoDirectory(this.getAppInstanceSpec().getId());
	}

	public File getStdOutFile() {
		String name = appInstanceSpec.getStdOutFile();
		if (StringUtils.isNotBlank(name)) {
			return new File(name);
		}
		return null;
	}

	public File getStdErrFile() {
		String name = appInstanceSpec.getStdErrFile();
		if (StringUtils.isNotBlank(name)) {
			return new File(name);
		}
		return null;
	}

	public AppInstanceSpec getParsedAppInstanceSpec() {
		Map<String, Object> parameters = getParameters();

		try {
			AppInstanceSpecImpl parsedConfig = new AppInstanceSpecImpl();
			BeanUtils.populate(parsedConfig, parameters);

			return parsedConfig;
		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_PARSE_FAILED, e);
		}
	}

	public Map<String, Object> getParameters() {

		try {
			Map<String, Object> parameters = new LinkedHashMap<>();
			parameters.put("id", appInstanceSpec.getId());
			parameters.put("name", appInstanceSpec.getName());
			parameters.put("artifactId", appInstanceSpec.getArtifactId());
			parameters.put("version", appInstanceSpec.getVersion());
			parameters.put("processType", appInstanceSpec.getProcessType());
			parameters.put("checkStatusInterval", appInstanceSpec.getCheckStatusInterval());
			parameters.put("preInstalled", appInstanceSpec.isPreInstalled());
			parameters.put("appDirectory", appInstanceSpec.getAppDirectory());
			parameters.put("workDirectory", appInstanceSpec.getWorkDirectory());
			parameters.put("logDirectory", appInstanceSpec.getLogDirectory());
			parameters.put("pidFile", appInstanceSpec.getPidFile());
			parameters.put("ptql", appInstanceSpec.getPtql());
			parameters.put("stdOutFile", appInstanceSpec.getStdOutFile());
			parameters.put("stdErrFile", appInstanceSpec.getStdErrFile());

			parameters.put("commandShell", appInstanceSpec.getCommandShell());
			parameters.put("startCommandLine", appInstanceSpec.getStartCommandLine());
			parameters.put("startTimeout", appInstanceSpec.getStartTimeout());
			parameters.put("stopCommandLine", appInstanceSpec.getStopCommandLine());
			parameters.put("stopTimeout", appInstanceSpec.getStopTimeout());
			parameters.put("monitor", appInstanceSpec.isMonitor());

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

			if (appInstanceSpec.getParameters() != null) {
				parameters.putAll(appInstanceSpec.getParameters());
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
		return appInstanceSpec.getId();
	}

	@Override public Shell getShell() {
		return lampContext.getShell();
	}

	@Override public AppInstanceStatus getStatus() {
		if (System.currentTimeMillis() - lastCheckTimeMillis > appInstanceSpec.getCheckStatusInterval()) {
			return checkAndUpdateStatus();
		}

		return appInstanceStatus;
	}

	@Override public AppInstanceStatus updateStatus(AppInstanceStatus status) {
		this.lastCheckTimeMillis = System.currentTimeMillis();
		this.appInstanceStatus = status;
		return this.appInstanceStatus;
	}

	@Override public AppInstanceStatus checkAndUpdateStatus() {
		AppProcess process = getProcess();
		if (process != null) {
			AppProcessState processStatus = process.getStatus();
			if (AppProcessState.RUNNING.equals(processStatus)) {
				return updateStatus(AppInstanceStatus.RUNNING);
			} else if (AppProcessState.NOT_RUNNING.equals(processStatus)) {
				return updateStatus(AppInstanceStatus.STOPPED);
			} else {
				return updateStatus(AppInstanceStatus.UNKNOWN);
			}
		}
		return appInstanceStatus;
	}


	public abstract AppProcess getProcess();

}
