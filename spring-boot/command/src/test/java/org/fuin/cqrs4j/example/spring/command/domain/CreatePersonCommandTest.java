package org.fuin.cqrs4j.example.spring.command.domain;

import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.cqrs4j.example.spring.shared.PersonName;
import org.fuin.cqrs4j.example.spring.shared.SharedConfig;
import org.fuin.objects4j.jackson.ImmutableObjectMapper;
import org.fuin.utils4j.Utils4J;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link CreatePersonCommand} class.
 */
class CreatePersonCommandTest {

    private static final String PERSON_UUID = "84565d62-115e-4502-b7c9-38ad69c64b05";

    private static ImmutableObjectMapper mapper;

    @BeforeAll
    static void beforeAll() {
        final SharedConfig factory = new SharedConfig();
        final ImmutableObjectMapper.Builder mapperBuilder = factory.immutableObjectMapperBuilder(factory.entityIdFactory());
        final ImmutableObjectMapper.Provider mapperProvider = factory.immutableObjectMapperProvider(mapperBuilder);
        mapper = mapperProvider.mapper();
    }

    @AfterAll
    static void afterAll() {
        mapper = null;
    }

    @Test
    void testSerializeDeserialize() {

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
    void testMarshalUnmarshalJson() throws Exception {

        // PREPARE
        final CreatePersonCommand original = createTestee();

        // TEST
        final String json = mapper.writer().writeValueAsString(original);
        final CreatePersonCommand copy = mapper.reader().readValue(json, CreatePersonCommand.class);

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getAggregateRootId()).isEqualTo(original.getAggregateRootId());
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    void testUnmarshalJsonFromFile() throws Exception {

        // PREPARE
        final String json = """
                {
                	"event-id": "109a77b2-1de2-46fc-aee1-97fa7740a552",
                	"event-timestamp": "2019-11-17T10:27:13.183+01:00[Europe/Berlin]",
                	"entity-id-path": "PERSON 84565d62-115e-4502-b7c9-38ad69c64b05",
                	"name": "Peter Parker"
                }
                """;

        // TEST
        final CreatePersonCommand copy = mapper.reader().readValue(json, CreatePersonCommand.class);

        // VERIFY
        assertThat(copy.getEventId().asBaseType()).isEqualTo(UUID.fromString("109a77b2-1de2-46fc-aee1-97fa7740a552"));
        assertThat(copy.getEventTimestamp()).isEqualTo(ZonedDateTime.parse("2019-11-17T10:27:13.183+01:00[Europe/Berlin]"));
        assertThat(copy.getAggregateRootId().asString()).isEqualTo(PERSON_UUID);
        assertThat(copy.getName().asString()).isEqualTo("Peter Parker");

    }

    private CreatePersonCommand createTestee() {
        final org.fuin.cqrs4j.example.spring.shared.PersonId personId = new PersonId(UUID.fromString(PERSON_UUID));
        final org.fuin.cqrs4j.example.spring.shared.PersonName personName = new PersonName("Peter Parker");
        return new CreatePersonCommand.Builder().id(personId).name(personName).build();
    }

}
