package org.fuin.cqrs4j.example.shared;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.apache.commons.io.IOUtils;
import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.utils4j.Utils4J;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// CHECKSTYLE:OFF
public final class CreatePersonCommandTest {

    private static final String PERSON_UUID = "84565d62-115e-4502-b7c9-38ad69c64b05";

    @Test
    public void testSerializeDeserialize() {

        // PREPARE
        final CreatePersonCommand original = createTestee();

        // TEST
        final CreatePersonCommand copy = Utils4J.deserialize(Utils4J.serialize(original));

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getAggregateRootId()).isEqualTo(original.getAggregateRootId());
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    public void testMarshalUnmarshalJson() throws Exception {

        // PREPARE
        final CreatePersonCommand original = createTestee();

        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        try (final Jsonb jsonb = JsonbBuilder.create(config)) {

            // TEST
            final String json = jsonb.toJson(original, CreatePersonCommand.class);
            final CreatePersonCommand copy = jsonb.fromJson(json, CreatePersonCommand.class);

            // VERIFY
            assertThat(copy).isEqualTo(original);
            assertThat(copy.getAggregateRootId()).isEqualTo(original.getAggregateRootId());
            assertThat(copy.getName()).isEqualTo(original.getName());

        }

    }

    @Test
    public void testUnmarshalJsonFromFile() throws Exception {

        // PREPARE
        final String json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/commands/CreatePersonCommand.json")),
                StandardCharsets.UTF_8);
        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        try (final Jsonb jsonb = JsonbBuilder.create(config)) {

            // TEST
            final CreatePersonCommand copy = jsonb.fromJson(json, CreatePersonCommand.class);

            // VERIFY
            assertThat(copy.getEventId().asBaseType()).isEqualTo(UUID.fromString("109a77b2-1de2-46fc-aee1-97fa7740a552"));
            assertThat(copy.getEventTimestamp()).isEqualTo(ZonedDateTime.parse("2019-11-17T10:27:13.183+01:00[Europe/Berlin]"));
            assertThat(copy.getAggregateRootId().asString()).isEqualTo(PERSON_UUID);
            assertThat(copy.getName().asString()).isEqualTo("Peter Parker");

        }

    }

    private CreatePersonCommand createTestee() {
        final PersonId personId = new PersonId(UUID.fromString(PERSON_UUID));
        final PersonName personName = new PersonName("Peter Parker");
        return new CreatePersonCommand.Builder().id(personId).name(personName).build();
    }

}
// CHECKSTYLE:ON
