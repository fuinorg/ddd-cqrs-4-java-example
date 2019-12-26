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
package org.fuin.cqrs4j.example.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.fuin.ddd4j.ddd.EntityId;
import org.fuin.ddd4j.ddd.EntityIdFactory;

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
