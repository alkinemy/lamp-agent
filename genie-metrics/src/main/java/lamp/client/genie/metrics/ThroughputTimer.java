package lamp.client.genie.metrics;

import com.codahale.metrics.Timer;

import java.util.concurrent.TimeUnit;

public class ThroughputTimer extends Timer {

	private final InstantThroughput instantThroughput = new InstantThroughput();

	public Long getValue() {
		return instantThroughput.count();
	}

	@Override
	public void update(long duration, TimeUnit unit) {
		super.update(duration, unit);
		instantThroughput.mark();
	}

}
