package org.fuin.cqrs4j.example.javasecdi.shared.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Creates an executor service.
 */
@ApplicationScoped
public class SharedExecutorServiceFactory {

    @ApplicationScoped
    @Produces
    public ExecutorService createExecutorService() {
        return Executors.newFixedThreadPool(10);
    }

}
