package io.mosip.preregistration.booking.errorcodes;

/**
 * Error codes
 * 
 * @author M1046129
 *
 */
public enum ErrorCodes {

	PRG_BOOK_RCI_001, // Appointment cannot be booked
	PRG_BOOK_RCI_002, // availability not thr for selected time
	PRG_BOOK_RCI_003, // User has not been selected any time slot
	PRG_BOOK_RCI_004, // Appointment time slot is already booked
	PRG_BOOK_RCI_005, // Appointment booking failed
	PRG_BOOK_RCI_006, // Pre id not found
	PRG_BOOK_RCI_007, // reg center id not found
	PRG_BOOK_RCI_008, // date is not selected
	PRG_BOOK_RCI_009, // Invalid Date and Time
	PRG_BOOK_RCI_010, // Booking Table not found
	PRG_BOOK_RCI_011, // status code updation failed
	PRG_BOOK_RCI_012, // get status code failed
	PRG_PAM_APP_002, PRG_BOOK_002, PRG_BOOK_001

}
