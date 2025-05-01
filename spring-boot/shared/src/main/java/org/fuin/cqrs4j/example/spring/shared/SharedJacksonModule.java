package org.fuin.cqrs4j.example.spring.shared;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.fuin.ddd4j.core.EntityIdFactory;
import org.fuin.ddd4j.jackson.EntityIdJacksonDeserializer;
import org.fuin.ddd4j.jackson.EntityIdJacksonSerializer;
import org.fuin.objects4j.jackson.ValueObjectStringJacksonDeserializer;
import org.fuin.objects4j.jackson.ValueObjectStringJacksonSerializer;
import org.fuin.utils4j.TestOmitted;

import java.util.List;
import java.util.Objects;

/**
 * Module that registers the adapters for the package.
 */
@TestOmitted("Tested with other tests")
public class SharedJacksonModule extends Module {

    private final EntityIdFactory entityIdFactory;

    /**
     * Constructor with entity ID factory.
     *
     * @param entityIdFactory Factory to use.
     */
    public SharedJacksonModule(EntityIdFactory entityIdFactory) {
        this.entityIdFactory = Objects.requireNonNull(entityIdFactory, "entityIdFactory==null");
    }

    @Override
    public String getModuleName() {
        return "Cqrs4JModule";
    }

    @Override
    public Iterable<? extends Module> getDependencies() {
        return List.of(new JavaTimeModule());
    }

    @Override
    public void setupModule(SetupContext context) {

        final SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(new EntityIdJacksonSerializer<>(PersonId.class));
        serializers.addSerializer(new ValueObjectStringJacksonSerializer<>(PersonName.class));
        context.addSerializers(serializers);

        final SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(PersonId.class, new EntityIdJacksonDeserializer<>(PersonId.class, entityIdFactory));
        deserializers.addDeserializer(PersonName.class, new ValueObjectStringJacksonDeserializer<>(PersonName.class, PersonName::new));
        context.addDeserializers(deserializers);

    }

    @Override
    public Version version() {
        return new Version(0, 5, 0, "SNAPSHOT",
                "org.fuin.cqrs4j.example.spring", "cqrs4j-spring-example-shared");
    }

}