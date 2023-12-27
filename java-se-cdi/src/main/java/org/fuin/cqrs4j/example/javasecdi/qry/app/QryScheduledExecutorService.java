package org.fuin.cqrs4j.example.javasecdi.qry.app;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * CDI producer that creates an {@link ScheduledExecutorService}.
 */
@ApplicationScoped
public class QryScheduledExecutorService {

    @Produces
    @ApplicationScoped
    public ScheduledExecutorService create(ThreadFactory threadFactory) {
        return new ScheduledThreadPoolExecutor(1, threadFactory, (runnable, executor) -> {
            System.out.println("Execution blocked");
        });
    }

}
