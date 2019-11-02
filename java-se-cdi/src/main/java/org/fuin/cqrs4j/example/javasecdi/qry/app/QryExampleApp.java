/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library. If not, see
 * http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.javasecdi.qry.app;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;

import org.fuin.ext4logback.LogbackStandalone;
import org.fuin.ext4logback.NewLogConfigFileParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal example command (write) application.
 */
public class QryExampleApp {

    private static final Logger LOG = LoggerFactory.getLogger(QryExampleApp.class);

    @Inject
    private ScheduledExecutorService scheduledExecutorService;

    @Inject
    private QryTimer qryTimer;

    /**
     * Executes the application.
     */
    public void execute() {
        LOG.info("Started execution...");

        final ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(qryTimer, 5, 1, TimeUnit.SECONDS);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }

        LOG.info("Ended execution...");
    }

    /**
     * Main entry point to the command line application.
     * 
     * @param args
     *            Not used.
     */
    public static void main(final String[] args) {

        try {

            new LogbackStandalone().init(args, new NewLogConfigFileParams("org.fuin.cqrs4j.example.javasecdi", "logback"));

            LOG.info("Started query example");

            try (final SeContainer container = SeContainerInitializer.newInstance().initialize()) {
                final QryExampleApp app = container.select(QryExampleApp.class).get();
                app.execute();
            }

            LOG.info("Query example is running until you kill it...");

        } catch (final RuntimeException ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }

    }

}
