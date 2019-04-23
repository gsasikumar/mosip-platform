package io.mosip.authentication.vid.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.VIDEntity;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.repository.VIDRepository;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.dto.vid.ResponseDTO;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.id.service.VIDService;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idgenerator.vid.impl.VidGeneratorImpl;

/**
 * The Class VIDServiceImpl.
 * 
 * @author Arun Bose
 */
@Component
public class VIDServiceImpl implements VIDService {

	private static final String VID_GENERATION_REQUEST = "VID generation request";

	private static final String VERSION = "1.0";

	private static final String IDA = "IDA";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";


	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(VIDServiceImpl.class);

	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idAuthService;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/** The vid repository. */
	@Autowired
	private VIDRepository vidRepository;

	/** The vid generator. */
	@Autowired
	private VidGeneratorImpl vidGenerator;

	/** The env. */
	@Autowired
	Environment env;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.spin.service.StaticPinService#generateVID(
	 * java.lang.String)
	 */
	// @Override
	public VIDResponseDTO generateVID(String uin) throws IdAuthenticationBusinessException {
		Map<String, Object> uinMap = idAuthService.processIdType(IdType.UIN.getType(), uin, false);
		VIDEntity vidEntityObj = null;
		ResponseDTO responseDTO = new ResponseDTO();
		VIDResponseDTO vidResponseDTO = new VIDResponseDTO();
		vidResponseDTO.setId(IdAuthConfigKeyConstants.MOSIP_IDENTITY_VID);
		vidResponseDTO.setVersion(VERSION);
		if (Objects.nonNull(uinMap) && !uinMap.isEmpty()) {
			List<VIDEntity> vidEntityList = vidRepository.findByUIN(uin, PageRequest.of(0, 1));
			if (vidEntityList.isEmpty()) {
				try {
					vidEntityObj = generateVIDEntity(uin);
					vidRepository.save(vidEntityObj);
				} catch (DataAccessException ex) {
					logger.error(SESSION_ID, this.getClass().getName(), ex.getClass().getName(), ex.getMessage());
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.VID_GENERATION_FAILED,
							ex);
				}
				constructResponse(vidEntityObj, vidResponseDTO, responseDTO);
			} else {
				handleIfVIDAlreadyExist(uin, responseDTO, vidResponseDTO, vidEntityList);
			}
		} else {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN);
		}
		vidResponseDTO.setResponseTime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN)));
		auditHelper.audit(AuditModules.VID_GENERATION_REQUEST, AuditEvents.VID_GENERATE_REQUEST_RESPONSE,
				IdType.UIN.getType(), IdType.UIN, VID_GENERATION_REQUEST);
		return vidResponseDTO;
	}
	
	/**
	 * Handle if VID already exist.
	 *
	 * @param uin the uin
	 * @param responseDTO the response DTO
	 * @param vidResponseDTO the vid response DTO
	 * @param vidEntityList the vid entity list
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void handleIfVIDAlreadyExist(String uin, ResponseDTO responseDTO, VIDResponseDTO vidResponseDTO,
			List<VIDEntity> vidEntityList) throws IdAuthenticationBusinessException {
		VIDEntity vidEntityObj;
		vidEntityObj = vidEntityList.get(0);
		if (vidEntityObj.isActive()
				&& vidEntityObj.getExpiryDate().isAfter(DateUtils.getUTCCurrentDateTime())) {
			constructResponse(vidEntityObj, vidResponseDTO, responseDTO);
			List<AuthError> listAuthError = new ArrayList<>();
			AuthError authError = new AuthError();
			authError.setErrorCode(IdAuthenticationErrorConstants.VID_REGENERATION_FAILED.getErrorCode());
			authError.setErrorMessage(IdAuthenticationErrorConstants.VID_REGENERATION_FAILED.getErrorMessage());
			listAuthError.add(authError);
			vidResponseDTO.setErrors(listAuthError);
		} else if (!vidEntityObj.isActive()
				|| vidEntityObj.getExpiryDate().isBefore(DateUtils.getUTCCurrentDateTime())) {
			try {
				vidEntityObj = generateVIDEntity(uin);
				vidRepository.save(vidEntityObj);
			} catch (DataAccessException ex) {
				logger.error(SESSION_ID, this.getClass().getName(), ex.getClass().getName(), ex.getMessage());
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.VID_GENERATION_FAILED, ex);
			}

			constructResponse(vidEntityObj, vidResponseDTO, responseDTO);
		}
	}

	/**
	 * Construct response.
	 *
	 * @param vidEntityObj the vid entity obj
	 * @param vidResponseDTO the vid response DTO
	 * @param responseDTO2 
	 */
	private void constructResponse(VIDEntity vidEntityObj, VIDResponseDTO vidResponseDTO, ResponseDTO responseDTO) {
		responseDTO.setVid(vidEntityObj.getId());
		vidResponseDTO.setResponse(responseDTO);
		vidResponseDTO.setErrors(Collections.emptyList());
	}

	/**
	 * Generate VID entity.
	 *
	 * @param uin the uin
	 * @return the VID entity
	 */

	private VIDEntity generateVIDEntity(String uin) {
		VIDEntity vidEntityObj;
		vidEntityObj = new VIDEntity();
		vidEntityObj.setId((String) vidGenerator.generateId());
		vidEntityObj.setUin(uin);
		vidEntityObj.setActive(true);
		vidEntityObj.setCreatedBy(IDA);
		vidEntityObj.setCreatedDTimes(DateUtils.getUTCCurrentDateTime());
		vidEntityObj.setExpiryDate(
				DateUtils.getUTCCurrentDateTime().plusHours(env.getProperty(IdAuthConfigKeyConstants.MOSIP_VID_VALIDITY_HOURS, Long.class)));
		vidEntityObj.setGeneratedOn(DateUtils.getUTCCurrentDateTime());
		vidEntityObj.setDeleted(false);
		return vidEntityObj;
	}
}
