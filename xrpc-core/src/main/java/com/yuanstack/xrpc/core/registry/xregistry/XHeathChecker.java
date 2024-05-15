package com.yuanstack.xrpc.core.registry.xregistry;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * schedule to check kk registry.
 *
 * @author Sylvan
 * @date 2024/05/15  0:42
 */
@Slf4j
public class XHeathChecker {

    ScheduledExecutorService consumerExecutor = null;
    ScheduledExecutorService providerExecutor = null;

    public void start() {
        log.info(" ====>>>> [XRegistry] : start with health checker.");
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);
    }

    public void stop() {
        log.info(" ====>>>> [XRegistry] : stop with health checker.");
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(providerExecutor);
    }

    public void providerCheck(Callback callback) {
        providerExecutor.scheduleAtFixedRate(() -> {
            try {
                callback.call();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void consumerCheck(Callback callback) {
        consumerExecutor.scheduleAtFixedRate(() -> {
            try {
                callback.call();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    private void gracefulShutdown(ScheduledExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public interface Callback {
        void call() throws Exception;
    }
}
