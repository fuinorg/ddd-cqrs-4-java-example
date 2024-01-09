package org.fuin.cqrs4j.example.shared;

import org.fuin.ddd4j.ddd.EntityId;
import org.fuin.ddd4j.ddd.EntityIdFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory that creates entity identifier instances based on the type.
 */
public final class SharedEntityIdFactory implements EntityIdFactory {

    private Map<String, Function<String, EntityId>> valueOfMap;

    private Map<String, Function<String, Boolean>> isValidMap;

    /**
     * Default constructor.
     */
    public SharedEntityIdFactory() {
        super();
        valueOfMap = new HashMap<>();
        isValidMap = new HashMap<>();
        valueOfMap.put(PersonId.TYPE.asString(), PersonId::valueOf);
        isValidMap.put(PersonId.TYPE.asString(), PersonId::isValid);
    }

    @Override
    public EntityId createEntityId(final String type, final String id) {
        final Function<String, EntityId> factory = valueOfMap.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
        return factory.apply(id);
    }

    @Override
    public boolean containsType(final String type) {
        return valueOfMap.containsKey(type);
    }

    @Override
    public boolean isValid(String type, String id) {
        final Function<String, Boolean> func = isValidMap.get(type);
        if (func == null) {
            return false;
        }
        return func.apply(id);
    }

}
