package vn.com.leaselink.commons.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Enhanced Reactive Logger - Cung cấp các tiện ích đầy đủ cho việc log trong môi trường reactive
 */
public class EnhancedReactiveLogger {

    private static final String REQUEST_ID = "requestId";
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";
    private static final String USER_ID = "userId";
    private static final String TENANT_ID = "tenantId";
    private static final String OPERATION = "operation";

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Transformer cho Flux có log entry và exit point
     */
    public static <T> Function<Flux<T>, Flux<T>> logOperationFlux(
            Logger logger, String operationName, String... params) {
        return flux -> {
            Map<String, String> contextMap = new HashMap<>();
            contextMap.put(OPERATION, operationName);

            return Flux.deferContextual(ctx -> {
                        Map<String, String> fullContext = mergeMaps(
                                extractContextMap(ctx),
                                contextMap
                        );

                        withMDC(fullContext, () ->
                                logger.info("STREAM START: {}{}",
                                        operationName, formatParams(params)));

                        return flux;
                    })
                    .doOnComplete(() -> Flux.deferContextual(ctx -> {
                        Map<String, String> fullContext = mergeMaps(
                                extractContextMap(ctx),
                                contextMap
                        );

                        withMDC(fullContext, () ->
                                logger.info("STREAM COMPLETE: {}{}",
                                        operationName, formatParams(params)));
                        return Flux.empty();
                    }).subscribe())
                    .doOnError(error -> Flux.deferContextual(ctx -> {
                        Map<String, String> fullContext = mergeMaps(
                                extractContextMap(ctx),
                                contextMap
                        );

                        withMDC(fullContext, () ->
                                logger.error("STREAM ERROR: {} - Error: {}{}",
                                        operationName, error.getMessage(), formatParams(params), error));
                        return Flux.empty();
                    }).subscribe())
                    .contextWrite(context -> addToContext(context, contextMap));
        };
    }

    private static Context addToContext(Context context, Map<String, String> map) {
        Context result = context;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result = result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    /**
     * Log mỗi item trong Flux stream
     */
    public static <T> Function<Flux<T>, Flux<T>> logItems(
            Logger logger, String operationName) {
        return flux -> flux.doOnEach(logOnNext(logger,
                "ITEM: " + operationName + " - Value: {}",
                (T t) -> new Object[] { t }));
    }

    /**
     * Consumer để log onNext signals với context
     */
    public static <T> Consumer<Signal<T>> logOnNext(
            Logger logger, String format, Function<T, Object[]> argSupplier) {
        return signal -> {
            if (signal.isOnNext()) {
                Optional<Context> contextOptional = signal.getContextView().getOrEmpty(REQUEST_ID)
                        .map(correlationId -> (Context) signal.getContextView());

                if (contextOptional.isPresent()) {
                    reactor.util.context.ContextView contextView = signal.getContextView();
                    Map<String, String> contextMap = extractContextMap(contextView);

                    withMDC(contextMap, () -> {
                        try {
                            logger.info(format, argSupplier.apply(signal.get()));
                        } catch (Exception e) {
                            logger.error("Error logging: {}", e.getMessage(), e);
                        }
                    });
                } else {
                    try {
                        logger.info(format, argSupplier.apply(signal.get()));
                    } catch (Exception e) {
                        logger.error("Error logging: {}", e.getMessage(), e);
                    }
                }
            }
        };
    }

    /**
     * Consumer để log onError signals với context
     */
    public static <T> Consumer<Signal<T>> logOnError(Logger logger, String format) {
        return signal -> {
            if (signal.isOnError()) {
                Optional<Context> contextOptional = signal.getContextView().getOrEmpty(REQUEST_ID)
                        .map(correlationId -> (Context) signal.getContextView());

                if (contextOptional.isPresent()) {
                    reactor.util.context.ContextView contextView = signal.getContextView();
                    Map<String, String> contextMap = extractContextMap(contextView);

                    withMDC(contextMap, () -> {
                        logger.error(format, signal.getThrowable().getMessage(), signal.getThrowable());
                    });
                } else {
                    logger.error(format, signal.getThrowable().getMessage(), signal.getThrowable());
                }
            }
        };
    }


    private static Map<String, String> extractContextMap(reactor.util.context.ContextView contextView) {
        Map<String, String> contextMap = new HashMap<>();

        addToMapIfPresent(contextMap, contextView, REQUEST_ID);
        addToMapIfPresent(contextMap, contextView, TRACE_ID);
        addToMapIfPresent(contextMap, contextView, SPAN_ID);
        addToMapIfPresent(contextMap, contextView, USER_ID);
        addToMapIfPresent(contextMap, contextView, TENANT_ID);
        addToMapIfPresent(contextMap, contextView, OPERATION);

        return contextMap;
    }


    private static void addToMapIfPresent(Map<String, String> map, reactor.util.context.ContextView ctx, String key) {
        ctx.getOrEmpty(key).ifPresent(value -> {
            if (value instanceof String) {
                map.put(key, (String) value);
            }
        });
    }


    private static Map<String, String> mergeMaps(Map<String, String> map1, Map<String, String> map2) {
        Map<String, String> result = new HashMap<>(map1);
        result.putAll(map2);
        return result;
    }

    private static String formatParams(String[] params) {
        if(params == null || params.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" [Params: ");
        for(int i = 0; i < params.length; i++) {
            if(i > 0) {
                sb.append(", ");
            }
            sb.append(params[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private static void withMDC(Map<String, String> contextMap, Runnable runnable) {
        Map<String, String> previousContext = MDC.getCopyOfContextMap();
        try {
            if(contextMap != null) {
                for(Map.Entry<String, String> entry : contextMap.entrySet()) {
                    MDC.put(entry.getKey(), entry.getValue());
                }
            }
            runnable.run();
        } finally {
            MDC.clear();
            if(previousContext != null) {
                assert contextMap != null;
                for(Map.Entry<String, String> entry : contextMap.entrySet()) {
                    MDC.put(entry.getKey(), entry.getValue());
                }
            }
        }


    }


}
