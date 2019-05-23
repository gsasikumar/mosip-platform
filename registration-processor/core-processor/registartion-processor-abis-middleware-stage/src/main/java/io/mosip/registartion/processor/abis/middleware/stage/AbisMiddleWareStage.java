package io.mosip.registartion.processor.abis.middleware.stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.abis.queue.dto.AbisQueueDetails;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.AbisStatusCode;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorUnCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisCommonResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.CandidatesDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestPKEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetPKEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponsePKEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.utilities.RegistrationUtility;

/**
 * 
 * @author Girish Yarru
 * @since v1.0
 *
 */
public class AbisMiddleWareStage extends MosipVerticleManager {
	private static Logger regProcLogger = RegProcessorLogger.getLogger(AbisMiddleWareStage.class);

	/** The mosip queue manager. */
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private BasePacketRepository<AbisRequestEntity, String> abisRequestRepositary;

	@Autowired
	private BasePacketRepository<AbisResponseEntity, String> abisResponseRepositary;

	@Autowired
	private BasePacketRepository<AbisResponseDetEntity, String> abisResponseDetailRepositary;

	@Autowired
	private Utilities utility;

	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	private PacketInfoDao packetInfoDao;

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;
	/** The url. */
	private static final String SYSTEM = "SYSTEM";
	private Map<Integer, String> failureReason = new HashMap<>();
	private List<AbisQueueDetails> abisQueueDetails;
	private InternalRegistrationStatusDto internalRegDto;
	private String registrationId;
	private static final String REQUESTID = "requestId";
	private static final String DEMOGRAPHIC_VERIFICATION = "DEMOGRAPHIC_VERIFICATION";
	private static final String BIOGRAPHIC_VERIFICATION = "BIOGRAPHIC_VERIFICATION";
	private static final String FOR_REQUESTID = "for resquestId ";

