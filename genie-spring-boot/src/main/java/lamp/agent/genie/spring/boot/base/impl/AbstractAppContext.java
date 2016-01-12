package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.install.InstallConfig;
import lamp.agent.genie.core.runtime.process.AppProcess;
import lamp.agent.genie.core.runtime.process.AppProcessState;
import lamp.agent.genie.core.runtime.shell.Shell;
import lamp.agent.genie.spring.boot.base.exception.ErrorCode;
import lamp.agent.genie.spring.boot.base.exception.Exceptions;
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
	private final AppConfig appConfig;
	@Getter
	private final InstallConfig installConfig;
	@Getter
	private File systemLogFile;

	private ExpressionParser parser = new SpelExpressionParser();

	private AppStatus appStatus = AppStatus.NOT_RUNNING;
	private long lastCheckTimeMillis = 1000;


	public AbstractAppContext(LampContext lampContext, AppConfig appConfig, InstallConfig installConfig) {
		this.lampContext = lampContext;
		this.appConfig = appConfig;
		this.installConfig = installConfig;

		this.systemLogFile = new File(lampContext.getLogDirectory(), appConfig.getId() + ".log");
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public AppConfig getParsedAppConfig() {
		Map<String, Object> parameters = getParameters();

		try {
			AppConfigImpl parsedConfig = new AppConfigImpl();
			BeanUtils.populate(parsedConfig, parameters);

			return parsedConfig;
		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_PARSE_FAILED, e);
		}

	}

	public Map<String, Object> getParameters() {

		try {
			Map<String, Object> parameters = new LinkedHashMap<>();
			parameters.put("id", appConfig.getId());
			parameters.put("name", appConfig.getName());
			parameters.put("appName", appConfig.getAppName());
			parameters.put("appVersion", appConfig.getAppVersion());
			parameters.put("processType", appConfig.getProcessType());
			parameters.put("checkStatusInterval", appConfig.getCheckStatusInterval());
			parameters.put("preInstalled", appConfig.isPreInstalled());
			parameters.put("homeDirectory", appConfig.getHomeDirectory());
			parameters.put("workDirectory", appConfig.getWorkDirectory());
			parameters.put("pidFile", appConfig.getPidFile());
			parameters.put("logFile", appConfig.getLogFile());
			parameters.put("startCommandLine", appConfig.getStartCommandLine());
			parameters.put("startTimeout", appConfig.getStartTimeout());
			parameters.put("stopCommandLine", appConfig.getStopCommandLine());
			parameters.put("stopTimeout", appConfig.getStopTimeout());
			parameters.put("autoStart", appConfig.isAutoStart());
			parameters.put("autoStop", appConfig.isAutoStop());

			if (installConfig != null) {
				parameters.put("filename", installConfig.getFilename());
			}

			if (lampContext instanceof EnvironmentCapable) {
				Environment environment = ((EnvironmentCapable)lampContext).getEnvironment();
				parameters.put("activeProfiles", environment.getActiveProfiles());
				parameters.put("env", environment);
			}

			if (appConfig.getParameters() != null) {
				parameters.putAll(appConfig.getParameters());
			}



			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				Object value = entry.getValue();
				if (value instanceof String) {
					Expression expression = parser.parseExpression((String) value, new TemplateParserContext("${", "}"));
					StandardEvaluationContext context = new StandardEvaluationContext(parameters);
					context.addPropertyAccessor(new MapAccessor());
					log.info("{} = {}", entry.getKey(), value);
					parameters.put(entry.getKey(), expression.getValue(context, value.getClass()));
				}
			}

			return parameters;
		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_PARSE_FAILED, e);
		}
	}

	public String getId() {
		return appConfig.getId();
	}

	@Override public <T> T getValue(T value, Object parameters) {
		if (value == null) {
			return value;
		}
		Expression expression = parser.parseExpression(String.valueOf(value), new TemplateParserContext("${", "}"));
		StandardEvaluationContext context = new StandardEvaluationContext(parameters);
		context.addPropertyAccessor(new MapAccessor());
		return (T) expression.getValue(context, value.getClass());
	}

	@Override public Shell getShell() {
		return lampContext.getShell();
	}

	@Override public AppStatus getStatus() {
		if (System.currentTimeMillis() - lastCheckTimeMillis > appConfig.getCheckStatusInterval()) {
			return checkAndUpdateStatus();
		}

		return appStatus;
	}

	@Override public AppStatus updateStatus(AppStatus status) {
		this.appStatus = status;
		return this.appStatus;
	}

	@Override public AppStatus checkAndUpdateStatus() {
		lastCheckTimeMillis = System.currentTimeMillis();
		AppProcessState processStatus = getProcessStatus();
		if (AppProcessState.RUNNING.equals(processStatus)) {
			return updateStatus(AppStatus.RUNNING);
		} else {
			return updateStatus(AppStatus.NOT_RUNNING);
		}
	}

	public abstract AppProcess getProcess();

	public AppProcessState getProcessStatus() {
		AppProcess process = getProcess();
		return process != null ? process.getStatus() : AppProcessState.NOT_RUNNING;
	}

}
