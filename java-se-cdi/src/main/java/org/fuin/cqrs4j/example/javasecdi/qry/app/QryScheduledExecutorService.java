package org.fuin.cqrs4j.example.javasecdi.qry.app;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * CDI producer that creates an {@link ScheduledExecutorService}.
 */
@ApplicationScoped
public class QryScheduledExecutorService {

    @Produces
    @ApplicationScoped
    public ScheduledExecutorService create(ThreadFactory threadFactory) {
        return new ScheduledThreadPoolExecutor(1, threadFactory, (runnable, executor) -> { System.out.println("Execution blocked"); });
    }
    
}
