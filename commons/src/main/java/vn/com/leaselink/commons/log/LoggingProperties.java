package vn.com.leaselink.commons.log;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@ConfigurationProperties(prefix = "logging.reactive")
public class LoggingProperties {

    /**
     * Có enable logging hay không
     */
    @Setter
    private boolean enabled = true;

    /**
     * Sample rate cho logs (1.0 = log everything, 0.1 = log 10%)
     */
    @Setter
    private double sampleRate = 1.0;

    /**
     * Có log request/response payloads không
     */
    @Setter
    private boolean includePayload = true;

    /**
     * Độ dài tối đa của payload để log
     */
    @Setter
    private int maxPayloadLength = 10000;

    /**
     * Có mask thông tin nhạy cảm không
     */
    @Setter
    private boolean maskSensitiveData = true;

    /**
     * Danh sách các fields được coi là nhạy cảm cần mask
     */
    @Setter
    private List<String> sensitiveFields = new ArrayList<>(
            List.of("password", "ssn", "creditCard", "secret", "token", "authorization"));

    /**
     * Log configuration cho mỗi loại log
     */
    private final RequestLog request = new RequestLog();
    private final ResponseLog response = new ResponseLog();
    private final ErrorLog error = new ErrorLog();
    private final MetricsLog metrics = new MetricsLog();

    /**
     * Configuration cho MDC keys
     */
    private final MdcKeys mdcKeys = new MdcKeys();

    /**
     * Configuration cho request logging
     */
    @Setter
    @Getter
    public static class RequestLog {
        private boolean enabled = true;
        private boolean includeHeaders = true;
        private boolean includeQueryParams = true;
        private boolean includeClientInfo = true;

    }

    /**
     * Configuration cho response logging
     */
    @Setter
    @Getter
    public static class ResponseLog {
        private boolean enabled = true;
        private boolean includeHeaders = true;
        private boolean includeTimeTaken = true;

    }

    /**
     * Configuration cho error logging
     */
    @Setter
    @Getter
    public static class ErrorLog {
        private boolean includeStackTrace = true;
        private boolean includeRequestDetails = true;

    }

    /**
     * Configuration cho metrics logging
     */
    @Setter
    @Getter
    public static class MetricsLog {
        private boolean enabled = true;
        private boolean logSlowRequests = true;
        private long slowRequestThresholdMs = 1000;

    }

    /**
     * Tên các keys sử dụng trong MDC
     */
    @Setter
    @Getter
    public static class MdcKeys {
        private String requestId = "requestId";
        private String traceId = "traceId";
        private String spanId = "spanId";
        private String userId = "userId";
        private String tenantId = "tenantId";
        private String operation = "operation";
        private String path = "path";
        private String method = "method";

    }

}
