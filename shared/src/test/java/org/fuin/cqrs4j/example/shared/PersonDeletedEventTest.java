package org.fuin.cqrs4j.example.shared;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.apache.commons.io.IOUtils;
import org.eclipse.yasson.FieldAccessStrategy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fuin.utils4j.Utils4J.deserialize;
import static org.fuin.utils4j.Utils4J.serialize;

// CHECKSTYLE:OFF
public final class PersonDeletedEventTest {

    @Test
    public final void testSerializeDeserialize() {

        // PREPARE
        final PersonDeletedEvent original = createTestee();

        // TEST
        final PersonDeletedEvent copy = deserialize(serialize(original));

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    public final void testMarshalUnmarshalJson() {

        // PREPARE
        final PersonDeletedEvent original = createTestee();

        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.JSONB_ADAPTERS)
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        final Jsonb jsonb = JsonbBuilder.create(config);

        // TEST
        final String json = jsonb.toJson(original, PersonDeletedEvent.class);
        final PersonDeletedEvent copy = jsonb.fromJson(json, PersonDeletedEvent.class);

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getName()).isEqualTo(original.getName());
        assertThat(copy.getAggregateVersionInteger()).isEqualTo(1L);

    }

    @Test
    public final void testUnmarshalJson() throws IOException {

        // PREPARE
        final PersonDeletedEvent original = createTestee();
        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.JSONB_ADAPTERS)
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        final Jsonb jsonb = JsonbBuilder.create(config);

        // TEST
        final String json = IOUtils.toString(this.getClass().getResourceAsStream("/events/PersonDeletedEvent.json"),
                Charset.forName("utf-8"));
        final PersonDeletedEvent copy = jsonb.fromJson(json, PersonDeletedEvent.class);

        // VERIFY
        assertThat(copy.getEntityIdPath()).isEqualTo(original.getEntityIdPath());
        assertThat(copy.getName()).isEqualTo(original.getName());
        assertThat(copy.getAggregateVersionInteger()).isEqualTo(1L);

    }

    @Test
    public final void testToString() {
        final PersonDeletedEvent testee = createTestee();
        assertThat(testee)
                .hasToString("Deleted person 'Peter Parker' (" + testee.getEntityId() + ") [Event " + testee.getEventId() + "]");
    }

    private PersonDeletedEvent createTestee() {
        final PersonId personId = new PersonId(UUID.fromString("f645969a-402d-41a9-882b-d2d8000d0f43"));
        final PersonName personName = new PersonName("Peter Parker");
        return new PersonDeletedEvent.Builder().id(personId).name(personName).version(1).build();
    }

}
