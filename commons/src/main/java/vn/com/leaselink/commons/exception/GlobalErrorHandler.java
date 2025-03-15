package vn.com.leaselink.commons.exception;

import brave.Tracer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.com.leaselink.commons.enumeration.HttpStatusOutcome;
import vn.com.leaselink.commons.log.EnhancedReactiveLogger;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(-2) // Cao hơn DefaultErrorWebExceptionHandler (-1)
@AllArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private static final Logger log = EnhancedReactiveLogger.getLogger(GlobalErrorHandler.class);

    private final Tracer tracer;
    private final MeterRegistry meterRegistry;





    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // Lấy request ID và thông tin từ exchange
        String requestId = exchange.getAttribute("requestId");
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        // Lấy thời gian bắt đầu request nếu có
        Instant startTime = exchange.getAttribute("startTime");
        long processingTimeMs = startTime != null
                ? Duration.between(startTime, Instant.now()).toMillis()
                : -1;

        // Xác định HTTP status và thông báo lỗi
        HttpStatus status = determineStatus(ex);
        String errorMessage = determineMessage(ex);
        HttpStatusOutcome outcome = HttpStatusOutcome.fromStatusCode(status.value());

        // Log error với đầy đủ context
        log.error("Request processing failed: {} {} [requestId: {}] - Status: {} ({}) - Error: {}",
                method, path, requestId, status.value(), outcome.name(), errorMessage, ex);

        // Record error metrics
        recordErrorMetrics(path, method, status.value(), ex, requestId, processingTimeMs);

        // Thêm thông tin từ Tracer nếu có
        String traceId = null;
        String spanId = null;
        if (tracer.currentSpan() != null) {
            traceId = tracer.currentSpan().context().traceIdString();
            spanId = tracer.currentSpan().context().spanIdString();

            // Log error vào span
            tracer.currentSpan()
                    .error(ex)
                    .tag("http.status_code", String.valueOf(status.value()))
                    .tag("error", ex.getClass().getSimpleName());
        }

        // Tạo response body
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("path", path);
        errorBody.put("status", status.value());
        errorBody.put("error", status.getReasonPhrase());
        errorBody.put("message", errorMessage);
        errorBody.put("requestId", requestId);

        if (traceId != null) {
            errorBody.put("traceId", traceId);
        }

        if (spanId != null) {
            errorBody.put("spanId", spanId);
        }

        if (processingTimeMs > 0) {
            errorBody.put("processingTimeMs", processingTimeMs);
        }

        // Xây dựng và trả về response
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(serializeErrorBody(errorBody).getBytes())));
    }

    /**
     * Xác định HTTP status từ loại exception
     */
    private HttpStatus determineStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return (HttpStatus) ((ResponseStatusException) ex).getStatusCode();
        }

        // Phân loại lỗi và ánh xạ sang HTTP status phù hợp
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex != null) {
            return HttpStatus.NOT_FOUND;
        }

        // Mặc định là internal server error
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * Trích xuất thông báo lỗi từ exception
     */
    private String determineMessage(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            return rse.getReason() != null ? rse.getReason() : rse.getMessage();
        }

        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return "An error occurred: " + ex.getClass().getSimpleName();
        }

        // Lọc lại message để loại bỏ các thông tin nhạy cảm hoặc quá chi tiết
        if (message.length() > 200) {
            return message.substring(0, 197) + "...";
        }

        return message;
    }

    /**
     * Ghi nhận metrics cho errors
     */
    private void recordErrorMetrics(String path, String method, int statusCode,
                                    Throwable error, String requestId, long processingTimeMs) {
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("path", normalizePath(path)));
        tags.add(Tag.of("method", method));
        tags.add(Tag.of("status", String.valueOf(statusCode)));
        tags.add(Tag.of("outcome", HttpStatusOutcome.fromStatusCode(statusCode).name()));
        tags.add(Tag.of("exception", error.getClass().getSimpleName()));

        // Thêm request ID nếu có
        if (requestId != null) {
            tags.add(Tag.of("requestId", requestId));
        }

        // Đếm số lượng lỗi
        meterRegistry.counter("http.server.errors", tags).increment();

        // Ghi nhận thời gian xử lý nếu có
        if (processingTimeMs > 0) {
            meterRegistry.timer("http.server.requests", tags)
                    .record(Duration.ofMillis(processingTimeMs));
        }
    }

    /**
     * Normalize đường dẫn để giảm cardinality của metrics
     */
    private String normalizePath(String path) {
        // Normalize các path parameters như /users/123 thành /users/{id}
        return path.replaceAll("/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "/{uuid}")
                .replaceAll("/[0-9]+", "/{id}");
    }

    /**
     * Serialize error body thành JSON
     */
    private String serializeErrorBody(Map<String, Object> errorBody) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : errorBody.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(entry.getKey()).append("\":");

            Object value = entry.getValue();
            if (value == null) {
                json.append("null");
            } else if (value instanceof Number) {
                json.append(value);
            } else if (value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Escape characters in JSON
     */
    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
