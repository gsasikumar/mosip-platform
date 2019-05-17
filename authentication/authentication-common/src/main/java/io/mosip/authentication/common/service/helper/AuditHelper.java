package io.mosip.authentication.common.service.helper;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.UUIDUtils;

/**
 * The Class AuditHelper - build audit requests and send it to audit service.
 *
 * @author Manoj SP
 */
@Component
public class AuditHelper {

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The audit factory. */
	@Autowired
	private AuditRequestFactory auditFactory;

	/** The Id Info Fetcher */
	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/** The rest factory. */
	@Autowired
	private RestRequestFactory restFactory;

	@Autowired
	private Environment env;

	/** The Constant SUCCESS_STATUS. */
	private static final String SUCCESS_STATUS = "Y";

	/** The Constant FAILED. */
	private static final String FAILED = "N";

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(AuditHelper.class);

	public String getUinorVid(AuthRequestDTO authRequestDTO) {
		return Optional.of(idInfoFetcher.getUinOrVid(authRequestDTO).get()).orElse("");
	}

	/**
	 * Method to build audit requests and send it to audit service.
	 *
	 * @param module {@link AuditModules}
	 * @param event  {@link AuditEvents}
	 * @param id     UIN/VID
	 * @param idType {@link IdType}
	 * @param desc   the desc
	 * @throws {@link IDDataValidationException} the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, IdType idType, String desc)
			throws IDDataValidationException {
		RequestWrapper<AuditRequestDto> auditRequest = auditFactory.buildRequest(module, event, id, idType, desc);
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				Map.class);
		restHelper.requestAsync(restRequest);
	}

	/**
	 * Creates UUID
	 * 
	 * @param uin
	 * @return
	 */
	private String createId(String uin) {
		String currentDate = DateUtils.formatDate(new Date(),
				env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		String uinAndDate = uin + "-" + currentDate;
		return UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID, uinAndDate).toString();
	}

}
