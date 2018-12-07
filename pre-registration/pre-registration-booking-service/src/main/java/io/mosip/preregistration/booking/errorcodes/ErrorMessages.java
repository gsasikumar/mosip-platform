package io.mosip.preregistration.booking.errorcodes;

public enum ErrorMessages {
	BOOKING_TABLE_NOT_ACCESSIBLE, 
	REGISTRATION_TABLE_NOT_ACCESSIBLE, 
	USER_HAS_NOT_SELECTED_TIME_SLOT, 
	APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED, 
	PREREGISTRATION_ID_NOT_ENTERED, 
	REGISTRATION_CENTER_ID_NOT_ENTERED, 
	BOOKING_DATE_TIME_NOT_SELECTED, 
	INVALID_ID, 
	INVALID_VERSION, 
	INVALID_DATE_TIME_FORMAT, 
	APPOINTMENT_CANNOT_BE_BOOKED,
	AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME,
	APPOINTMENT_BOOKING_FAILED,
	TABLE_NOT_FOUND_EXCEPTION,
	DEMOGRAPHIC_STATUS_UPDATION_FAILED,
	DEMOGRAPHIC_GET_STATUS_FAILED
}
