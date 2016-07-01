package lamp.agent.genie.spring.boot.base.impl.simple;

import lamp.agent.genie.core.AppStatus;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.core.app.simple.SimpleAppContainer;
import lamp.agent.genie.core.app.simple.SimpleAppContext;
import lamp.agent.genie.core.app.simple.resource.AppResource;
import lamp.agent.genie.core.app.simple.resource.ArtifactAppResource;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcess;
import lamp.agent.genie.core.app.simple.runtime.process.AppProcessState;
import lamp.agent.genie.core.app.simple.runtime.shell.Shell;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractSimpleAppContext implements SimpleAppContext {

	@Getter
	private String id;
	@Getter
	private final LampContext lampContext;
	@Getter
	private final SimpleAppContainer appContainer;

	private ExpressionParser parser = new SpelExpressionParser();

	private AppStatus appStatus = AppStatus.STOPPED;
	private long lastCheckTimeMillis = 1000;

	public AbstractSimpleAppContext(LampContext lampContext, SimpleAppContainer appContainer) {
		this.lampContext = lampContext;
		this.appContainer = appContainer;
		this.id = appContainer.getId();
	}

	public File getAppMetaInfoDirectory() {
		return getLampContext().getAppMetaInfoDirectory(getId());
	}


	@Override
	public InputStream getStdOutInputStream() throws IOException {
		String filename = appContainer.getStdOutFile();
		if (StringUtils.isNotBlank(filename)) {
			return new FileInputStream(new File(filename));
		}
		return null;
	}

	@Override
	public InputStream getStdErrInputStream() throws IOException {
		String filename = appContainer.getStdErrFile();
		if (StringUtils.isNotBlank(filename)) {
			return new FileInputStream(new File(filename));
		}
		return null;
	}

	public SimpleAppContainer getParsedAppContainer() {
		Map<String, Object> parameters = getParameters();

		try {
			SimpleAppContainer parsedConfig = new SimpleAppContainer();
			BeanUtils.populate(parsedConfig, parameters);

			return parsedConfig;
		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_CONFIG_PARSE_FAILED, e);
		}
	}

	public Map<String, Object> getParameters() {

		try {
			Map<String, Object> parameters = new LinkedHashMap<>();
			parameters.put("id", appContainer.getId());
			parameters.put("name", appContainer.getName());
			AppResource appResource = appContainer.getAppResource();
			if (appResource instanceof ArtifactAppResource) {
				parameters.put("groupId", ((ArtifactAppResource) appResource).getGroupId());
				parameters.put("artifactId", ((ArtifactAppResource) appResource).getArtifactId());
				parameters.put("version", ((ArtifactAppResource) appResource).getVersion());
			}

			parameters.put("checkStatusInterval", appContainer.getCheckStatusInterval());
			parameters.put("preInstalled", appContainer.isPreInstalled());
			if (StringUtils.isBlank(appContainer.getAppDirectory())) {
				File appDir = new File(getAppMetaInfoDirectory(), "app");
				parameters.put("appDirectory", appDir.getAbsolutePath());
			} else {
				parameters.put("appDirectory", appContainer.getAppDirectory());
			}
			parameters.put("workDirectory", appContainer.getWorkDirectory());
			parameters.put("logDirectory", appContainer.getLogDirectory());
			parameters.put("pidFile", appContainer.getPidFile());
			parameters.put("ptql", appContainer.getPtql());
			parameters.put("stdOutFile", appContainer.getStdOutFile());
			parameters.put("stdErrFile", appContainer.getStdErrFile());

			parameters.put("processType", appContainer.getProcessType());
			parameters.put("commandShell", appContainer.getCommandShell());
			parameters.put("startCommandLine", appContainer.getStartCommandLine());
			parameters.put("startTimeout", appContainer.getStartTimeout());
			parameters.put("stopCommandLine", appContainer.getStopCommandLine());
			parameters.put("stopTimeout", appContainer.getStopTimeout());
			parameters.put("monitor", appContainer.isMonitor());

			parameters.put("hostname", lampContext.getHostname());
			parameters.put("address", lampContext.getAddress());

			parameters.put("filename", appContainer.getInstallFilename());

			if (lampContext instanceof EnvironmentCapable) {
				Environment environment = ((EnvironmentCapable) lampContext).getEnvironment();
				parameters.put("activeProfiles", environment.getActiveProfiles());
				parameters.put("env", environment);
			}

			if (appContainer.getParameters() != null) {
				parameters.putAll(appContainer.getParameters());
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
					log.debug("{} = {}", entry.getKey(), expValue);
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


	@Override public Shell getShell() {
		return lampContext.getShell();
	}

	@Override public boolean isProcessRunning() {
		return AppStatus.RUNNING.equals(getStatus());
	}

	@Override public AppStatus getStatus() {
		if (System.currentTimeMillis() - lastCheckTimeMillis > appContainer.getCheckStatusInterval()) {
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
				return updateStatus(AppStatus.STOPPED);
			} else {
				return updateStatus(AppStatus.UNKNOWN);
			}
		}
		return appStatus;
	}

	@Override
	public synchronized void startProcess() {
		AppStatus currentStatus = getStatus();
		boolean canStart = AppStatus.STOPPED.equals(currentStatus);
		if (!canStart) {
			throw Exceptions.newException(ErrorCode.APP_IS_ALREADY_RUNNING);
		}

		try {
			updateStatus(AppStatus.STARTING);

			doCreateProcess();
		} catch (Exception e) {
			updateStatus(AppStatus.STOPPED);
			throw Exceptions.newException(ErrorCode.APP_START_FAILED, e);
		}
	}

	@Override
	public synchronized void stopProcess() {

		AppStatus currentStatus = getStatus();
		boolean canStop = AppStatus.RUNNING.equals(currentStatus);
		if (!canStop) {
			throw Exceptions.newException(ErrorCode.APP_IS_NOT_RUNNING);
		}

		try {
			updateStatus(AppStatus.STOPPING);

			doTerminateProcess();

			if (appContainer.getStopTimeout() > 0) {
				AppProcess process = getProcess();
				long timeout = appContainer.getStopTimeout() / 1000;
				for (long i = 0; i < timeout && AppProcessState.RUNNING.equals(process.getStatus()); timeout++) {
					log.info("Waiting for process to stop... {} ({}/{})", getId(), i, timeout);
					Thread.sleep(1000);
				}
			}

		} catch (Exception e) {
			throw Exceptions.newException(ErrorCode.APP_STOP_FAILED, e);
		}
	}



	public abstract AppProcess getProcess();

}
