package org.fuin.cqrs4j.example.quarkus.query.app;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Represents the (custom) entry point, most likely used to the Quarkus application in the IDE.
 */
@QuarkusMain
public class QryApp {

    /**
     * Main method to start the app.
     *
     * @param args Arguments from the command line.
     */
    public static void main(String[] args) {
        Quarkus.run(args);
    }

}