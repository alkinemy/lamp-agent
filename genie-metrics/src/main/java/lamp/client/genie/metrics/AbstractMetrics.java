package lamp.client.genie.metrics;


import com.codahale.metrics.*;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Objects;

public class AbstractMetrics {

	private static final Function<Metric, ThroughputMeter> THROUGHPUT_METER = new Function<Metric, ThroughputMeter>() {
		@Nullable @Override public ThroughputMeter apply(Metric metric) {
			if (metric != null) {
				return (ThroughputMeter) metric;
			} else {
				return new ThroughputMeter();
			}
		}
	};

	private static final Function<Metric, ThroughputTimer> THROUGHPUT_TIMER = new Function<Metric, ThroughputTimer>() {
		@Nullable @Override public ThroughputTimer apply(@Nullable Metric metric) {
			if (metric != null) {
				return (ThroughputTimer) metric;
			} else {
				return new ThroughputTimer();
			}
		}
	};

	protected final String baseName;
	protected final MetricRegistry registry;

	public AbstractMetrics(String baseName, MetricRegistry registry) {
		Objects.requireNonNull(baseName);
		Objects.requireNonNull(registry);

		this.baseName = baseName;
		this.registry = registry;
	}

	protected String name(String... names) {
		return MetricRegistry.name(baseName, names);
	}

	public <T> Gauge<T> gauge(Gauge<T> gauge, String... names) {
		return registry.register(name(names), gauge);
	}

	public Counter counter(String... names) {
		return registry.counter(name(names));
	}

	public Histogram histogram(String... names) {
		return registry.histogram(name(names));
	}

	public Meter meter(String... names) {
		return registry.meter(name(names));
	}

	public Timer timer(String... names) {
		return registry.timer(name(names));
	}

	public ThroughputMeter throughputMeter(String... names) {
		return getOrAdd(name(names), THROUGHPUT_METER);
	}

	public ThroughputTimer throughputTimer(String... names) {
		return getOrAdd(name(names), THROUGHPUT_TIMER);
	}

	public <M extends Metric> M getOrAdd(String name, Function<Metric, M> metricProvider) {
		Metric metric = registry.getMetrics().get(name);
		M found = metric != null ? metricProvider.apply(metric) : null;
		if (found != null) {
			return found;
		} else if (metric == null) {
			try {
				return registry.register(name, metricProvider.apply(null));
			} catch (IllegalArgumentException e) {
				metric = registry.getMetrics().get(name);
				found = metricProvider.apply(metric);
				if (found != null) {
					return found;
				}
			}
		}
		throw new IllegalArgumentException(name + " is already used for a different type of metric");
	}

	public void remove(String... names) {
		registry.remove(name(names));
	}

}
