package org.fuin.cqrs4j.example.shared;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.apache.commons.io.IOUtils;
import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.cqrs4j.example.spring.shared.PersonName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fuin.utils4j.Utils4J.deserialize;
import static org.fuin.utils4j.Utils4J.serialize;

// CHECKSTYLE:OFF
public final class PersonCreatedEventTest {

    @Test
    public final void testSerializeDeserialize() {

        // PREPARE
        final org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent original = createTestee();

        // TEST
        final org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent copy = deserialize(serialize(original));

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    public final void testMarshalUnmarshalJson() throws Exception {

        // PREPARE
        final org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent original = createTestee();

        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        try (final Jsonb jsonb = JsonbBuilder.create(config)) {

            // TEST
            final String json = jsonb.toJson(original, org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent.class);
            final org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent copy = jsonb.fromJson(json, org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent.class);

            // VERIFY
            assertThat(copy).isEqualTo(original);
            assertThat(copy.getName()).isEqualTo(original.getName());

        }
    }

    @Test
    public final void testUnmarshalJson() throws Exception {

        // PREPARE
        final org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent original = createTestee();
        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        try (final Jsonb jsonb = JsonbBuilder.create(config)) {

            // TEST
            final String json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/events/PersonCreatedEvent.json")),
                    StandardCharsets.UTF_8);
            final org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent copy = jsonb.fromJson(json, org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent.class);

            // VERIFY
            assertThat(copy.getEntityIdPath()).isEqualTo(original.getEntityIdPath());
            assertThat(copy.getName()).isEqualTo(original.getName());
        }

    }

    @Test
    public final void testToString() {
        final org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent testee = createTestee();
        assertThat(testee)
                .hasToString("Person 'Peter Parker' (" + testee.getEntityId() + ") was created [Event " + testee.getEventId() + "]");
    }

    private org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent createTestee() {
        final org.fuin.cqrs4j.example.spring.shared.PersonId personId = new PersonId(UUID.fromString("f645969a-402d-41a9-882b-d2d8000d0f43"));
        final org.fuin.cqrs4j.example.spring.shared.PersonName personName = new PersonName("Peter Parker");
        return new PersonCreatedEvent.Builder().id(personId).name(personName).version(0).build();
    }

}
