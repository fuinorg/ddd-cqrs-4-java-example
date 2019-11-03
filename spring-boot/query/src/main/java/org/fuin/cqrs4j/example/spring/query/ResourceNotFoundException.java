package org.fuin.cqrs4j.example.spring.query;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A resource was not found.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with message.
	 *
	 * @param message Exception message.
	 */
	public ResourceNotFoundException(final String message) {
		super(message);
	}

}