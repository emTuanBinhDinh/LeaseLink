package vn.com.leaselink.commons.log;

import brave.Tracer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Hooks;
import vn.com.leaselink.commons.exception.GlobalErrorHandler;
import vn.com.leaselink.commons.filter.ReactiveLoggingFilter;
import vn.com.leaselink.commons.metrics.ReactiveMetricsRecorder;

@AutoConfiguration
@EnableConfigurationProperties(LoggingProperties.class)
@ConditionalOnClass(name = {"reactor.core.publisher.Flux", "org.springframework.web.reactive.DispatcherHandler"})
@Import({ReactorHooksConfiguration.class})
@AllArgsConstructor
public class LoggingAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LoggingAutoConfiguration.class);

    private final LoggingProperties properties;
    private final Environment environment;



    @Bean
    @ConditionalOnMissingBean
    public ReactiveLoggingFilter reactiveLoggingFilter(Tracer tracer) {
        log.info("Initializing Reactive Logging Filter");
        return new ReactiveLoggingFilter(tracer);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(MeterRegistry.class)
    public ReactiveMetricsRecorder reactiveMetricsRecorder(MeterRegistry meterRegistry,
                                                           ObservationRegistry observationRegistry) {
        log.info("Initializing Reactive Metrics Recorder");
        return new ReactiveMetricsRecorder(meterRegistry, observationRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public GlobalErrorHandler globalErrorHandler(Tracer tracer, MeterRegistry meterRegistry) {
        log.info("Initializing Global Error Handler");
        return new GlobalErrorHandler(tracer, meterRegistry);
    }

    /**
     * Configuration for AOP related beans
     */
    @Configuration
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    @ComponentScan("vn.com.leaselink")
    static class ReactiveLogAspectConfiguration {

//        @Bean
//        @ConditionalOnMissingBean
//        @ConditionalOnBean(Tracer.class)
//        public ReactiveLogAspect reactiveLogAspect(Tracer tracer) {
//            log.info("Initializing Reactive Log Aspect");
//            return new com.example.commons.logging.aop.ReactiveLogAspect(tracer);
//        }
    }

    /**
     * Init method to log configuration status
     */
    @Bean
    public LoggingInitializer loggingInitializer() {
        String appName = environment.getProperty("spring.application.name", "unknown-application");
        log.info("Enhanced Reactive Logging initialized for application: {}", appName);
        log.info("Logging configuration: sampleRate={}, includePayload={}, maxPayloadLength={}",
                properties.getSampleRate(),
                properties.isIncludePayload(),
                properties.getMaxPayloadLength());

        return new LoggingInitializer();
    }

    /**
     * Dummy bean to trigger initialization logging
     */
    public static class LoggingInitializer {
        // Empty class just for initialization purposes
    }


}
