/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library. If not, see
 * http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.quarkus.shared;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.fuin.cqrs4j.example.shared.SharedUtils;
import org.fuin.esc.spi.JsonbDeSerializer;
import org.fuin.esc.spi.SerDeserializerRegistry;
import org.fuin.esc.spi.SerializedDataTypeRegistry;

/**
 * CDI bean that creates a {@link SerDeserializerRegistry}.
 */
@ApplicationScoped
public class SerDeserializerRegistryFactory {

    @Produces
    @ApplicationScoped
    public SerDeserializerRegistry createRegistry() {

        // Knows about all types for usage with JSON-B
        final SerializedDataTypeRegistry typeRegistry = SharedUtils.createTypeRegistry();

        // Does the actual marshalling/unmarshalling
        final JsonbDeSerializer jsonbDeSer = SharedUtils.createJsonbDeSerializer();

        // Registry connects the type with the appropriate serializer and de-serializer
        return SharedUtils.createSerDeserializerRegistry(typeRegistry,
                jsonbDeSer);

    }

}