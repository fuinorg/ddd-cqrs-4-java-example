package org.fuin.cqrs4j.example.spring.shared;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.fuin.ddd4j.core.EntityType;
import org.fuin.ddd4j.core.StringBasedEntityType;
import org.fuin.objects4j.common.ConstraintViolationException;
import org.fuin.objects4j.jackson.ImmutableObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test for {@link PersonId} class.
 */
class PersonIdTest {

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
    void testEquals() {
        EqualsVerifier.forClass(PersonId.class).suppress(Warning.NONFINAL_FIELDS).withNonnullFields("entityType", "uuid")
                .withPrefabValues(EntityType.class, new StringBasedEntityType("A"), new StringBasedEntityType("B")).verify();
    }

    @Test
    void testValueOf() {
        final PersonId personId = PersonId.valueOf(PERSON_UUID);

        assertThat(personId.asString()).isEqualTo(PERSON_UUID);

    }

    @Test
    void testValueOfIllegalArgumentCharacter() {
        try {
            PersonId.valueOf("abc");
            fail();
        } catch (final ConstraintViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo("The argument 'value' is not valid: 'abc'");
        }
    }

    @Test
    final void testUnmarshal() throws Exception {

        // PREPARE
        final String json = """
                { "id" : "PERSON 84565d62-115e-4502-b7c9-38ad69c64b05" }
                """;

        // TEST
        final Person copy = mapper.reader().readValue(json, Person.class);

        // VERIFY
        assertThat(copy.id()).isEqualTo(PersonId.valueOf(PERSON_UUID));
    }

    @Test
    void testMarshal() throws Exception {

        final Person original = new Person(PersonId.valueOf(PERSON_UUID));

        // TEST
        final String json = mapper.writer().writeValueAsString(original);

        // VERIFY
        final Person copy = mapper.reader().readValue(json, Person.class);
        assertThat(copy.id()).isEqualTo(original.id());
    }

    public record Person(PersonId id) {
    }

}
