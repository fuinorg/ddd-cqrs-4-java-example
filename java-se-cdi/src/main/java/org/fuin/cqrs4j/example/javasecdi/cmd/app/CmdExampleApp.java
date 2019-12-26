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
package org.fuin.cqrs4j.example.javasecdi.cmd.app;

import java.util.UUID;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;

import org.fuin.cqrs4j.example.javasecdi.cmd.domain.Person;
import org.fuin.cqrs4j.example.javasecdi.cmd.domain.PersonRepository;
import org.fuin.cqrs4j.example.javasecdi.cmd.domain.PersonRepositoryFactory;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.esc.api.EventStore;
import org.fuin.ext4logback.LogbackStandalone;
import org.fuin.ext4logback.NewLogConfigFileParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal example command (write) application.
 */
public class CmdExampleApp {

    private static final Logger LOG = LoggerFactory.getLogger(CmdExampleApp.class);
    
    @Inject
    private Instance<EventStore> eventStoreInstance;

    /**
     * Executes the application.
     */
    @ActivateRequestContext
    public void execute() {

        LOG.info("Executing...");
        
        try (final EventStore eventStore = eventStoreInstance.get()) {

            final PersonId id = new PersonId(UUID.fromString("f645969a-402d-41a9-882b-d2d8000d0f43"));
            final PersonName name = new PersonName("Peter Parker Inc.");
            final Person person = new Person(id, name, (dn) -> {
                return null;
            });
            final PersonRepository repo = new PersonRepositoryFactory().create(eventStore);

            repo.update(person);

            LOG.info("Updated event store...");
            
        } catch (final Exception ex) {
            throw new RuntimeException("Error saving person aggregate into event store", ex);
        }

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

            LOG.info("Start example");
            
            try (final SeContainer container = SeContainerInitializer.newInstance().initialize()) {
                final CmdExampleApp app = container.select(CmdExampleApp.class).get();
                app.execute();
            }

            LOG.info("Finished example");
            
            System.exit(0);
            
        } catch (final RuntimeException ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }

    }

}
