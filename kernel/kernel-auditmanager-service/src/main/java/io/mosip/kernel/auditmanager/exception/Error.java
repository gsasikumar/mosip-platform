package io.mosip.kernel.auditmanager.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error item bean class having error code and error message
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {

	/**
	 * The error code field
	 */
	private String errorCode;

	/**
	 * The error message field
	 */
	private String errorMessage;

}