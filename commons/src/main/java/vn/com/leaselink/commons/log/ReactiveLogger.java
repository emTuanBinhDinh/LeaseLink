package vn.com.leaselink.commons.log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.parameters.P;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
public class ReactiveLogger {
    private static final String CORRELATION_ID = "correlationId";

    public static <T> Consumer<Signal<T>> logOnNext(Logger log, String format, Function<T, Object[]> argSupplier) {
        return signal -> {
            if(signal.isOnNext()) {
                log.info(format, argSupplier.apply(signal.get()));
            }
        };
    }
    public static <T> Consumer<Signal<T>> logOnError(Logger log, String format) {
        return signal -> {
            if (signal.isOnError()) {
                log.error(format, signal.getThrowable());
            }
        };
    }

    public static <T> Function<Mono<T>, Mono<T>> logWithCorrelationId(Logger log, String message) {
        return mono -> mono
                .doOnEach(logWithContext(log, message))
                .contextWrite(context -> context.getOrEmpty(CORRELATION_ID)
                        .map(cid -> context)
                        .orElseGet(() -> context.put(CORRELATION_ID, "NO_CORRELATION_ID")));
    }

    private static Consumer<Signal<?>> logWithContext(Logger log, String message) {
        return signal -> {
            if(signal.isOnNext() || signal.isOnError()) {
                Optional<Object> contextOptional = signal.getContextView().getOrEmpty(CORRELATION_ID)
                        .map(correlationId -> {
                            MDC.put(CORRELATION_ID, correlationId.toString());
                            return signal.getContextView();
                        });
                try {
                    if(signal.isOnNext()) {
                        log.info(message);
                    } else if(signal.isOnError()) {
                        log.error(message, signal.getThrowable());
                    }
                } finally {
                        contextOptional.ifPresent(context -> MDC.clear());
                }
            }
        };
    }
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }


}
