package vn.com.leaselink.commons.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


public class AsyncLogger {
    private final Logger logger;

    public AsyncLogger(Class <?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public Mono<Void> info(String message) {
        return Mono.fromRunnable(() -> logger.info(message))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> error(String message, Throwable throwable) {
        return Mono.fromRunnable(() -> logger.error(message, throwable))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public <T> Mono<T> infoWithContext(T data, String message) {
        return info(message+ ":" + data).thenReturn(data);
    }

}
