package vn.com.leaselink.commons.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;
import reactor.tools.agent.ReactorDebugAgent;

@Configuration
@ConditionalOnClass(Hooks.class)
public class ReactorHooksConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ReactorHooksConfiguration.class);

    @Bean
    public ReactorContextPropagationEnabler enableContextPropagation() {
        log.info("Enabling Reactor Context Propagation");
        Hooks.enableAutomaticContextPropagation();
        return new ReactorContextPropagationEnabler();
    }

    @Bean
    @ConditionalOnClass(ReactorDebugAgent.class)
    public ReactorDebugEnabler enableReactorDebug() {
        try {
            log.info("Enabling Reactor Debug Agent");
            ReactorDebugAgent.init();
            ReactorDebugAgent.processExistingClasses();
            return new ReactorDebugEnabler();
        } catch (Throwable e) {
            log.warn("Failed to initialize Reactor Debug Agent: {}", e.getMessage());
            return new ReactorDebugEnabler();
        }
    }

    /**
     * Marker class for Reactor context propagation
     */
    public static class ReactorContextPropagationEnabler {
        // Marker class
    }

    /**
     * Marker class for Reactor debug agent
     */
    public static class ReactorDebugEnabler {
        // Marker class
    }

}
