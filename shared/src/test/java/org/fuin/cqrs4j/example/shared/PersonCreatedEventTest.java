/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fuin.utils4j.Utils4J.deserialize;
import static org.fuin.utils4j.Utils4J.serialize;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.commons.io.IOUtils;
import org.eclipse.yasson.FieldAccessStrategy;
import org.junit.Test;


// CHECKSTYLE:OFF
public final class PersonCreatedEventTest {

    @Test
    public final void testSerializeDeserialize() {

        // PREPARE
        final PersonCreatedEvent original = createTestee();

        // TEST
        final PersonCreatedEvent copy = deserialize(serialize(original));

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    public final void testMarshalUnmarshalJson() {

        // PREPARE
        final PersonCreatedEvent original = createTestee();

        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.JSONB_ADAPTERS)
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        final Jsonb jsonb = JsonbBuilder.create(config);

        // TEST
        final String json = jsonb.toJson(original, PersonCreatedEvent.class);
        final PersonCreatedEvent copy = jsonb.fromJson(json, PersonCreatedEvent.class);

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getName()).isEqualTo(original.getName());

    }
    
    @Test
    public final void testUnmarshalJson() throws IOException {

        // PREPARE
        final PersonCreatedEvent original = createTestee();
        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.JSONB_ADAPTERS)
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        final Jsonb jsonb = JsonbBuilder.create(config);

        // TEST
        final String json = IOUtils.toString(this.getClass().getResourceAsStream("/events/PersonCreatedEvent.json"),
                Charset.forName("utf-8"));
        final PersonCreatedEvent copy = jsonb.fromJson(json, PersonCreatedEvent.class);

        // VERIFY
        assertThat(copy.getEntityIdPath()).isEqualTo(original.getEntityIdPath());
        assertThat(copy.getName()).isEqualTo(original.getName());

    }


    @Test
    public final void testToString() {
        final PersonCreatedEvent testee = createTestee();
        assertThat(testee.toString())
                .isEqualTo("Person 'Peter Parker' (" + testee.getEntityId() + ") was created [Event " + testee.getEventId() + "]");
    }

    private PersonCreatedEvent createTestee() {
        final PersonId personId = new PersonId(UUID.fromString("f645969a-402d-41a9-882b-d2d8000d0f43"));
        final PersonName personName = new PersonName("Peter Parker");
        return new PersonCreatedEvent(personId, personName);
    }

}