	/**
	 * Get all the abis queue details,register listener to outbound queue's
	 */
	public void deployVerticle() {
		try {
			MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
			this.consume(mosipEventBus, MessageBusAddress.ABIS_MIDDLEWARE_BUS_IN);
			abisQueueDetails = utility.getAbisQueueDetails();
			for (AbisQueueDetails abisQueue : abisQueueDetails) {
				String abisInBoundaddress = abisQueue.getInboundQueueName();
				MosipQueue queue = abisQueue.getMosipQueue();
				QueueListener listener = new QueueListener() {
					@Override
					public void setListener(Message message) {
						try {
							consumerListener(message, abisInBoundaddress, queue, mosipEventBus);
						} catch (Exception e) {

							regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
									LoggerFileConstant.REGISTRATIONID.toString(), "", ExceptionUtils.getStackTrace(e));

						}
					}
				};
				mosipQueueManager.consume(queue, abisQueue.getOutboundQueueName(), listener);

			}

		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, ExceptionUtils.getStackTrace(e));
			throw new RegistrationProcessorUnCheckedException(PlatformErrorMessages.UNKNOWN_EXCEPTION_OCCURED.getCode(),
					PlatformErrorMessages.UNKNOWN_EXCEPTION_OCCURED.getMessage(), e);

		}
	}

	@Override
	public MessageDTO process(MessageDTO object) {

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisMiddlewareStage::process()::entry");
		object.setMessageBusAddress(MessageBusAddress.ABIS_MIDDLEWARE_BUS_IN);
		object.setIsValid(false);
		object.setInternalError(false);
		boolean isTransactionSuccessful = false;
		String description = "";
		registrationId = object.getRid();

		String exceptionMesaage = "";
		try {
			internalRegDto = registrationStatusService.getRegistrationStatus(registrationId);
			List<String> abisRefList = packetInfoManager.getReferenceIdByRid(registrationId);
			if (abisRefList == null || abisRefList != null && abisRefList.isEmpty()) {
				exceptionMesaage = "Abis reference id not found";
				throw new RegistrationProcessorUnCheckedException(
						PlatformErrorMessages.ABIS_REFERENCE_ID_NOT_FOUND.getCode(),
						PlatformErrorMessages.ABIS_REFERENCE_ID_NOT_FOUND.getMessage());
			}

			String refRegtrnId = getLatestTransactionId(registrationId);
			if (refRegtrnId == null) {
				exceptionMesaage = "latest transactionId not found";
				throw new RegistrationProcessorUnCheckedException(
						PlatformErrorMessages.LATEST_TRANSACTION_ID_NOT_FOUND.getCode(),
						PlatformErrorMessages.LATEST_TRANSACTION_ID_NOT_FOUND.getMessage());
			}

			String abisRefId = abisRefList.get(0);
			List<AbisRequestDto> abisInsertIdentifyList = packetInfoManager.getInsertOrIdentifyRequest(abisRefId,
					refRegtrnId);
			if (abisInsertIdentifyList.isEmpty()) {
				exceptionMesaage = "Identify requests not found for abisrefId" + abisRefId;
				throw new RegistrationProcessorUnCheckedException(
						PlatformErrorMessages.IDENTIFY_REQUESTS_NOT_FOUND.getCode(),
						PlatformErrorMessages.IDENTIFY_REQUESTS_NOT_FOUND.getMessage());
			}
			// get all insert requests(already processed,in progress)
			List<AbisRequestDto> abisInsertRequestList = abisInsertIdentifyList.stream()
					.filter(dto -> dto.getRequestType().equals(AbisStatusCode.INSERT.toString()))
					.collect(Collectors.toList());
			List<AbisRequestDto> abisInprogressInsertRequestList = abisInsertRequestList.stream()
					.filter(dto -> dto.getStatusCode().equals(AbisStatusCode.IN_PROGRESS.toString()))
					.collect(Collectors.toList());
			List<AbisRequestDto> abisAlreadyprocessedInsertRequestList = abisInsertRequestList.stream()
					.filter(dto -> dto.getStatusCode().equals(AbisStatusCode.ALREADY_PROCESSED.toString()))
					.collect(Collectors.toList());
			List<AbisRequestDto> abisIdentifyRequestList = abisInsertIdentifyList.stream()
					.filter(dto -> dto.getRequestType().equals(AbisStatusCode.IDENTIFY.toString()))
					.collect(Collectors.toList());
			// If all insert request are null then send all identify requests.
			if (abisInsertRequestList == null || abisInsertRequestList != null && abisInsertRequestList.isEmpty()) {
				for (AbisRequestDto abisIdentifyRequest : abisIdentifyRequestList) {
					List<AbisQueueDetails> abisQueue = abisQueueDetails.stream()
							.filter(dto -> dto.getName().equals(abisIdentifyRequest.getAbisAppCode()))
							.collect(Collectors.toList());

					byte[] reqBytearray = abisIdentifyRequest.getReqText();

					boolean isAddedToQueue = sendToQueue(abisQueue.get(0).getMosipQueue(), new String(reqBytearray),
							abisQueue.get(0).getInboundQueueName());

					updateAbisRequest(isAddedToQueue, abisIdentifyRequest);
				}

			}
			// send in progress insert requests to queue
			for (AbisRequestDto abisInprogressRequest : abisInprogressInsertRequestList) {
				List<AbisQueueDetails> abisQueue = abisQueueDetails.stream()
						.filter(dto -> dto.getName().equals(abisInprogressRequest.getAbisAppCode()))
						.collect(Collectors.toList());

				byte[] reqBytearray = abisInprogressRequest.getReqText();

				boolean isAddedToQueue = sendToQueue(abisQueue.get(0).getMosipQueue(), new String(reqBytearray),
						abisQueue.get(0).getInboundQueueName());

				updateAbisRequest(isAddedToQueue, abisInprogressRequest);
			}
			// send all identify requests for already processed insert requests
			for (AbisRequestDto abisAlreadyProcessedInsertRequest : abisAlreadyprocessedInsertRequestList) {
				List<AbisQueueDetails> abisQueue = abisQueueDetails.stream()
						.filter(dto -> dto.getName().equals(abisAlreadyProcessedInsertRequest.getAbisAppCode()))
						.collect(Collectors.toList());

				List<AbisRequestDto> identifyRequest = abisIdentifyRequestList.stream()
						.filter(dto -> dto.getAbisAppCode().equals(abisAlreadyProcessedInsertRequest.getAbisAppCode()))
						.collect(Collectors.toList());
				byte[] reqBytearray = identifyRequest.get(0).getReqText();
				boolean isAddedToQueue = sendToQueue(abisQueue.get(0).getMosipQueue(), new String(reqBytearray),
						abisQueue.get(0).getInboundQueueName());
				updateAbisRequest(isAddedToQueue, abisAlreadyProcessedInsertRequest);

			}
			object.setIsValid(true);
			object.setInternalError(false);
			isTransactionSuccessful = true;
			description = "Abis insertRequests sucessfully sent to Queue";
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
					"AbisMiddlewareStage::process()::Abis insertRequests sucessfully sent to Queue");
		} catch (RegistrationProcessorUnCheckedException | RegistrationProcessorCheckedException e) {
			object.setInternalError(true);
			object.setIsValid(false);
			description = exceptionMesaage;
			internalRegDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
			internalRegDto.setStatusComment(exceptionMesaage);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, ExceptionUtils.getStackTrace(e));
		} catch (Exception e) {
			object.setInternalError(true);
			object.setIsValid(false);
			description = exceptionMesaage;
			internalRegDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
			internalRegDto.setStatusComment("Unknown exception occured in abis middle ware");
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, ExceptionUtils.getStackTrace(e));
		} finally {
			if (!isTransactionSuccessful) {
				String transactionTypeCode = "";
				if (transactionTypeCode.equalsIgnoreCase(DEMOGRAPHIC_VERIFICATION)) {
					internalRegDto.setRegistrationStageName("DemoDedupeStage");
				} else if (transactionTypeCode.equalsIgnoreCase(BIOGRAPHIC_VERIFICATION)) {
					internalRegDto.setRegistrationStageName("BioDedupeStage");
				}
				registrationStatusService.updateRegistrationStatus(internalRegDto);
			}

			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			String moduleId = isTransactionSuccessful ? "Abis-MiddleWare Success" : "";
			String moduleName = "Abis-MiddleWare";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisMiddlewareStage::process()::Exit");
		return object;
	}

	public void consumerListener(Message message, String abisInBoundAddress, MosipQueue queue, MosipEventBus eventBus)
			throws RegistrationProcessorCheckedException {
		InternalRegistrationStatusDto internalRegStatusDto;

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisMiddlewareStage::consumerListener()::entry");

		String response = new String(((ActiveMQBytesMessage) message).getContent().data);

		try {
			JSONObject inserOrIdentifyResponse = JsonUtil.objectMapperReadValue(response, JSONObject.class);
			String requestId = JsonUtil.getJSONValue(inserOrIdentifyResponse, REQUESTID);
			String batchId = packetInfoManager.getBatchIdByRequestId(requestId);
			if (batchId == null)
				throw new RegistrationProcessorUnCheckedException(
						PlatformErrorMessages.ABIS_BATCH_ID_NOT_FOUND.getCode(),
						PlatformErrorMessages.ABIS_BATCH_ID_NOT_FOUND.getMessage() + FOR_REQUESTID + requestId);

			List<String> bioRefId = packetInfoManager.getReferenceIdByBatchId(batchId);
			if (bioRefId == null || bioRefId.isEmpty())
				throw new RegistrationProcessorUnCheckedException(
						PlatformErrorMessages.ABIS_REFERENCE_ID_NOT_FOUND.getCode(),
						PlatformErrorMessages.ABIS_REFERENCE_ID_NOT_FOUND.getMessage() + FOR_REQUESTID + requestId);

			List<String> registrationIds = packetInfoDao.getAbisRefRegIdsByMatchedRefIds(bioRefId);
			internalRegStatusDto = registrationStatusService.getRegistrationStatus(registrationIds.get(0));

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
					"AbisMiddlewareStage::consumerListener()::response from abis for requestId ::" + requestId);

			AbisRequestDto abisCommonRequestDto = packetInfoManager.getAbisRequestByRequestId(requestId);
			// check for insert response,if success send corresponding identify request to
			// queue
			if (abisCommonRequestDto.getRequestType().equals(AbisStatusCode.INSERT.toString())) {
				AbisInsertResponseDto abisInsertResponseDto = JsonUtil.objectMapperReadValue(response,
						AbisInsertResponseDto.class);

				updateAbisResponseEntity(abisInsertResponseDto, response);
				updteAbisRequestProcessed(abisInsertResponseDto, abisCommonRequestDto);
				if (abisInsertResponseDto.getReturnValue() == 1) {
					List<String> transactionIdList = packetInfoManager.getAbisTransactionIdByRequestId(requestId);
					List<AbisRequestDto> abisIdentifyRequestList = packetInfoManager.getIdentifyReqListByTransactionId(
							transactionIdList.get(0), AbisStatusCode.IDENTIFY.toString());
					validateAbisIdentifyRequestList(abisIdentifyRequestList);
					List<AbisRequestDto> abisIdentifyRequest = abisIdentifyRequestList.stream()
							.filter(dto1 -> dto1.getAbisAppCode().equals(abisCommonRequestDto.getAbisAppCode()))
							.collect(Collectors.toList());
					AbisRequestDto abisIdentifyRequestDto = abisIdentifyRequest.get(0);
					boolean isAddedToQueue = sendToQueue(queue, new String(abisIdentifyRequestDto.getReqText()),
							abisInBoundAddress);
					updateAbisRequest(isAddedToQueue, abisIdentifyRequestDto);
				} else {
					internalRegDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
					internalRegDto
							.setStatusComment("Insert response failed for request Id" + abisCommonRequestDto.getId());
					registrationStatusService.updateRegistrationStatus(internalRegDto);
				}
			}
			// check if identify response,then if all identify requests are processed send
			// to abis handler
			if (abisCommonRequestDto.getRequestType().equals(AbisStatusCode.IDENTIFY.toString())) {

				AbisIdentifyResponseDto abisIdentifyResponseDto = JsonUtil.objectMapperReadValue(response,
						AbisIdentifyResponseDto.class);
				if (abisIdentifyResponseDto.getReturnValue() != 1) {
					internalRegDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
					internalRegDto
							.setStatusComment("Insert response failed for request Id" + abisCommonRequestDto.getId());
					registrationStatusService.updateRegistrationStatus(internalRegDto);
				}
				AbisResponseDto abisResponseDto = updateAbisResponseEntity(abisIdentifyResponseDto, response);
				if (abisIdentifyResponseDto.getCandidateList() != null) {
					CandidatesDto[] candidatesDtos = abisIdentifyResponseDto.getCandidateList().getCandidates();
					saveCandiateDtos(candidatesDtos, abisResponseDto);
				}

				updteAbisRequestProcessed(abisIdentifyResponseDto, abisCommonRequestDto);

				if (checkAllIdentifyRequestsProcessed(batchId)) {

					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
							"",
							"AbisMiddlewareStage::consumerListener()::All identify are requests processed sending to Abis handler");

					sendToAbisHandler(eventBus, bioRefId, registrationIds.get(0),
							internalRegStatusDto.getRegistrationType());

				}

			}

		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, ExceptionUtils.getStackTrace(e));
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		} catch (Exception e) {
			if (internalRegDto != null) {
				internalRegDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
				internalRegDto.setStatusComment("Unknown exception occured while consuming message from Abis");
				registrationStatusService.updateRegistrationStatus(internalRegDto);
			}

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, ExceptionUtils.getStackTrace(e));
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisMiddlewareStage::consumerListener()::Exit()");
	}

	private void validateAbisIdentifyRequestList(List<AbisRequestDto> abisIdentifyRequestList) {
		if (abisIdentifyRequestList == null || abisIdentifyRequestList.isEmpty()) {
			throw new RegistrationProcessorUnCheckedException(
					PlatformErrorMessages.IDENTIFY_REQUESTS_NOT_FOUND.getCode(),
					PlatformErrorMessages.IDENTIFY_REQUESTS_NOT_FOUND.getMessage());
		}
	}

	private boolean sendToQueue(MosipQueue queue, String abisReqTextString, String abisQueueAddress)
			throws RegistrationProcessorCheckedException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisMiddlewareStage::sendToQueue()::Entry");
		boolean isAddedToQueue;
		try {
			isAddedToQueue = mosipQueueManager.send(queue, abisReqTextString.getBytes(), abisQueueAddress);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
					"AbisMiddlewareStage:: sent to abis queue ::" + abisReqTextString);

		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, ExceptionUtils.getStackTrace(e));
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisMiddlewareStage::sendToQueue()::Exit");
		return isAddedToQueue;
	}

	private void updateAbisRequest(boolean isAddedToQueue, AbisRequestDto abisRequestDto) {
		AbisRequestEntity abisReqEntity = convertAbisRequestDtoToAbisRequestEntity(abisRequestDto);

		if (isAddedToQueue) {

			abisReqEntity.setStatusCode(AbisStatusCode.SENT.toString());
			abisReqEntity.setStatusComment("Request sent to ABIS is sucessful");
		} else {
			abisReqEntity.setStatusCode(AbisStatusCode.FAILED.toString());
			abisReqEntity.setStatusComment("Request sent to ABIS is unsucessful");
			internalRegDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
			internalRegDto.setStatusComment(
					"Insert/Identify Request sent is unsucessful for abis " + abisRequestDto.getAbisAppCode());
		}
		abisRequestRepositary.save(abisReqEntity);

	}

	private void updteAbisRequestProcessed(AbisCommonResponseDto abisCommonResponseDto,
			AbisRequestDto abisCommonRequestDto) {
		AbisRequestEntity abisReqEntity = new AbisRequestEntity();
		AbisRequestPKEntity abisReqPKEntity = new AbisRequestPKEntity();
		abisReqPKEntity.setId(abisCommonResponseDto.getRequestId());
		abisReqEntity.setId(abisReqPKEntity);
		abisReqEntity.setStatusCode(abisCommonResponseDto.getReturnValue() == 1 ? AbisStatusCode.PROCESSED.toString()
				: AbisStatusCode.FAILED.toString());
		abisReqEntity
				.setStatusComment(abisCommonResponseDto.getReturnValue() == 1 ? "Received sucessful response from abis"
						: getFaliureReason(abisCommonResponseDto.getFailureReason()));
		abisReqEntity.setAbisAppCode(abisCommonRequestDto.getAbisAppCode());
		abisReqEntity.setRequestType(abisCommonRequestDto.getRequestType());
		abisReqEntity.setRequestDtimes(abisCommonRequestDto.getRequestDtimes());
		abisReqEntity.setReqBatchId(abisCommonRequestDto.getReqBatchId());
		abisReqEntity.setLangCode(abisCommonRequestDto.getLangCode());
		abisReqEntity.setCrBy(SYSTEM);
		abisReqEntity.setCrDtimes(abisCommonRequestDto.getCrDtimes());
		abisReqEntity.setBioRefId(abisCommonRequestDto.getBioRefId());
		abisReqEntity.setRefRegtrnId(abisCommonRequestDto.getRefRegtrnId());
		abisReqEntity.setReqText(abisCommonRequestDto.getReqText());

		abisRequestRepositary.save(abisReqEntity);
	}

	private AbisRequestEntity convertAbisRequestDtoToAbisRequestEntity(AbisRequestDto abisRequestDto) {
		AbisRequestEntity abisReqEntity = new AbisRequestEntity();
		AbisRequestPKEntity abisReqPKEntity = new AbisRequestPKEntity();
		abisReqPKEntity.setId(abisRequestDto.getId());
		abisReqEntity.setId(abisReqPKEntity);
		abisReqEntity.setAbisAppCode(abisRequestDto.getAbisAppCode());
		abisReqEntity.setBioRefId(abisRequestDto.getBioRefId());
		abisReqEntity.setCrBy(abisRequestDto.getCrBy());
		abisReqEntity.setIsDeleted(false);
		abisReqEntity.setLangCode(abisRequestDto.getLangCode());
		abisReqEntity.setRefRegtrnId(abisRequestDto.getRefRegtrnId());
		abisReqEntity.setReqBatchId(abisRequestDto.getReqBatchId());
		abisReqEntity.setReqText(abisRequestDto.getReqText());
		abisReqEntity.setRequestDtimes(abisRequestDto.getRequestDtimes());
		abisReqEntity.setRequestType(abisRequestDto.getRequestType());
		abisReqEntity.setStatusCode(abisRequestDto.getStatusCode());
		abisReqEntity.setStatusComment(abisRequestDto.getStatusComment());
		abisReqEntity.setUpdBy(abisRequestDto.getUpdBy());

		return abisReqEntity;

	}

	private AbisResponseEntity convertAbisResponseDtoToAbisResponseEntity(AbisResponseDto abisResponseDto) {
		AbisResponseEntity abisResponseEntity = new AbisResponseEntity();
		AbisResponsePKEntity abisResponsePKEntity = new AbisResponsePKEntity();
		abisResponsePKEntity.setId(abisResponseDto.getId());
		abisResponseEntity.setId(abisResponsePKEntity);
		abisResponseEntity.setAbisRequest(abisResponseDto.getAbisRequest());
		abisResponseEntity.setRespText(abisResponseDto.getRespText());
		abisResponseEntity.setStatusCode(abisResponseDto.getStatusCode());
		abisResponseEntity.setStatusComment(abisResponseDto.getStatusComment());
		abisResponseEntity.setLangCode(abisResponseDto.getLangCode());
		abisResponseEntity.setCrBy(abisResponseDto.getCrBy());
		abisResponseEntity.setUpdBy(abisResponseDto.getUpdBy());
		abisResponseEntity.setIsDeleted(abisResponseDto.getIsDeleted());

		return abisResponseEntity;

	}

	/**
	 * get the failure reason for given key
	 * 
	 * @param key
	 * @return
	 */
	private String getFaliureReason(Integer key) {
		if (key == null)
			return null;
		failureReason.put(1, "Internal error - Unknown");
		failureReason.put(2, "Aborted");
		failureReason.put(3, "Unexpected error - Unable to access biometric data");
		failureReason.put(4, "Unable to serve the request");
		failureReason.put(5, "Invalid request / Missing mandatory fields");
		failureReason.put(6, "Unauthorized Access");
		failureReason.put(7, "Unable to fetch biometric details");
		return failureReason.get(key);

	}

	private AbisResponseDto updateAbisResponseEntity(AbisCommonResponseDto abisCommonResponseDto, String response) {
		AbisResponseDto abisResponseDto = new AbisResponseDto();

		abisResponseDto.setId(RegistrationUtility.generateId());
		abisResponseDto.setRespText(response.getBytes());
		int responseStatus = abisCommonResponseDto.getReturnValue();

		abisResponseDto.setStatusCode(
				responseStatus == 1 ? AbisStatusCode.SUCCESS.toString() : AbisStatusCode.FAILED.toString());
		abisResponseDto.setStatusComment(getFaliureReason(abisCommonResponseDto.getFailureReason()));
		abisResponseDto.setLangCode("eng");
		abisResponseDto.setCrBy(SYSTEM);
		abisResponseDto.setUpdBy(SYSTEM);
		abisResponseDto.setIsDeleted(false);
		abisResponseDto.setAbisRequest(abisCommonResponseDto.getRequestId());
		abisResponseRepositary.save(convertAbisResponseDtoToAbisResponseEntity(abisResponseDto));

		return abisResponseDto;
	}

	private void updateAbisResponseDetail(CandidatesDto candidatesDto, AbisResponseDto abisResponseDto) {
		AbisResponseDetEntity abisResponseDetEntity = new AbisResponseDetEntity();
		AbisResponseDetPKEntity abisResponseDetPKEntity = new AbisResponseDetPKEntity();
		abisResponseDetPKEntity.setAbisRespId(abisResponseDto.getId());
		abisResponseDetPKEntity.setMatchedBioRefId(candidatesDto.getReferenceId());
		abisResponseDetEntity.setId(abisResponseDetPKEntity);
		if (candidatesDto.getScaledScore() != null) {
			abisResponseDetEntity.setScore(Integer.valueOf(candidatesDto.getScaledScore()));
		} else {
			abisResponseDetEntity.setScore(null);

		}
		abisResponseDetEntity.setCrBy(SYSTEM);
		abisResponseDetEntity.setUpdBy(SYSTEM);
		abisResponseDetEntity.setIsDeleted(false);
		abisResponseDetailRepositary.save(abisResponseDetEntity);

	}

	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

	private boolean checkAllIdentifyRequestsProcessed(String batchId) {
		List<String> batchStatus = packetInfoManager.getBatchStatusbyBatchId(batchId);
		if (batchStatus != null) {
			boolean flag = batchStatus.stream().allMatch(status -> status.equals(AbisStatusCode.PROCESSED.toString()));
			if (flag)
				return true;
		}
		return false;
	}

	private void sendToAbisHandler(MosipEventBus eventBus, List<String> bioRefId, String regId, String regType) {
		if (bioRefId != null) {
			MessageDTO messageDto = new MessageDTO();
			messageDto.setRid(regId);
			messageDto.setReg_type(regType);
			this.send(eventBus, MessageBusAddress.ABIS_HANDLER_BUS_IN, messageDto);
		}

	}

	private void saveCandiateDtos(CandidatesDto[] candidatesDtos, AbisResponseDto abisResponseDto) {
		for (CandidatesDto candidatesDto : candidatesDtos) {
			updateAbisResponseDetail(candidatesDto, abisResponseDto);
		}
	}

}
