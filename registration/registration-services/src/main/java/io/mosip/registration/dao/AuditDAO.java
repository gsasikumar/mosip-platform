package io.mosip.registration.dao;

import java.time.LocalDateTime;
import java.util.List;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.registration.entity.RegistrationAuditDates;

/**
 * DAO class for Audit
 * 
 * @author Balaji Sridharan
 * @author Yaswanth S
 * @since 1.0.0
 */
public interface AuditDAO {

	/**
	 * Delete All audit rows In between time
	 * 
	 * @param auditLogFromDtimes
	 *            startTime
	 * @param auditLogToDtimes
	 *            end time
	 */
	void deleteAll(LocalDateTime auditLogFromDtimes, LocalDateTime auditLogToDtimes);

	/**
	 * Retrieves the {@link Audit} logs which are yet to be synchronized to the
	 * server along with the registration packet
	 * 
	 * @param registrationAuditDates
	 *            the start and end DateTimes of the audits synchronized with last
	 *            registration packet
	 * @return the {@link Audit} logs to be synchronized to the server with
	 *         registration packet
	 */
	List<Audit> getAudits(RegistrationAuditDates registrationAuditDates);

}
