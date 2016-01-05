package lamp.agent.genie.metrics;

import java.util.concurrent.TimeUnit;

public class InstantThroughput {

	private static final long ONE_SEC = TimeUnit.SECONDS.toNanos(1);
	private static final long TWO_SECS = TimeUnit.SECONDS.toNanos(2);

	private volatile long prevCount = -1;
	private volatile long timestamp = 0;
	private volatile long count = 0;

	void check() {
		long now = System.nanoTime();
		if (now - timestamp > ONE_SEC) {
			if (now - timestamp < TWO_SECS) {
				prevCount = count;
			} else {
				if (prevCount > 0) {
					prevCount = 0;
				}
			}
			timestamp = now;
			count = 0;
		}
	}

	public void mark() {
		check();
		count++;
	}

	public long count() {
		check();
		return prevCount != -1 ? prevCount : count;
	}
}
