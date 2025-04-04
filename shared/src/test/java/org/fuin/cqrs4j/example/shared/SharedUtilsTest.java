package org.fuin.cqrs4j.example.shared;

import org.fuin.esc.client.JandexSerializedDataTypeRegistry;
import org.fuin.esc.jsonb.Base64Data;
import org.fuin.esc.jsonb.EscEvent;
import org.fuin.esc.jsonb.EscEvents;
import org.fuin.esc.jsonb.EscMeta;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for the {@link SharedUtils} class.
 */
public final class SharedUtilsTest {

    @Test
    public void testCreate() {
        final JandexSerializedDataTypeRegistry testee = new JandexSerializedDataTypeRegistry();
        assertThat(testee.getClasses()).containsOnly(
                EscEvent.class,
                EscEvents.class,
                EscMeta.class,
                Base64Data.class,
                PersonCreatedEvent.class,
                PersonDeletedEvent.class
        );
    }

}
