package com.catan.server.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class CommandMetricsService {

  private final MeterRegistry meterRegistry;

  public CommandMetricsService(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  public void recordLatency(String commandType, String outcome, long elapsedNanos) {
    Timer.builder("catan.command.latency")
        .tag("command", commandType)
        .tag("outcome", outcome)
        .publishPercentileHistogram()
        .register(meterRegistry)
        .record(elapsedNanos, TimeUnit.NANOSECONDS);
  }

  public void recordValidationFailure(String commandType) {
    Counter.builder("catan.command.validation.failures")
        .tag("command", commandType)
        .register(meterRegistry)
        .increment();
  }
}
