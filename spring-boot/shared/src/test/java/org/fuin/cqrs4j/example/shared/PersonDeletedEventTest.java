package org.fuin.cqrs4j.example.shared;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.apache.commons.io.IOUtils;
import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent;
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
public final class PersonDeletedEventTest {

    @Test
    public final void testSerializeDeserialize() {

        // PREPARE
        final org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent original = createTestee();

        // TEST
        final org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent copy = deserialize(serialize(original));

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    public final void testMarshalUnmarshalJson() throws Exception {

        // PREPARE
        final org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent original = createTestee();

        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        try (final Jsonb jsonb = JsonbBuilder.create(config)) {

            // TEST
            final String json = jsonb.toJson(original, org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent.class);
            final org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent copy = jsonb.fromJson(json, org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent.class);

            // VERIFY
            assertThat(copy).isEqualTo(original);
            assertThat(copy.getName()).isEqualTo(original.getName());
            assertThat(copy.getAggregateVersionInteger()).isEqualTo(1L);

        }
    }

    @Test
    public final void testUnmarshalJson() throws Exception {

        // PREPARE
        final org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent original = createTestee();
        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        try (final Jsonb jsonb = JsonbBuilder.create(config)) {

            // TEST
            final String json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/events/PersonDeletedEvent.json")),
                    StandardCharsets.UTF_8);
            final org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent copy = jsonb.fromJson(json, org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent.class);

            // VERIFY
            assertThat(copy.getEntityIdPath()).isEqualTo(original.getEntityIdPath());
            assertThat(copy.getName()).isEqualTo(original.getName());
            assertThat(copy.getAggregateVersionInteger()).isEqualTo(1L);

        }

    }

    @Test
    public final void testToString() {
        final org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent testee = createTestee();
        assertThat(testee)
                .hasToString("Deleted person 'Peter Parker' (" + testee.getEntityId() + ") [Event " + testee.getEventId() + "]");
    }

    private org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent createTestee() {
        final org.fuin.cqrs4j.example.spring.shared.PersonId personId = new PersonId(UUID.fromString("f645969a-402d-41a9-882b-d2d8000d0f43"));
        final org.fuin.cqrs4j.example.spring.shared.PersonName personName = new PersonName("Peter Parker");
        return new PersonDeletedEvent.Builder().id(personId).name(personName).version(1).build();
    }

}
