package org.mosip.registration.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;

import org.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import org.mosip.kernel.core.spi.auditmanager.AuditHandler;
import org.mosip.registration.constants.AppModuleEnum;
import org.mosip.registration.constants.AuditEventEnum;
import org.mosip.registration.constants.RegConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.stereotype.Service;

import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;;

/**
 * Class to Audit the events of Registration.
 * <p>
 * This class creates a wrapper around {@link AuditRequest} class. This class
 * creates a {@link AuditRequest} object for each audit event and persists the
 * same.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class AuditFactory {

	@Autowired
	private AuditHandler auditHandler;

	/**
	 * Static method to audit the events across Registration Processor Module.
	 * <p>
	 * This method takes {@code AuditEventEnum}, {@link AppModuleEnum}, audit
	 * description, refId and refIdType as inputs values from Session Context object
	 * namely createdBy, sessionUserId and sessionUserName to build the
	 * {@link AuditRequest} object. This {@link AuditRequest} object will be passed
	 * to the {@link AuditingHandler} which will persist the audit event in
	 * database.
	 * 
	 * @param auditEventEnum
	 *            this {@code Enum} contains the event details namely eventId,
	 *            eventType and eventName
	 * @param appModuleEnum
	 *            this {@code Enum} contains the application module details namely
	 *            moduleId and moduleName
	 * @param auditDescription
	 *            the description of the audit event
	 * @param refId
	 *            the ref id of the audit event
	 * @param refIdType
	 *            the ref id type of the audit event
	 */
	public void audit(AuditEventEnum auditEventEnum, AppModuleEnum appModuleEnum, String auditDescription, String refId,
			String refIdType) {

		// Getting Host IP Address and Name
		String hostIP = null;
		String hostName = null;
		try {
			InetAddress hostInetAddress = InetAddress.getLocalHost();
			hostIP = new String(hostInetAddress.getAddress());
			hostName = hostInetAddress.getHostName();
		} catch (UnknownHostException e) {
			hostIP = getPropertyValue(RegConstants.HOST_IP);
			hostName = getPropertyValue(RegConstants.HOST_NAME);
		}

		// TODO: Get createdBy, sessionUserId, SessionUserName values from Session
		// Context
		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
				.setApplicationId(getPropertyValue(RegConstants.APPLICATION_ID))
				.setApplicationName(getPropertyValue(RegConstants.APPLICATION_NAME)).setCreatedBy("createdBy")
				.setDescription(auditDescription).setEventId(auditEventEnum.getId())
				.setEventName(auditEventEnum.getName()).setEventType(auditEventEnum.getType()).setHostIp(hostIP)
				.setHostName(hostName).setId(refId).setIdType(refIdType).setModuleId(appModuleEnum.getId())
				.setModuleName(appModuleEnum.getName()).setSessionUserId("sessionUserId")
				.setSessionUserName("sessionUserName");
		auditHandler.writeAudit(auditRequestBuilder.build());
	}
}
