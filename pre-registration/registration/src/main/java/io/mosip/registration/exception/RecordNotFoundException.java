package io.mosip.registration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class RecordNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public RecordNotFoundException(String errorCodes) {
		super(errorCodes,errorCodes);
	}
}
