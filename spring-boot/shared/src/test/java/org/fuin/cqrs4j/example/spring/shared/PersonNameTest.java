package org.fuin.cqrs4j.example.spring.shared;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.fuin.objects4j.common.ConstraintViolationException;
import org.fuin.objects4j.jackson.ImmutableObjectMapper;
import org.fuin.utils4j.Utils4J;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test for {@link PersonName} class.
 */
class PersonNameTest {

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
    void testSerialize() {
        final PersonName original = new PersonName("Peter Parker");
        final PersonName copy = Utils4J.deserialize(Utils4J.serialize(original));
        assertThat(original).isEqualTo(copy);
    }

    @Test
    void testHashCodeEquals() {
        EqualsVerifier.forClass(PersonName.class).suppress(Warning.NULL_FIELDS).withRedefinedSuperclass().verify();
    }

    @Test
    final void testUnmarshal() throws Exception {

        // PREPARE
        final String json = """
                { "name" : "Peter Parker" }
                """;

        // TEST
        final Person copy = mapper.reader().readValue(json, Person.class);

        // VERIFY
        assertThat(copy.name()).isEqualTo(new PersonName("Peter Parker"));
    }

    @Test
    void testMarshal() throws Exception {

        final Person original = new Person(new PersonName("Peter Parker"));

        // TEST
        final String json = mapper.writer().writeValueAsString(original);

        // VERIFY
        final Person copy = mapper.reader().readValue(json, Person.class);
        assertThat(copy.name()).isEqualTo(original.name());
    }

    @Test
    void testIsValid() {

        assertThat(PersonName.isValid(null)).isTrue();
        assertThat(PersonName.isValid("Peter Parker")).isTrue();

        assertThat(PersonName.isValid("")).isFalse();
        assertThat(PersonName.isValid(
                "123456789.123456789.123456789.123456789.123456789." + "123456789.123456789.123456789.123456789.123456789." + "12345"))
                .isFalse();

    }

    @Test
    void testRequireArgValid() {

        PersonName.requireArgValid("a", "Peter Parker");
        PersonName.requireArgValid("b", null);

        try {
            PersonName.requireArgValid("c", "");
            fail();
        } catch (final ConstraintViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo("The argument 'c' is not valid: ''");
        }

        try {
            PersonName.requireArgValid("d",
                    "123456789.123456789.123456789.123456789.123456789." + "123456789.123456789.123456789.123456789.123456789." + "12345");
            fail();
        } catch (final ConstraintViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo("The argument 'd' is not valid: '" + "123456789.123456789.123456789.123456789.123456789."
                    + "123456789.123456789.123456789.123456789.123456789." + "12345" + "'");
        }

    }

    @Test
    void testValidator() {

        assertThat(new PersonName.Validator().isValid(null, null)).isTrue();
        assertThat(new PersonName.Validator().isValid("Peter Parker", null)).isTrue();

        assertThat(new PersonName.Validator().isValid("", null)).isFalse();
        assertThat(new PersonName.Validator().isValid(
                "123456789.123456789.123456789.123456789.123456789." + "123456789.123456789.123456789.123456789.123456789." + "12345",
                null)).isFalse();

    }

    public record Person(PersonName name) {
    }
    
}
