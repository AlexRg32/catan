package com.catan.server.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTagsCustomizer() {
    return registry -> registry.config().commonTags("app", "catan-server");
  }

  @Bean
  MeterFilter commandMetricsRenameFilter() {
    return MeterFilter.renameTag("catan.command.latency", "command", "command");
  }
}
