package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class RestCallException extends BaseUncheckedException  {

	private static final long serialVersionUID = 1L;
	
	public RestCallException(String msg) {
		super("", msg);
	}
	public RestCallException(String msg, Throwable cause) {
		super("", msg, cause);
	}
}
