package org.fuin.cqrs4j.example.quarkus.shared;

import jakarta.json.bind.adapter.JsonbAdapter;
import org.fuin.ddd4j.core.EntityIdFactory;
import org.fuin.ddd4j.core.EventType;
import org.fuin.ddd4j.core.JandexEntityIdFactory;
import org.fuin.ddd4j.jsonb.AggregateVersionJsonbAdapter;
import org.fuin.ddd4j.jsonb.EntityIdJsonbAdapter;
import org.fuin.ddd4j.jsonb.EntityIdPathJsonbAdapter;
import org.fuin.ddd4j.jsonb.EventIdJsonbAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.zip.Adler32;

/**
 * Utility code shared between command (write) and query (read) module.
 */
public final class SharedUtils {

    /** All JSON-B adapters from this module. */
    private static List<JsonbAdapter<?, ?>> JSONB_ADAPTERS;

    private SharedUtils() {
        throw new UnsupportedOperationException("It is not allowed to create an instance of a utiliy class");
    }

    /**
     * Returns all JSON-B adapter used to (de)serialize the domain value objects and events. Used by the
     * unit tests to build a stand-alone JSON-B instance; the application wires the same adapters via
     * {@code SerDeserializerRegistryFactory} / {@code JandexJsonbRegistry}.
     *
     * @return JSON-B adapter list.
     */
    public static synchronized JsonbAdapter<?, ?>[] getJsonbAdapters() {
        if (JSONB_ADAPTERS == null) {
            final EntityIdFactory entityIdFactory = new JandexEntityIdFactory(); // Scans classes
            JSONB_ADAPTERS = List.of(
                    new EventIdJsonbAdapter(),
                    new EntityIdPathJsonbAdapter(entityIdFactory),
                    new EntityIdJsonbAdapter(entityIdFactory),
                    new AggregateVersionJsonbAdapter(),
                    new PersonId.Converter(),
                    new PersonName.Adapter()
            );
        }
        return JSONB_ADAPTERS.toArray(new JsonbAdapter<?, ?>[0]);
    }

    /**
     * Creates an Adler32 checksum based on event type names.
     *
     * @param eventTypes
     *            Types to calculate a checksum for.
     *
     * @return Checksum based on all names.
     */
    public static long calculateChecksum(final Collection<EventType> eventTypes) {
        final Adler32 checksum = new Adler32();
        for (final EventType eventType : eventTypes) {
            checksum.update(eventType.asBaseType().getBytes(StandardCharsets.US_ASCII));
        }
        return checksum.getValue();
    }

}
