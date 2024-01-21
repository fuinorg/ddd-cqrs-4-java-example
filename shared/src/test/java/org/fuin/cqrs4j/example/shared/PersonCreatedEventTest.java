package org.fuin.cqrs4j.example.shared;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.apache.commons.io.IOUtils;
import org.eclipse.yasson.FieldAccessStrategy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
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
        final PersonCreatedEvent original = createTestee();

        // TEST
        final PersonCreatedEvent copy = deserialize(serialize(original));

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    public final void testMarshalUnmarshalJson() throws Exception {

        // PREPARE
        final PersonCreatedEvent original = createTestee();

        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        try (final Jsonb jsonb = JsonbBuilder.create(config)) {

            // TEST
            final String json = jsonb.toJson(original, PersonCreatedEvent.class);
            final PersonCreatedEvent copy = jsonb.fromJson(json, PersonCreatedEvent.class);

            // VERIFY
            assertThat(copy).isEqualTo(original);
            assertThat(copy.getName()).isEqualTo(original.getName());

        }
    }

    @Test
    public final void testUnmarshalJson() throws Exception {

        // PREPARE
        final PersonCreatedEvent original = createTestee();
        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        try (final Jsonb jsonb = JsonbBuilder.create(config)) {

            // TEST
            final String json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/events/PersonCreatedEvent.json")),
                    StandardCharsets.UTF_8);
            final PersonCreatedEvent copy = jsonb.fromJson(json, PersonCreatedEvent.class);

            // VERIFY
            assertThat(copy.getEntityIdPath()).isEqualTo(original.getEntityIdPath());
            assertThat(copy.getName()).isEqualTo(original.getName());
        }

    }

    @Test
    public final void testToString() {
        final PersonCreatedEvent testee = createTestee();
        assertThat(testee)
                .hasToString("Person 'Peter Parker' (" + testee.getEntityId() + ") was created [Event " + testee.getEventId() + "]");
    }

    private PersonCreatedEvent createTestee() {
        final PersonId personId = new PersonId(UUID.fromString("f645969a-402d-41a9-882b-d2d8000d0f43"));
        final PersonName personName = new PersonName("Peter Parker");
        return new PersonCreatedEvent.Builder().id(personId).name(personName).version(0).build();
    }

}
