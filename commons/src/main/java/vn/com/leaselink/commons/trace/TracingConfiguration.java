package vn.com.leaselink.commons.trace;

import brave.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.sampler.Sampler;
import org.springframework.web.server.WebFilter;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.brave.ZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

import static zipkin2.reporter.AsyncReporter.create;

@Configuration
public class TracingConfiguration {

    @Bean
    public Tracing tracing() {
        OkHttpSender sender = OkHttpSender.create("http://zipkin:9411/api/v2/spans");
        try (AsyncReporter<Span> reporter = create(sender)) {
            return Tracing.newBuilder()
                    .localServiceName("commons")
                    .propagationFactory(B3Propagation.newFactoryBuilder()
                            .injectFormat(B3Propagation.Format.SINGLE)
                            .build())
                    .addSpanHandler(ZipkinSpanHandler.create(reporter))
                    .sampler(Sampler.ALWAYS_SAMPLE)
                    .build();
        }
    }

    @Bean
    public Tracer tracer(Tracing tracing) {
        return tracing.tracer();
    }

    @Bean
    public WebFilter tracingFilter(Tracer tracer) {
        return (exchange, chain) -> {
            String traceId = tracer.currentSpan().context().traceIdString();

            return chain.filter(exchange)
                    .contextWrite(context -> context.put("traceId", traceId));
        };
    }

}
