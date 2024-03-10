package org.fuin.cqrs4j.example.shared;

import org.fuin.ddd4j.core.EventType;
import org.fuin.esc.client.JandexSerializedDataTypeRegistry;
import org.fuin.esc.jsonb.Base64Data;
import org.fuin.esc.jsonb.EscEvent;
import org.fuin.esc.jsonb.EscEvents;
import org.fuin.esc.jsonb.EscMeta;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for the {@link SharedUtils} class.
 */
public final class SharedUtilsTest {

    @Test
    public void testCalculateChecksum() {

        // PREPARE
        final Set<EventType> eventTypes = new HashSet<>();
        eventTypes.add(new EventType("PersonCreatedEvent"));
        eventTypes.add(new EventType("PersonRenamedEvent"));
        eventTypes.add(new EventType("PersonDeletedEvent"));

        // TEST
        final long checksum = SharedUtils.calculateChecksum(eventTypes);

        // VERIFY
        assertThat(checksum).isEqualTo(1341789591L);

    }

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
