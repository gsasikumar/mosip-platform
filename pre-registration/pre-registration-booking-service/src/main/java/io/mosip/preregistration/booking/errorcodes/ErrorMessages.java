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
	NO_SLOTS_AVAILABLE_FOR_THAT_DATE,
	NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER,
	DEMOGRAPHIC_GET_STATUS_FAILED,
	AVAILABILITY_TABLE_NOT_ACCESSABLE,
	BOOKING_DATA_NOT_FOUND,
	APPOINTMENT_CANNOT_BE_CANCELED,
	APPOINTMENT_TIME_SLOT_IS_ALREADY_CANCELED,
	APPOINTMENT_CANCEL_FAILED,
	APPOINTMENT_REBOOKING_FAILED,
	MASTER_DATA_NOT_FOUND,
	INVALID_REQUEST_PARAMETER;
}
