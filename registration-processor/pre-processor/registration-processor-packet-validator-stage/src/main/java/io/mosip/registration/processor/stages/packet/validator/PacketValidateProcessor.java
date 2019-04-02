package io.mosip.registration.processor.stages.packet.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.constant.RegistrationStageName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.idjson.Document;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.MainRequestDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.MainResponseDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ReverseDataSyncRequestDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ReverseDatasyncReponseDTO;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.utils.ApplicantDocumentValidation;
import io.mosip.registration.processor.stages.utils.CheckSumValidation;
import io.mosip.registration.processor.stages.utils.DocumentUtility;
import io.mosip.registration.processor.stages.utils.FilesValidation;
import io.mosip.registration.processor.stages.utils.MasterDataValidation;
import io.mosip.registration.processor.stages.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Service
@Transactional
public class PacketValidateProcessor {

	/** The identity iterator util. */
	IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketValidateProcessor.class);

	/** The adapter. */
	@Autowired
	private FileSystemAdapter adapter;
	/** Validator stage */

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant APPLICANT_TYPE. */
	public static final String APPLICANT_TYPE = "applicantType";

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	RegistrationProcessorIdentity regProcessorIdentityJson;

	@Autowired
	private Environment env;

	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	/** The core audit request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	DocumentUtility documentUtility;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	JsonValidator jsonValidator;

	@Autowired
	private Utilities utility;

	/** The registration id. */
	private String registrationId = "";

	/** The description. */
	private String description;

	/** the Error Code */
	private String code;

	/** The flag check for reg_type. */
	private boolean regTypeCheck;

	JSONObject identityJson = null;

	JSONObject demographicIdentity = null;

	RegistrationExceptionMapperUtil registrationStatusMapperUtil = new RegistrationExceptionMapperUtil();

	/** The is transaction successful. */
	private boolean isTransactionSuccessful;
	private static final String PRE_REG_ID = "mosip.pre-registration.datasync";
	private static final String VERSION = "1.0";
	private static final String CREATED_BY = "MOSIP_SYSTEM";

	public MessageDTO process(MessageDTO object) {
		String preRegId = null;
		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		try {
			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.VALIDATE_PACKET.toString());
			registrationStatusDto.setRegistrationStageName(RegistrationStageName.PACKET_VALIDATOR_STAGE);
			object.setMessageBusAddress(MessageBusAddress.PACKET_VALIDATOR_BUS_IN);
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.FALSE);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "PacketValidatorStage::process()::entry");
			this.registrationId = object.getRid();
			description = "";
			isTransactionSuccessful = false;
			boolean isCheckSumValidated = false;
			boolean isApplicantDocumentValidation = false;
			boolean isFilesValidated = false;
			boolean isMasterDataValidated = false;

			try {
				registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);
				InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKET_META_INFO.name());
				PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
						PacketMetaInfo.class);
				List<FieldValue> metadataList = packetMetaInfo.getIdentity().getMetaData();
				object.setReg_type(identityIteratorUtil.getFieldValue(metadataList, JsonConstant.REGISTRATIONTYPE));
				regTypeCheck = (object.getReg_type().equalsIgnoreCase(RegistrationType.ACTIVATED.toString())
						|| object.getReg_type().equalsIgnoreCase(RegistrationType.DEACTIVATED.toString()));

				InputStream idJsonStream = adapter.getFile(registrationId,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
				byte[] bytearray = IOUtils.toByteArray(idJsonStream);
				String jsonString = new String(bytearray);

				ValidationReport isSchemaValidated = jsonValidator.validateJson(jsonString);

				InputStream documentInfoStream = null;
				List<Document> documentList = null;

				if (isSchemaValidated.isValid()) {
					FilesValidation filesValidation = new FilesValidation(adapter, registrationStatusDto);
					isFilesValidated = filesValidation.filesValidation(registrationId, packetMetaInfo.getIdentity());

					byte[] bytes = null;
					if (isFilesValidated) {
						documentInfoStream = adapter.getFile(registrationId,
								PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
						bytes = IOUtils.toByteArray(documentInfoStream);
						if (!regTypeCheck) {
							documentList = documentUtility.getDocumentList(bytes);
						}
						CheckSumValidation checkSumValidation = new CheckSumValidation(adapter, registrationStatusDto);

						isCheckSumValidated = checkSumValidation.checksumvalidation(registrationId,
								packetMetaInfo.getIdentity());

						if (isCheckSumValidated && !(regTypeCheck)) {
							ApplicantDocumentValidation applicantDocumentValidation = new ApplicantDocumentValidation(
									registrationStatusDto);
							isApplicantDocumentValidation = applicantDocumentValidation
									.validateDocument(packetMetaInfo.getIdentity(), documentList, registrationId);

							if (isApplicantDocumentValidation) {
								MasterDataValidation masterDataValidation = new MasterDataValidation(
										registrationStatusDto, env, registrationProcessorRestService, utility);
								isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
							}
						}
					}
				}

				if ((isSchemaValidated.isValid() && isFilesValidated && isCheckSumValidated
						&& isApplicantDocumentValidation && isMasterDataValidated)
						|| (isSchemaValidated.isValid() && isFilesValidated && isCheckSumValidated && regTypeCheck)) {
					object.setIsValid(Boolean.TRUE);
					registrationStatusDto.setStatusComment(StatusMessage.PACKET_STRUCTURAL_VALIDATION_SUCCESS);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_SUCCESS.toString());
					// ReverseDataSync

					IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
					preRegId = identityIteratorUtil.getFieldValue(packetMetaInfo.getIdentity().getMetaData(),
							JsonConstant.PREREGISTRATIONID);
					object.setRid(registrationStatusDto.getRegistrationId());
					isTransactionSuccessful = true;
					description = PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getMessage() + " -- "
							+ registrationId;
					code = PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getCode();
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId,
							"PacketValidatorStage::process()::exit");
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId, description);

				} else {
					object.setIsValid(Boolean.FALSE);
					int retryCount = registrationStatusDto.getRetryCount() != null
							? registrationStatusDto.getRetryCount() + 1
							: 1;
					description = "File validation(" + isFilesValidated + ")/Checksum validation(" + isCheckSumValidated
							+ ")/Applicant Document Validation(" + isApplicantDocumentValidation
							+ ") failed for registrationId " + registrationId;
					isTransactionSuccessful = false;
					registrationStatusDto.setRetryCount(retryCount);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED.toString());
					registrationStatusDto.setStatusComment(description);

					description = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + " -- "
							+ description;
					code = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode();
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId,
							"PacketValidatorStage::process()::exit");
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId, description);

				}

				registrationStatusDto.setUpdatedBy(USER);

				setApplicant(packetMetaInfo.getIdentity(), registrationStatusDto);

				registrationStatusService.updateRegistrationStatus(registrationStatusDto);
				isTransactionSuccessful = true;
			} catch (FSAdapterException e) {
				registrationStatusDto.setLatestTransactionStatusCode(
						registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
				isTransactionSuccessful = false;
				description = PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getMessage();
				code = PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getCode();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId,
						PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getMessage() + e.getMessage());
				object.setInternalError(Boolean.TRUE);
				object.setRid(registrationStatusDto.getRegistrationId());
			} catch (DataAccessException e) {
				registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.DATA_ACCESS_EXCEPTION));
				isTransactionSuccessful = false;
				description = PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage();
				code = PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId,
						PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage() + e.getMessage()
								+ ExceptionUtils.getStackTrace(e));
				object.setInternalError(Boolean.TRUE);
				object.setRid(registrationStatusDto.getRegistrationId());
			} catch (IOException exc) {
				registrationStatusDto.setLatestTransactionStatusCode(
						registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
				isTransactionSuccessful = false;
				description = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage();
				code = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId,
						PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + exc.getMessage()
								+ ExceptionUtils.getStackTrace(exc));
				object.setInternalError(Boolean.TRUE);
				object.setRid(registrationStatusDto.getRegistrationId());

			} catch (TablenotAccessibleException e) {
				registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.TABLE_NOT_ACCESSIBLE_EXCEPTION));
				object.setInternalError(Boolean.TRUE);
				description = PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage();
				code = PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code + " -- " + registrationId,
						PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e.toString());

			} catch (Exception ex) {
				registrationStatusDto.setLatestTransactionStatusCode(
						registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
				isTransactionSuccessful = false;
				description = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage();
				code = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId,
						PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + ex.getMessage()
								+ ExceptionUtils.getStackTrace(ex));
				object.setInternalError(Boolean.TRUE);
				object.setRid(registrationStatusDto.getRegistrationId());

			} finally {
				registrationStatusService.updateRegistrationStatus(registrationStatusDto);
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description);
				if (object.getInternalError()) {
					registrationStatusDto.setUpdatedBy(USER);
					int retryCount = registrationStatusDto.getRetryCount() != null
							? registrationStatusDto.getRetryCount() + 1
							: 1;

					registrationStatusDto.setRetryCount(retryCount);
					registrationStatusDto.setStatusComment(description);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED.toString());
					registrationStatusService.updateRegistrationStatus(registrationStatusDto);
				}
				description = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getMessage()
						: description;
				String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
				String eventName = isTransactionSuccessful ? EventName.UPDATE.toString()
						: EventName.EXCEPTION.toString();
				String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString()
						: EventType.SYSTEM.toString();

				/** Module-Id can be Both Succes/Error code */
				String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getCode()
						: code;
				String moduleName = ModuleName.PACKET_VALIDATOR.toString();
				auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
						moduleName, registrationId);
			}

			if (this.registrationId != null) {
				isTransactionSuccessful = false;
				MainResponseDTO<ReverseDatasyncReponseDTO> mainResponseDto = null;
				if (preRegId != null && !preRegId.trim().isEmpty()) {
					MainRequestDTO<ReverseDataSyncRequestDTO> mainRequestDto = new MainRequestDTO<>();
					mainRequestDto.setId(PRE_REG_ID);
					mainRequestDto.setVer(VERSION);
					mainRequestDto.setReqTime(new Date());
					ReverseDataSyncRequestDTO reverseDataSyncRequestDto = new ReverseDataSyncRequestDTO();
					reverseDataSyncRequestDto.setCreatedBy(CREATED_BY);
					reverseDataSyncRequestDto.setLangCode("eng");
					reverseDataSyncRequestDto.setPreRegistrationIds(Arrays.asList(preRegId));
					reverseDataSyncRequestDto.setCreatedDateTime(new Date());
					reverseDataSyncRequestDto.setUpdateDateTime(new Date());
					reverseDataSyncRequestDto.setUpdateBy(CREATED_BY);
					mainRequestDto.setRequest(reverseDataSyncRequestDto);

					mainResponseDto = (MainResponseDTO) restClientService.postApi(ApiName.REVERSEDATASYNC, "", "",
							mainRequestDto, MainResponseDTO.class);
					isTransactionSuccessful = true;

				}
				if (mainResponseDto != null && mainResponseDto.getErr() != null) {
					regProcLogger.error(LoggerFileConstant.REGISTRATIONID.toString(), registrationId.toString(),
							PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
							mainResponseDto.getErr().toString());
					isTransactionSuccessful = false;
					description = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage();

				}

			}

		} catch (ApisResourceAccessException e) {

			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId.toString(),
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
						httpClientException.getResponseBodyAsString() + ExceptionUtils.getStackTrace(e));
				description = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage();
				code = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getCode();
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId.toString(),
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
						httpServerException.getResponseBodyAsString() + ExceptionUtils.getStackTrace(e));
				description = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage();
				code = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getCode();
			} else {
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId.toString(),
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(), e.getMessage());
				description = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage();
				code = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getCode();
			}

		} catch (BaseUncheckedException e) {
			object.setInternalError(Boolean.TRUE);

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), registrationId,
					PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage(), e.toString());

			description = "Schema Validation Failed";
		} finally {
			description = isTransactionSuccessful ? "Reverse data sync of Pre-RegistrationIds sucessful" : description;
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Succes/Error code */
			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getCode()
					: code;
			String moduleName = ModuleName.PACKET_VALIDATOR.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}

		return object;

	}

	private void setApplicant(Identity identity, InternalRegistrationStatusDto registrationStatusDto) {
		IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
		String applicantType = identityIteratorUtil.getFieldValue(identity.getMetaData(), APPLICANT_TYPE);
		registrationStatusDto.setApplicantType(applicantType);

	}
}
