package org.fuin.cqrs4j.example.spring.query;

import java.util.Date;

/**
 * Error message for HTTP response.
 */
public class ErrorResponse {

	private Date timestamp;

	private String status;

	private String message;

	private String details;

	/**
	 * Constructor with all data.
	 *
	 * @param timestamp Timestamp.
	 * @param status    Status.
	 * @param message   Message.
	 * @param details   Details.
	 */
	public ErrorResponse(final Date timestamp, final String status, final String message, final String details) {
		super();
		this.timestamp = timestamp;
		this.status = status;
		this.message = message;
		this.details = details;
	}

	/**
	 * Returns the timestamp.
	 *
	 * @return Timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the status.
	 *
	 * @return Status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Returns the message.
	 *
	 * @return Message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the details.
	 *
	 * @return Details.
	 */
	public String getDetails() {
		return details;
	}

}
