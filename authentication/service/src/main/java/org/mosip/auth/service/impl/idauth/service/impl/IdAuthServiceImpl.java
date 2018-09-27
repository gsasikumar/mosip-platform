package org.mosip.auth.service.impl.idauth.service.impl;

import java.util.Date;
import java.util.Optional;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.core.spi.idauth.service.IdAuthService;
import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.auth.core.util.dto.AuditResponseDto;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.dao.UinRepository;
import org.mosip.auth.service.dao.VIDRepository;
import org.mosip.auth.service.entity.UinEntity;
import org.mosip.auth.service.entity.VIDEntity;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The class validates the UIN and VID
 * 
 * @author Arun Bose
 */
@Service
public class IdAuthServiceImpl implements IdAuthService {
	
	private static final String DEFAULT_SESSION_ID = "sessionId";

	@Autowired
	private RestHelper restHelper;
	
	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	@Autowired
	private RestRequestFactory  restFactory;
	
	@Autowired
	private AuditRequestFactory auditFactory;
	
	@Autowired
	private UinRepository uinRepository;

	@Autowired
	private VIDRepository vidRepository;

	public String validateUIN(String UIN) throws IdAuthenticationBusinessException {
		String refId = null;
		UinEntity uinEntity = uinRepository.findByUin(UIN);
		if (null != uinEntity) {

			if (uinEntity.isActive()) {
				refId = uinEntity.getId();
			} else {
				// TODO log error
				throw new IdValidationFailedException(IdAuthenticationErrorConstants.INACTIVE_UIN);
			}
		} else {
			throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_UIN);
		}

		//TODO Update audit details
		auditData();  

		return refId;
	}

	private void auditData() throws IdAuthenticationBusinessException {
		AuditRequestDto auditRequest = auditFactory.buildRequest("moduleId", "description");

		RestRequestDTO restRequest;
		try {
			restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
					AuditResponseDto.class);
		} catch (IDDataValidationException e) {
			logger.error(DEFAULT_SESSION_ID, null, null, e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN,	e);
		}

		restHelper.requestAsync(restRequest);
	}

	public String validateVID(String vid) throws IdAuthenticationBusinessException {
		String refId = doValidateVIDEntity(vid);
		
		auditData();  

		return refId;
	}

	private String doValidateVIDEntity(String vid) throws IdValidationFailedException {
		String refId = null;
		VIDEntity vidEntity = vidRepository.getOne(vid);
		if (null != vidEntity) {

			if (vidEntity.isActive()) {
				Date currentDate = new Date();
				if (vidEntity.getExpiryDate().before(currentDate)) {
					refId = vidEntity.getId();
					Optional<UinEntity> uinEntityOpt = uinRepository.findById(refId);
					doValidateUIN(uinEntityOpt);
				} else {
					throw new IdValidationFailedException(IdAuthenticationErrorConstants.EXPIRED_VID);
				}
			} else {
				// TODO log error
				throw new IdValidationFailedException(IdAuthenticationErrorConstants.INACTIVE_VID);
			}
		} else {
			throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_VID);
		}
		return refId;
	}

	private void doValidateUIN(Optional<UinEntity> uinEntityOpt) throws IdValidationFailedException {
		if (uinEntityOpt.isPresent()) {
			UinEntity uinEntity = uinEntityOpt.get();
			if (uinEntity.isActive()) {
				throw new IdValidationFailedException(IdAuthenticationErrorConstants.INACTIVE_UIN);
			}
		} else {
			throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_UIN);
		}
	}

}
