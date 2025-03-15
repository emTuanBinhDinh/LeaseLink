package vn.com.leaselink.commons.trace;

import org.slf4j.MDC;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReactiveContextPropagation {

    private static final String REQUEST_ID = "requestId";
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";
    private static final String USER_ID = "userId";
    private static final String SESSION_ID = "sessionId";
    private static final String TENANT_ID = "tenantId";

    public static Context createContext(String requestId, String traceId, String spanId,
                                        String userId, String sessionId, String tenantId) {
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put(REQUEST_ID, requestId);
        contextMap.put(TRACE_ID, traceId);
        contextMap.put(SPAN_ID, spanId);
        contextMap.put(USER_ID, userId);
        contextMap.put(SESSION_ID, sessionId);
        contextMap.put(TENANT_ID, tenantId);
        return Context.of(contextMap);
    }

    /**
     * Transformer để đặt MDC từ Context trong Reactive chain
     */
    public static <T> Function<Mono<T>, Mono<T>> withMdc() {
        return mono -> mono
                .doOnEach(signal -> {
                    if (signal.isOnNext() || signal.isOnError()) {
                        signal.getContextView().forEach((key, value) -> {
                            if (value instanceof String) {
                                MDC.put(key.toString(), (String) value);
                            }
                        });
                    }
                })
                .doFinally(signalType -> MDC.clear());
    }


    /**
     * Lấy tất cả context values và đặt vào MDC
     */
    public static Mono<Map<String, String>> captureContextToMdc() {
        return Mono.deferContextual(ctx -> {
            ctx.forEach((key, value) -> {
                if (value instanceof String) {
                    MDC.put(key.toString(), (String) value);
                }
            });
            return Mono.just(MDC.getCopyOfContextMap());
        });
    }


    /**
     * Đảm bảo propagation giữa các thread khác nhau
     */
    public static <T> Function<Mono<T>, Mono<T>> contextPropagation() {
        return mono -> Mono.deferContextual(ctx -> {
            Map<String, String> contextMap = captureContextMapFromReactorContext(ctx);
            return mono.contextWrite(context -> {
                Context newContext = context;
                for (Map.Entry<String, String> entry : contextMap.entrySet()) {
                    newContext = newContext.put(entry.getKey(), entry.getValue());
                }
                return newContext;
            });
        });
    }


    private static Map<String, String> captureContextMapFromReactorContext(reactor.util.context.ContextView ctx) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        if (contextMap == null) {
            contextMap = new java.util.HashMap<>();
        }

        addToMapIfPresent(contextMap, ctx, REQUEST_ID);
        addToMapIfPresent(contextMap, ctx, TRACE_ID);
        addToMapIfPresent(contextMap, ctx, SPAN_ID);
        addToMapIfPresent(contextMap, ctx, USER_ID);
        addToMapIfPresent(contextMap, ctx, SESSION_ID);
        addToMapIfPresent(contextMap, ctx, TENANT_ID);

        return contextMap;
    }

    private static void addToMapIfPresent(Map<String, String> map, reactor.util.context.ContextView ctx, String key) {
        ctx.getOrEmpty(key).ifPresent(value -> {
            if (value instanceof String) {
                map.put(key, (String) value);
            }
        });
    }

}
