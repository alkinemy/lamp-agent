package lamp.agent.genie.core.runtime.process.exec;

import org.apache.commons.exec.LogOutputStream;
import org.slf4j.Logger;

public class Slf4jLogOutputStream extends LogOutputStream {

	private final Logger logger;

	public Slf4jLogOutputStream(Logger logger) {
		this.logger = logger;
	}

	@Override protected void processLine(String line, int logLevel) {
		logger.info(line);
	}

}
