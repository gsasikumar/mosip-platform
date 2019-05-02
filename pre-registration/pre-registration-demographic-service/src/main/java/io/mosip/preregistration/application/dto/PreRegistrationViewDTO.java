/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.dto;

import java.io.Serializable;

import org.json.simple.JSONArray;

import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * View registration response DTO
 * 
 * @author Rupika
 * @since 1.0.0
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PreRegistrationViewDTO implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2770204280374548395L;

	/**
	 * PreRegistration Id
	 */
	private String preRegistrationId;
	/**
	 * Full Name
	 */
	private JSONArray fullname;
	/**
	 * Status code
	 */
	private String statusCode;
	/**
	 * BookingRegistrationDTO object
	 */
	private BookingRegistrationDTO bookingRegistrationDTO;
	/**
	 * postalCode
	 */
	private String postalCode;
}
