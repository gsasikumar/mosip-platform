package io.mosip.registration.processor.stages.demodedupe;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegDemoDedupeListDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Service
@Transactional
public class DemodedupeProcessor {

	private static Logger regProcLogger = RegProcessorLogger.getLogger(DemodedupeProcessor.class);

	public static final String FILE_SEPARATOR = "\\";

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";
	
	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "MOSIP";

	private static final String IDENTIFY = "IDENTIFY";

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The demographic dedupe repository. */
	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The demo dedupe. */
	@Autowired
	private DemoDedupe demoDedupe;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The adapter. */
	@Autowired
	private FileSystemAdapter adapter;

	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();
	
	@Autowired
	Utilities utility;
	
	@Autowired
	private RegistrationStatusDao registrationStatusDao;
	
	/** The reg processor identity json. */
	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;
	
	@Autowired
	private ABISHandlerUtil abisHandlerUtil;
	
	List<DemographicInfoDto> duplicateDtos = new ArrayList<>();

	InputStream demographicInfoStream = null;

	byte[] bytesArray = null;
	
	private boolean isMatchFound = false;
	
	private String description = "";

	private String code = "";
	
	private static final String POST_API_PROCESS = "POST_API_PROCESS";
	
	private static final String RE_REGISTER = "RE-REGISTER";

	public MessageDTO process(MessageDTO object, String stageName) {

		object.setMessageBusAddress(MessageBusAddress.DEMO_DEDUPE_BUS_IN);
		object.setInternalError(Boolean.FALSE);
		object.setIsValid(Boolean.FALSE);

		boolean isTransactionSuccessful = false;

		String registrationId = object.getRid();
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);

