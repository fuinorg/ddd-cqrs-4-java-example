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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.commons.io.IOUtils;
import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.utils4j.Utils4J;
import org.junit.Test;

// CHECKSTYLE:OFF
public final class CreatePersonCommandTest {
	
	private static final String PERSON_UUID = "84565d62-115e-4502-b7c9-38ad69c64b05";


	@Test
	public final void testSerializeDeserialize() {

		// PREPARE
		final CreatePersonCommand original = createTestee();

		// TEST
		final CreatePersonCommand copy = Utils4J.deserialize(Utils4J.serialize(original));

		// VERIFY
		assertThat(copy).isEqualTo(original);
		assertThat(copy.getAggregateRootId()).isEqualTo(original.getAggregateRootId());
		assertThat(copy.getName()).isEqualTo(original.getName());

	}

	@Test
	public final void testMarshalUnmarshalJson() {

		// PREPARE
		final CreatePersonCommand original = createTestee();

		final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.JSONB_ADAPTERS)
				.withPropertyVisibilityStrategy(new FieldAccessStrategy());
		final Jsonb jsonb = JsonbBuilder.create(config);

		// TEST
		final String json = jsonb.toJson(original, CreatePersonCommand.class);
		final CreatePersonCommand copy = jsonb.fromJson(json, CreatePersonCommand.class);

		// VERIFY
		assertThat(copy).isEqualTo(original);
		assertThat(copy.getAggregateRootId()).isEqualTo(original.getAggregateRootId());
		assertThat(copy.getName()).isEqualTo(original.getName());

	}

	@Test
	public final void testUnmarshalJsonFromFile() throws IOException {

		// PREPARE
        final String json = IOUtils.toString(this.getClass().getResourceAsStream("/commands/CreatePersonCommand.json"),
                Charset.forName("utf-8"));
		final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.JSONB_ADAPTERS)
				.withPropertyVisibilityStrategy(new FieldAccessStrategy());
		final Jsonb jsonb = JsonbBuilder.create(config);
		

		// TEST
		final CreatePersonCommand copy = jsonb.fromJson(json, CreatePersonCommand.class);

		// VERIFY
		assertThat(copy.getEventId().asBaseType()).isEqualTo(UUID.fromString("109a77b2-1de2-46fc-aee1-97fa7740a552"));
		assertThat(copy.getTimestamp()).isEqualTo(ZonedDateTime.parse("2019-11-17T10:27:13.183+01:00[Europe/Berlin]"));
		assertThat(copy.getAggregateRootId().asString()).isEqualTo(PERSON_UUID);
		assertThat(copy.getName().asString()).isEqualTo("Peter Parker");

	}

	private CreatePersonCommand createTestee() {
		final PersonId personId = new PersonId(UUID.fromString(PERSON_UUID));
		final PersonName personName = new PersonName("Peter Parker");
		return new CreatePersonCommand(personId, personName);
	}

}
// CHECKSTYLE:ON
