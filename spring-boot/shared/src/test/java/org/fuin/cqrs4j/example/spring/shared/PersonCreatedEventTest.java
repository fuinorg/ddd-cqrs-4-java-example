package org.fuin.cqrs4j.example.spring.shared;

import org.fuin.objects4j.jackson.ImmutableObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fuin.utils4j.Utils4J.deserialize;
import static org.fuin.utils4j.Utils4J.serialize;

// CHECKSTYLE:OFF
class PersonCreatedEventTest {

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
        final PersonCreatedEvent original = createTestee();

        // TEST
        final PersonCreatedEvent copy = deserialize(serialize(original));

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    void testMarshalUnmarshalJson() throws Exception {

        // PREPARE
        final PersonCreatedEvent original = createTestee();

        // TEST
        final String json = mapper.writer().writeValueAsString(original);
        final PersonCreatedEvent copy = mapper.reader().readValue(json, PersonCreatedEvent.class);

        // VERIFY
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    void testUnmarshalJson() throws Exception {

        // PREPARE
        final PersonCreatedEvent original = createTestee();

        // TEST
        final String json = """
                {
                	"event-id": "a7b88543-ce32-40eb-a3fe-f49aec39b570",
                	"event-timestamp": "2019-11-02T09:56:40.669Z[Etc/UTC]",
                	"entity-id-path": "PERSON f645969a-402d-41a9-882b-d2d8000d0f43",
                	"aggregate-version": 0,
                	"name": "Peter Parker"
                }                
                """;
        final PersonCreatedEvent copy = mapper.reader().readValue(json, PersonCreatedEvent.class);

        // VERIFY
        assertThat(copy.getEntityIdPath()).isEqualTo(original.getEntityIdPath());
        assertThat(copy.getName()).isEqualTo(original.getName());

    }

    @Test
    void testToString() {
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
