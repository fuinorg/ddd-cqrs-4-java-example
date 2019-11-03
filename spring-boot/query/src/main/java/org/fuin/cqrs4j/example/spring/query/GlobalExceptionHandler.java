package org.fuin.cqrs4j.example.spring.query;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Maps exceptions to a HTTP status code.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Converts a {@link ResourceNotFoundException} to a HTTP response.
	 *
	 * @param ex Exception to convert.
	 * @param request Current request.
	 * 
	 * @return Response. 
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> resourceNotFoundException(final ResourceNotFoundException ex, final WebRequest request) {
		final ErrorResponse errorResponse = new ErrorResponse(new Date(), HttpStatus.NOT_FOUND.toString(),
				ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	/**
	 * Converts a general {@link Exception} to a HTTP response.
	 *
	 * @param ex Exception to convert.
	 * @param request Current request.
	 * 
	 * @return Response. 
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> globleExcpetionHandler(final Exception ex, final WebRequest request) {
		final ErrorResponse errorResponse = new ErrorResponse(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
				ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
