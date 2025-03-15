package vn.com.leaselink.commons.metrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.ObservationRegistry;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.com.leaselink.commons.enumeration.HttpStatusOutcome;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


@Component
@AllArgsConstructor
public class ReactiveMetricsRecorder {

    private final MeterRegistry meterRegistry;
    private final ObservationRegistry observationRegistry;


    /**
     * Record API request metrics
     */
    public void recordHttpRequest(ServerWebExchange exchange, Instant startTime, int statusCode) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        String outcome = String.valueOf(HttpStatusOutcome.fromStatusCode(statusCode));
        Duration duration = Duration.between(startTime, Instant.now());

        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("path", normalizePath(path)));
        tags.add(Tag.of("method", method));
        tags.add(Tag.of("status", String.valueOf(statusCode)));
        tags.add(Tag.of("outcome", outcome));

        // Thêm tenant ID nếu có
        String tenantId = exchange.getRequest().getHeaders().getFirst("X-Tenant-ID");
        if (tenantId != null) {
            tags.add(Tag.of("tenant", tenantId));
        }

        meterRegistry.timer("http.server.requests", tags)
                .record(duration.toMillis(), TimeUnit.MILLISECONDS);
    }
    /**
     * Transformer để đo lường thời gian thực thi của một reactive operation
     */
    public <T> Function<Mono<T>, Mono<T>> recordOperation(
            String operationName, String... tags) {

        return mono -> Mono.deferContextual(ctx -> {
            Instant startTime = Instant.now();

            return mono
                    .doOnSuccess(result -> {
                        Duration duration = Duration.between(startTime, Instant.now());
                        recordOperationMetrics(operationName, "success", duration, tags);
                    })
                    .doOnError(error -> {
                        Duration duration = Duration.between(startTime, Instant.now());
                        recordOperationMetrics(operationName, "error", duration, tags);
                        recordErrorMetrics(operationName, error, tags);
                    });
        });
    }

    /**
     * Ghi nhận cache hit rate
     */
    public void recordCacheHit(String cacheName, boolean hit) {
        meterRegistry.counter("cache.access",
                        List.of(Tag.of("cache", cacheName), Tag.of("result", hit ? "hit" : "miss")))
                .increment();
    }

    /**
     * Ghi nhận độ trễ database transaction
     */
    public void recordDatabaseLatency(String operation, String database, Duration duration) {
        meterRegistry.timer("database.transaction.duration",
                        List.of(Tag.of("operation", operation), Tag.of("database", database)))
                .record(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Ghi nhận thông tin về connection pool
     */
    public void recordConnectionPoolMetrics(String poolName, int activeConnections, int idleConnections, int totalConnections, int waitingThreads) {
        meterRegistry.gauge("db.connections.active",
                List.of(Tag.of("pool", poolName)), activeConnections);
        meterRegistry.gauge("db.connections.idle",
                List.of(Tag.of("pool", poolName)), idleConnections);
        meterRegistry.gauge("db.connections.total",
                List.of(Tag.of("pool", poolName)), totalConnections);
        meterRegistry.gauge("db.connections.waiting",
                List.of(Tag.of("pool", poolName)), waitingThreads);
    }

    /**
     * Ghi nhận resource usage
     */
    public void recordResourceUsage(String resourceType, String resourceName, double usage) {
        meterRegistry.gauge("resource.usage",
                List.of(Tag.of("type", resourceType), Tag.of("name", resourceName)), usage);
    }

    // Helper methods

    private void recordOperationMetrics(String operationName, String outcome, Duration duration, String... extraTags) {
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("operation", operationName));
        tags.add(Tag.of("outcome", outcome));

        // Thêm extra tags
        if (extraTags != null && extraTags.length > 0) {
            for (int i = 0; i < extraTags.length - 1; i += 2) {
                if (i + 1 < extraTags.length) {
                    tags.add(Tag.of(extraTags[i], extraTags[i + 1]));
                }
            }
        }

        meterRegistry.timer("operation.duration", tags)
                .record(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void recordErrorMetrics(String operationName, Throwable error, String... extraTags) {
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("operation", operationName));
        tags.add(Tag.of("exception", error.getClass().getSimpleName()));

        // Thêm extra tags
        if (extraTags != null && extraTags.length > 0) {
            for (int i = 0; i < extraTags.length - 1; i += 2) {
                if (i + 1 < extraTags.length) {
                    tags.add(Tag.of(extraTags[i], extraTags[i + 1]));
                }
            }
        }

        meterRegistry.counter("operation.errors", tags).increment();
    }

    private String normalizePath(String path) {
        // Normalize các path parameters như /users/123 thành /users/{id}
        return path.replaceAll("/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "/{uuid}")
                .replaceAll("/[0-9]+", "/{id}");
    }


}
