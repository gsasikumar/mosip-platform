package io.mosip.preregistration.booking.dto;


import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelBookingDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String preRegistrationId;
	/**
	 * registration Center Id
	 */
	private String registrationCenterId;
	/**
	 * booked Date Time
	 */
	private String regDate;
	/**
	 * booked Time Slot
	 */
	@JsonProperty("time_slot_from")
	private String slotFromTime;
	
	@JsonProperty("time_slot_to")
	private String slotToTime;
}