		try {
			
			// Persist Demographic packet Data if packet Registration type is NEW
			if (registrationStatusDto.getRegistrationType().equals(RegistrationType.NEW.name())) {
				
				String packetStatus = abisHandlerUtil.getPacketStatus(registrationStatusDto,
						RegistrationTransactionTypeCode.BIOGRAPHIC_VERIFICATION.toString());
				
				if (packetStatus.equalsIgnoreCase(RegistrationType.NEW.name())) {
					InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKET_META_INFO.name());
					PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
							PacketMetaInfo.class);
					demographicInfoStream = adapter.getFile(registrationId,
							PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
					bytesArray = IOUtils.toByteArray(demographicInfoStream);
					packetInfoManager.saveDemographicInfoJson(bytesArray, registrationId,
							packetMetaInfo.getIdentity().getMetaData());
					isTransactionSuccessful = performDemoDedupe(registrationStatusDto,object);
				} else if (packetStatus.equalsIgnoreCase(POST_API_PROCESS)) {
					// Do the handler process
					isTransactionSuccessful = processDemoDedupeRequesthandler(registrationStatusDto, object);
				}
				
			} else if (registrationStatusDto.getRegistrationType().equals(RegistrationType.UPDATE.name())
					|| registrationStatusDto.getRegistrationType().equals(RegistrationType.RESUPDATE.name())) {
				IndividualDemographicDedupe demoDedupeData = new IndividualDemographicDedupe();

				demographicInfoStream = adapter.getFile(registrationId,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
				bytesArray = IOUtils.toByteArray(demographicInfoStream);
				String demographicJsonString = new String(bytesArray);
				IndividualDemographicDedupe demographicData = packetInfoManager
						.getIdentityKeysAndFetchValuesFromJSON(demographicJsonString);
				regProcessorIdentityJson = utility.getRegistrationProcessorIdentityJson();
				Long uinFieldCheck = utility.getUIn(registrationId);
				JSONObject jsonObject = utility.retrieveIdrepoJson(uinFieldCheck);
				if (jsonObject == null) {
					throw new IdRepoAppException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
				}
				demoDedupeData.setName(demographicData.getName() == null ? JsonUtil.getJsonValues(jsonObject,
						regProcessorIdentityJson.getIdentity().getName().getValue()) : demographicData.getName());
				demoDedupeData.setDateOfBirth(demographicData.getDateOfBirth() == null
						? JsonUtil.getJSONValue(jsonObject, regProcessorIdentityJson.getIdentity().getDob().getValue())
						: demographicData.getDateOfBirth());
				demoDedupeData.setGender(demographicData.getGender() == null ? JsonUtil.getJsonValues(jsonObject,
						regProcessorIdentityJson.getIdentity().getGender().getValue()) : demographicData.getGender());
				packetInfoManager.saveIndividualDemographicDedupeUpdatePacket(demoDedupeData, registrationId);
				object.setIsValid(Boolean.TRUE);
			}

			
/*			Set<String> uniqueUins = new HashSet<>();
			Set<String> uniqueMatchedRefIds = new HashSet<>();
			List<String> uniqueMatchedRefIdList = new ArrayList<>();
			for (DemographicInfoDto demographicInfoDto : duplicateDtos) {
				uniqueUins.add(demographicInfoDto.getUin());
				uniqueMatchedRefIds.add(demographicInfoDto.getRegId());
			}
			uniqueMatchedRefIdList.addAll(uniqueMatchedRefIds);
			List<String> duplicateUINList = new ArrayList<>(uniqueUins);

			if (!duplicateDtos.isEmpty()) {

				registrationStatusDto
						.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_POTENTIAL_MATCH_FOUND.toString());
				registrationStatusDto.setStatusComment(StatusMessage.POTENTIAL_MATCH_FOUND);

				registrationStatusService.updateRegistrationStatus(registrationStatusDto);
				// authenticating duplicateIds with provided packet biometrics
				boolean isDuplicateAfterAuth = demoDedupe.authenticateDuplicates(registrationId, duplicateUINList);

				if (isDuplicateAfterAuth) {
					object.setIsValid(Boolean.FALSE);

					int retryCount = registrationStatusDto.getRetryCount() != null
							? registrationStatusDto.getRetryCount() + 1
							: 1;
					description = registrationStatusDto.getStatusComment() + " -- " +registrationId;
					registrationStatusDto.setRetryCount(retryCount);

					registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_FAILED);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.toString());
					registrationStatusDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());

					demographicDedupeRepository.updateIsActiveIfDuplicateFound(registrationId);
					// Saving potential duplicates in reg_manual_verification table
					packetInfoManager.saveManualAdjudicationData(uniqueMatchedRefIdList, registrationId,
							DedupeSourceName.DEMO);

				} else {
					object.setIsValid(Boolean.TRUE);
					registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_SUCCESS);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_SUCCESS.toString());

					code = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP_POTENTIAL_DUPLICATION_FOUND.getCode();
					description = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP_POTENTIAL_DUPLICATION_FOUND.getMessage()
							+ " -- " + registrationId;
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), code, registrationId, description);
					registrationStatusDto.setUpdatedBy(USER);
					isTransactionSuccessful = true;
				}

			} else {
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_SUCCESS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_SUCCESS.toString());

				code = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getCode();
				description = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getMessage() + " -- " +registrationId;
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), code, registrationId, description);
				registrationStatusDto.setUpdatedBy(USER);
				isTransactionSuccessful = true;
			}*/

			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.DEMOGRAPHIC_VERIFICATION.toString());
			registrationStatusDto.setRegistrationStageName(stageName);

		} catch (FSAdapterException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_REPROCESSING.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.PACKET_DEMO_PACKET_STORE_NOT_ACCESSIBLE.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_PACKET_STORE_NOT_ACCESSIBLE.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_PACKET_STORE_NOT_ACCESSIBLE.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (IllegalArgumentException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_ILLEGAL_ACCESS_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.ILLEGAL_ARGUMENT_EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} finally {
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			if(isMatchFound) {
				saveDuplicateDtoList(duplicateDtos, registrationStatusDto);
			}
			
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Succes/Error code */
			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getCode() : code;
			String moduleName = ModuleName.DEMO_DEDUPE.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);

		}

		return object;
	}

	private boolean performDemoDedupe(InternalRegistrationStatusDto registrationStatusDto, MessageDTO object) {
		boolean isTransactionSuccessful = false;
		String registrationId = registrationStatusDto.getRegistrationId();
		// Potential Duplicate Ids after performing demo dedupe
		duplicateDtos = demoDedupe.performDedupe(registrationStatusDto.getRegistrationId());
		
		if (!duplicateDtos.isEmpty()) {
			isMatchFound = true;
			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);
			code = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getCode();
			description = "Record is inserted in demo dedupe potential match, destination stage is abis handler"
					+ " -- " + registrationId;
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					"Record is inserted in demo dedupe potential match, destination stage is abis handler");

		} else {
			object.setIsValid(Boolean.TRUE);

			registrationStatusDto
			.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
			
			registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_SUCCESS);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_SUCCESS.toString());

			code = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getCode();
			description = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getMessage() + " -- " +registrationId;
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), code, registrationId, description);
			registrationStatusDto.setUpdatedBy(USER);
			isTransactionSuccessful = true;
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(), "Demo dedupe successful. No duplicates found");
		}
		return isTransactionSuccessful;
	}

	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;
	}
	
	
	private boolean processDemoDedupeRequesthandler(InternalRegistrationStatusDto registrationStatusDto,
			MessageDTO object) throws ApisResourceAccessException, IOException {
		boolean isTransactionSuccessful = false;
		String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());

		List<AbisResponseDto> abisResponseDto = packetInfoManager.getAbisResponseRecords(latestTransactionId, IDENTIFY);

		for (AbisResponseDto responseDto : abisResponseDto) {
			if (responseDto.getStatusCode().equalsIgnoreCase(RegistrationTransactionStatusCode.PROCESSED.toString())) {
				List<AbisResponseDetDto> abisResponseDetDto = packetInfoManager.getAbisResponseDetRecords(responseDto);
				if (abisResponseDetDto.isEmpty()) {
					object.setIsValid(Boolean.TRUE);
					registrationStatusDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
							registrationStatusDto.getRegistrationId(), "ABIS response Details null, hence no duplicates found");
					isTransactionSuccessful = true;
				} else {
					object.setIsValid(Boolean.FALSE);
					registrationStatusDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
					saveManualAdjudicationData(registrationStatusDto);
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
							registrationStatusDto.getRegistrationId(), "ABIS response Details found. Hence sending to manual adjudication");

				}
			} else {
				int retryCount = registrationStatusDto.getRetryCount() != null
						? registrationStatusDto.getRetryCount() + 1
						: 1;
				description = registrationStatusDto.getStatusComment() + " -- "
						+ registrationStatusDto.getRegistrationId();
				registrationStatusDto.setRetryCount(retryCount);
				
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						"ABIS response not processed. Hence sending to Reprocess");
			}
		}
		
		return isTransactionSuccessful;
	}
	
	private boolean saveDuplicateDtoList(List<DemographicInfoDto> duplicateDtos,
			InternalRegistrationStatusDto registrationStatusDto) {
		boolean isDataSaved = false;
		for (DemographicInfoDto demographicInfoDto : duplicateDtos) {
			InternalRegistrationStatusDto potentialMatchRegistrationDto = registrationStatusService
					.getRegistrationStatus(demographicInfoDto.getRegId());
			if (potentialMatchRegistrationDto.getLatestTransactionStatusCode()
					.equalsIgnoreCase(RegistrationTransactionStatusCode.REPROCESS.toString())
					|| potentialMatchRegistrationDto.getLatestTransactionStatusCode().equalsIgnoreCase(RE_REGISTER)) {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						"The packet status is Rejected or Re-Register. Hence ignoring Registration Id");
			} else if (potentialMatchRegistrationDto.getLatestTransactionStatusCode()
					.equalsIgnoreCase(RegistrationTransactionStatusCode.IN_PROGRESS.toString())
					|| potentialMatchRegistrationDto.getLatestTransactionStatusCode()
							.equalsIgnoreCase(RegistrationTransactionStatusCode.PROCESSED.toString())) {
				String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());
				RegDemoDedupeListDto regDemoDedupeListDto = new RegDemoDedupeListDto();
				regDemoDedupeListDto.setRegId(registrationStatusDto.getRegistrationId());
				regDemoDedupeListDto.setMatchedRegId(demographicInfoDto.getRegId());
				regDemoDedupeListDto.setRegtrnId(latestTransactionId);
				regDemoDedupeListDto.setIsDeleted(Boolean.FALSE);
				regDemoDedupeListDto.setCrBy(CREATED_BY);
				packetInfoManager.saveDemoDedupePotentialData(regDemoDedupeListDto);
				isDataSaved = true;
			} else {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						"The packet status is something different");
			}
		}
		return isDataSaved;
	}

	private void saveManualAdjudicationData(InternalRegistrationStatusDto registrationStatusDto) throws ApisResourceAccessException, IOException {
		List<String> matchedRegIds = abisHandlerUtil.getUniqueRegIds(registrationStatusDto.getRegistrationId(),
				SyncTypeDto.NEW.toString());
		packetInfoManager.saveManualAdjudicationData(matchedRegIds, registrationStatusDto.getRegistrationId(),
				DedupeSourceName.DEMO);
	}
	
}
