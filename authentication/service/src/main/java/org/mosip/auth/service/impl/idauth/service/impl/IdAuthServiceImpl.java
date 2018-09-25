package org.mosip.auth.service.impl.idauth.service.impl;

import java.util.Date;
import java.util.Optional;

import org.mosip.auth.core.constant.AuditServicesConstants;
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
import org.mosip.auth.service.util.RestUtil;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The class validates the UIN and VID
 * 
 * @author Arun Bose
 */
@Service
public class IdAuthServiceImpl implements IdAuthService {
	
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
		AuditRequestDto auditRequest = auditFactory.buildRequest("moduleId", "description");

		RestRequestDTO restRequest;
		try {
			restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
					AuditResponseDto.class);
		} catch (IDDataValidationException e) {
			logger.error("sessionId", null, null, e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN,	e);
		}

		RestUtil.requestAsync(restRequest);  

		return refId;
	}

	public String validateVID(String vid) throws IdAuthenticationBusinessException {
		String refId = null;
		VIDEntity vidEntity = vidRepository.getOne(vid);
		if (null != vidEntity) {

			if (vidEntity.isActive()) {
				Date currentDate = new Date();
				if (vidEntity.getExpiryDate().before(currentDate)) {
					refId = vidEntity.getId();
					Optional<UinEntity> uinEntityOpt = uinRepository.findById(refId);
					if (uinEntityOpt.isPresent()) {
						UinEntity uinEntity = uinEntityOpt.get();
						if (uinEntity.isActive()) {
							throw new IdValidationFailedException(IdAuthenticationErrorConstants.INACTIVE_UIN);
						}
					} else {
						throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_UIN);
					}

				} else {
					throw new IdValidationFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
				}
			} else {
				// TODO log error
				throw new IdValidationFailedException(IdAuthenticationErrorConstants.INACTIVE_VID);
			}
		} else {
			throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_VID);
		}
		
		//TODO Update audit details
		AuditRequestDto auditRequest = auditFactory.buildRequest("moduleId", "description");

		RestRequestDTO restRequest;
		try {
			restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
					AuditResponseDto.class);
		} catch (IDDataValidationException e) {
			logger.error("sessionId", null, null, e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN,	e);
		}

		RestUtil.requestAsync(restRequest);  

		return refId;
	}

	/*
	 * public String getRefId(String UIN) { String refId = null; try { UinEntity
	 * uinEntity = uinRepository.findByUin(UIN); if (uinEntity != null) { refId =
	 * uinEntity.getId(); } else { throw new
	 * IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_UIN); } }
	 * catch (Exception e) { // throw connection exception }
	 * 
	 * return refId; }
	 */
}
