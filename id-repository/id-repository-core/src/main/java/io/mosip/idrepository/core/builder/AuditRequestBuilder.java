package io.mosip.idrepository.core.builder;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.constant.AuditEvents;
import io.mosip.idrepository.core.constant.AuditModules;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.dto.AuditRequestDto;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import lombok.NoArgsConstructor;

/**
 * A factory for creating and building AuditRequest objects from
 * audit.properties
 *
 * @author Manoj SP
 */
@Component
@NoArgsConstructor
public class AuditRequestBuilder {

	/** The mosipLogger. */
	private static Logger mosipLogger = IdRepoLogger.getLogger(AuditRequestBuilder.class);

	/** The env. */
	@Autowired
	private Environment env;

	/**
	 * Builds the request.
	 *
	 * @param module the module
	 * @param event  the event
	 * @param id     the id
	 * @param desc   the desc
	 * @return the audit request dto
	 */
	public RequestWrapper<AuditRequestDto> buildRequest(AuditModules module, AuditEvents event, String id,
			String desc) {
		RequestWrapper<AuditRequestDto> request = new RequestWrapper<>();
		AuditRequestDto auditRequest = new AuditRequestDto();
		String hostName;
		String hostAddress;

		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			hostName = inetAddress.getHostName();
			hostAddress = inetAddress.getHostAddress();
		} catch (UnknownHostException ex) {
			mosipLogger.error("sessionId", "AuditRequestFactory", ex.getClass().getName(),
					"Exception : " + ExceptionUtils.getStackTrace(ex));
			hostName = env.getProperty("audit.defaultHostName");
			hostAddress = env.getProperty("audit.defaultHostAddress");
		}

		auditRequest.setEventId(event.getEventId());
		auditRequest.setEventName(event.getEventName());
		auditRequest.setEventType(event.getEventType());
		auditRequest.setActionTimeStamp(DateUtils.parseUTCToLocalDateTime(DateUtils.getUTCCurrentDateTimeString(),
				env.getProperty("mosip.utc-datetime-pattern")));
		auditRequest.setHostName(hostName);
		auditRequest.setHostIp(hostAddress);
		auditRequest.setApplicationId(env.getProperty(IdRepoConstants.APPLICATION_ID.getValue()));
		auditRequest.setApplicationName(env.getProperty(IdRepoConstants.APPLICATION_NAME.getValue()));
		auditRequest.setSessionUserId("sessionUserId");
		auditRequest.setSessionUserName("sessionUserName");
		auditRequest.setId(id);
		auditRequest.setIdType("UIN");
		auditRequest.setCreatedBy(env.getProperty("user.name"));
		auditRequest.setModuleName(module.getModuleName());
		auditRequest.setModuleId(module.getModuleId());
		auditRequest.setDescription(desc);

		request.setId("audit");
		request.setRequest(auditRequest);
		request.setVersion("1.0");
		request.setRequesttime(DateUtils.parseUTCToLocalDateTime(DateUtils.getUTCCurrentDateTimeString(),
				env.getProperty("mosip.utc-datetime-pattern")));

		return request;
	}
}
