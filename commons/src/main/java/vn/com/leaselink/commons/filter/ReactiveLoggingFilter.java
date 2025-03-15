package vn.com.leaselink.commons.filter;

import brave.Tracer;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import vn.com.leaselink.commons.enumeration.CodeResponse;
import vn.com.leaselink.commons.trace.ReactiveContextPropagation;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
public class ReactiveLoggingFilter implements WebFilter{
    private static final Logger log = LoggerFactory.getLogger(ReactiveLoggingFilter.class);

    private final Tracer tracer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Instant startTime = Instant.now();

        String requestPath = request.getPath().value();
        String method = request.getMethod().name();
        String clientIP = getClientIP(request);
        String userAgent = getUserAgent(request);

        // Lấy hoặc tạo các ID cần thiết cho tracing
        String requestId = getOrCreateRequestsId(request);
        String traceId = getOrCreateTraceId();
        String spanId = getOrCreateSpanId();
        String userId = getUserId(request);
        String sessionId = getSessionId(request);
        String tenantId = getTenantId(request);

        // Log request
        log.info("Request: {} {} from {} [User-Agent: {}] [requestId: {}] [traceId: {}]",
                method, requestPath, clientIP, userAgent, requestId, traceId);

        // Tạo context để truyền xuyên suốt reactive stream
        Context context = ReactiveContextPropagation.createContext(
                requestId, traceId, spanId, userId, sessionId, tenantId);

        MDC.put("requestId", requestId);
        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);
        MDC.put("method", method);
        MDC.put("path", requestPath);

        if (userId != null) MDC.put("userId", userId);
        if (tenantId != null) MDC.put("tenantId", tenantId);

        // Thêm thông tin vào request attributes
        exchange.getAttributes().put("requestId", requestId);
        exchange.getAttributes().put("startTime", startTime);

        // Thêm headers vào response
        exchange.getResponse().getHeaders().add("X-Request-ID", requestId);
        exchange.getResponse().getHeaders().add("X-Trace-ID", traceId);

        // Xử lý request và log response
        return chain.filter(exchange)
                .contextWrite(context)
                .doOnSuccess(v -> logResponse(exchange, startTime, null))
                .doOnError(error -> logResponse(exchange, startTime, error))
                .doFinally(signalType -> MDC.clear());
    }

    private void logResponse(ServerWebExchange exchange, Instant startTime, Throwable error) {
        long durationMS = Duration.between(startTime, Instant.now()).toMillis();
        int statusCode = exchange.getResponse().getStatusCode() != null
                ? exchange.getResponse().getStatusCode().value() :
                CodeResponse.ServerErrorCode.INTERNAL_SERVER.getIntCode();

        String requestId = (String) exchange.getAttributes().get("requestId");
        String requestPath= exchange.getRequest().getPath().value();
        String method= exchange.getRequest().getMethod().name();
        if(error != null) {
            log.error("Response: {} {} - Status: {} - Time: {}ms [requestId: {}] - Error: {}",
                    method, requestPath, statusCode, durationMS, requestId, error.getMessage(), error);
        } else {
            log.info("Response: {} {} - Status: {} - Time: {}ms [requestId: {}]",
                    method, requestPath, statusCode, durationMS, requestId);
        }

        if(durationMS > 1000) {
            log.warn("Slow request detected: {} {} - {}ms [requestId: {}]", method, requestPath, durationMS, requestId);
        }
    }

//    TODO Helper methods
    private String getOrCreateRequestsId (ServerHttpRequest request) {
        String requestId = request.getHeaders().getFirst("X-Request-ID");
        return (requestId != null) ? requestId : UUID.randomUUID().toString();
    }

    private String getOrCreateTraceId() {
        try {
            return tracer.currentSpan() !=null
                    ? tracer.currentSpan().context().traceIdString()
                    : UUID.randomUUID().toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }

    private String getOrCreateSpanId() {
        try {
            // Sử dụng Brave Tracer nếu đã được khởi tạo
            return tracer.currentSpan() != null
                    ? tracer.currentSpan().context().spanIdString()
                    : UUID.randomUUID().toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }

    private String getUserId(ServerHttpRequest request) {
        return request.getHeaders().getFirst("X-User-ID");
    }

    private String getSessionId(ServerHttpRequest request) {
        return request.getHeaders().getFirst("X-Session-ID");
    }

    private String getTenantId(ServerHttpRequest request) {
        return request.getHeaders().getFirst("X-Tenant-ID");
    }

    private String getClientIP(ServerHttpRequest request) {
        String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        return forwardedFor != null ? forwardedFor.split(",")[0].trim() : request.getRemoteAddress().getHostString();
    }

    private String getUserAgent(ServerHttpRequest request) {
        return request.getHeaders().getFirst("User-Agent");
    }



}
