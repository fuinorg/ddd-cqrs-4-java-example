package org.fuin.cqrs4j.example.javasecdi.qry.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Creates an thread factory.
 */
@ApplicationScoped
public class QryThreadFactoryFactory {

    @ApplicationScoped
    @Produces
    public ThreadFactory create() {
        return Executors.defaultThreadFactory();
    }

}
