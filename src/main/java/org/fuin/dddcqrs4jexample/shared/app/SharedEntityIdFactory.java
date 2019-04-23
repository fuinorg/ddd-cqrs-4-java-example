/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see http://www.gnu.org/licenses/.
 */
package org.fuin.dddcqrs4jexample.shared.app;

import java.util.HashMap;
import java.util.Map;

import org.fuin.ddd4j.ddd.EntityId;
import org.fuin.ddd4j.ddd.EntityIdFactory;
import org.fuin.ddd4j.ddd.SingleEntityIdFactory;
import org.fuin.dddcqrs4jexample.shared.domain.PersonId;

/**
 * Factory that creates entity identifier instances based on the type.
 */
public final class SharedEntityIdFactory implements EntityIdFactory {

    private Map<String, SingleEntityIdFactory> map;

    /**
     * Default constructor.
     */
    public SharedEntityIdFactory() {
        super();
        map = new HashMap<String, SingleEntityIdFactory>();
        map.put(PersonId.TYPE.asString(), PersonId::valueOf);
    }

    @Override
    public EntityId createEntityId(final String type, final String id) {
        final SingleEntityIdFactory factory = map.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
        return factory.createEntityId(id);
    }

    @Override
    public boolean containsType(final String type) {
        return map.containsKey(type);
    }

}
