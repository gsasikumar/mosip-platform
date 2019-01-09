package io.mosip.preregistration.booking.errorcodes;

/**
 * Error messages
 * @author M1044479
 *
 */
public enum ErrorMessages {
	BOOKING_TABLE_NOT_ACCESSIBLE("BOOKING_TABLE_NOT_ACCESSIBLE"), 
	REGISTRATION_TABLE_NOT_ACCESSIBLE("REGISTRATION_TABLE_NOT_ACCESSIBLE"), 
	USER_HAS_NOT_SELECTED_TIME_SLOT("USER_HAS_NOT_SELECTED_TIME_SLOT"), 
	APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED("APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED"), 
	PREREGISTRATION_ID_NOT_ENTERED("PREREGISTRATION_ID_NOT_ENTERED"), 
	REGISTRATION_CENTER_ID_NOT_ENTERED("REGISTRATION_CENTER_ID_NOT_ENTERED"), 
	BOOKING_DATE_TIME_NOT_SELECTED("BOOKING_DATE_TIME_NOT_SELECTED"), 
	INVALID_ID("INVALID_ID"), 
	INVALID_VERSION("INVALID_VERSION"), 
	INVALID_DATE_TIME_FORMAT("INVALID_DATE_TIME_FORMAT"), 
	APPOINTMENT_CANNOT_BE_BOOKED("APPOINTMENT_CANNOT_BE_BOOKED"),
	AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME("AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME"),
	APPOINTMENT_BOOKING_FAILED("APPOINTMENT_BOOKING_FAILED"),
	TABLE_NOT_FOUND_EXCEPTION("TABLE_NOT_FOUND_EXCEPTION"),
	DEMOGRAPHIC_STATUS_UPDATION_FAILED("DEMOGRAPHIC_STATUS_UPDATION_FAILED"),
	NO_SLOTS_AVAILABLE_FOR_THAT_DATE("NO_SLOTS_AVAILABLE_FOR_THAT_DATE"),
	NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER("NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER"),
	DEMOGRAPHIC_GET_STATUS_FAILED("DEMOGRAPHIC_GET_STATUS_FAILED"),
	AVAILABILITY_TABLE_NOT_ACCESSABLE("AVAILABILITY_TABLE_NOT_ACCESSABLE"),
	BOOKING_DATA_NOT_FOUND("BOOKING_DATA_NOT_FOUND"),
	APPOINTMENT_CANNOT_BE_CANCELED("APPOINTMENT_CANNOT_BE_CANCELED"),
	APPOINTMENT_TIME_SLOT_IS_ALREADY_CANCELED("APPOINTMENT_TIME_SLOT_IS_ALREADY_CANCELED"),
	APPOINTMENT_CANCEL_FAILED("APPOINTMENT_CANCEL_FAILED"),
	APPOINTMENT_REBOOKING_FAILED("APPOINTMENT_REBOOKING_FAILED"),
	MASTER_DATA_NOT_FOUND("MASTER_DATA_NOT_FOUND"),
	HOLIDAY_MASTER_DATA_NOT_FOUND("HOLIDAY_MASTER_DATA_NOT_FOUND"),
	INVALID_REQUEST_PARAMETER("INVALID_REQUEST_PARAMETER"),
    DOCUMENTS_NOT_FOUND_EXCEPTION("DOCUMENTS_NOT_FOUND_EXCEPTION");
	/**
	 * @param code
	 */
	private ErrorMessages(String message) {
		this.message = message;
	}

	private final String message;

	/**
	 * @return
	 */
	public String getMessage() {
		return message;
	}

}
