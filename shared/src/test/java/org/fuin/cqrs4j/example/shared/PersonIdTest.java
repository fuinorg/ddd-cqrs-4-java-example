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
import static org.junit.Assert.fail;

import java.util.UUID;

import org.fuin.ddd4j.ddd.EntityType;
import org.fuin.ddd4j.ddd.StringBasedEntityType;
import org.fuin.objects4j.common.ConstraintViolationException;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

/**
 * Test for {@link PersonId}.
 */
public final class PersonIdTest {

	private static final String PERSON_UUID = "84565d62-115e-4502-b7c9-38ad69c64b05";

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(PersonId.class).suppress(Warning.NONFINAL_FIELDS)
				.withNonnullFields("entityType", "uuid")
				.withPrefabValues(EntityType.class, new StringBasedEntityType("A"), new StringBasedEntityType("B"))
				.verify();
	}

	@Test
	public void testValueOf() {
		final PersonId personId = PersonId.valueOf(PERSON_UUID);

		assertThat(personId.asString()).isEqualTo(PERSON_UUID);

	}

	@Test
	public void testValueOfIllegalArgumentCharacter() {
		try {
			PersonId.valueOf("abc");
			fail();
		} catch (final ConstraintViolationException ex) {
			assertThat(ex.getMessage()).isEqualTo("The argument 'value' is not valid: 'abc'");
		}
	}

	@Test
	public final void testConverterUnmarshal() throws Exception {

		// PREPARE
		final String personIdValue = PERSON_UUID;

		// TEST
		final PersonId personId = new PersonId.Converter().adaptFromJson(UUID.fromString(PERSON_UUID));

		// VERIFY
		assertThat(personId.asString()).isEqualTo(personIdValue);
	}

	@Test
	public void testConverterMarshal() throws Exception {

		final PersonId personId = PersonId.valueOf(PERSON_UUID);

		// TEST
		final UUID uuid = new PersonId.Converter().adaptToJson(personId);

		// VERIFY
		assertThat(uuid).isEqualTo(UUID.fromString(PERSON_UUID));
	}

}
