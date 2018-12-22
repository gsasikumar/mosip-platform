/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.code.StatusCodes;
import io.mosip.preregistration.application.dto.BookingRegistrationDTO;
import io.mosip.preregistration.application.dto.BookingResponseDTO;
import io.mosip.preregistration.application.dto.CreateDemographicDTO;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.dto.ResponseDTO;
import io.mosip.preregistration.application.dto.UpdateResponseDTO;
import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.util.DemographicExceptionCatcher;
import io.mosip.preregistration.application.repository.DemographicRepository;
import io.mosip.preregistration.application.service.util.DemographicServiceUtil;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * This class provides the service implementation for Demographic
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 */
@Service
public class DemographicService {
	/**
	 * Autowired reference for {@link #MosipPridGenerator<String>}
	 */
	@Autowired
	private PridGenerator<String> pridGenerator;

	/**
	 * Autowired reference for {@link #RegistrationRepositary}
	 */
	@Autowired
	private DemographicRepository demographicRepository;

	/**
	 * Autowired reference for {@link #DemographicServiceUtil}
	 */
	@Autowired
	private DemographicServiceUtil serviceUtil;

	/**
	 * Autowired reference for {@link #JsonValidatorImpl}
	 */
	@Autowired
	private JsonValidatorImpl jsonValidator;

	/**
	 * Autowired reference for {@link #RestTemplateBuilder}
	 */
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Value("${resource.url}")
	private String resourceUrl;

	@Value("${id}")
	private String id;

	@Value("${ver}")
	private String ver;

	@Value("${appointmentResourse.url}")
	private String appointmentResourseUrl;

	protected String trueStatus = "true";

	Map<String, String> requiredRequestMap = new HashMap<>();

	/**
	 * This method acts as a post constructor to initialize the required request
	 * parameters.
	 */
	@PostConstruct
	public void setup() {
		requiredRequestMap.put("id", id);
		requiredRequestMap.put("ver", ver);
	}

	/*
	 * This method is used to create the demographic data by generating the unique
	 * PreId
	 * 
	 * @see
	 * io.mosip.registration.service.RegistrationService#addPreRegistration(java.
	 * lang.Object, java.lang.String)
	 * 
	 * @param demographicRequest
	 * 
	 * @return responseDTO
	 */
	public ResponseDTO<CreateDemographicDTO> addPreRegistration(
			DemographicRequestDTO<CreateDemographicDTO> demographicRequest) {
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(demographicRequest), requiredRequestMap)) {
				jsonValidator.validateJson(demographicRequest.getRequest().getDemographicDetails().toJSONString(),
						"mosip-prereg-identity-json-schema.json");
				return createOrUpdate(demographicRequest.getRequest(), demographicRequest.getId());
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		return null;
	}

	/**
	 * This Method is used to fetch all the applications created by User
	 * 
	 * @param userId
	 *            pass a userId through which user has logged in which can be either
	 *            email Id or phone number
	 * @return List of groupIds
	 * 
	 */
	public ResponseDTO<PreRegistrationViewDTO> getAllApplicationDetails(String userId) {
		ResponseDTO<PreRegistrationViewDTO> response = new ResponseDTO<>();
		List<PreRegistrationViewDTO> viewList = new ArrayList<>();
		PreRegistrationViewDTO viewDto = null;
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.userId.toString(), userId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				List<DemographicEntity> demographicEntityList = demographicRepository.findByuserId(userId);
				if (!serviceUtil.isNull(demographicEntityList)) {
					for (DemographicEntity demographicEntity : demographicEntityList) {
						String identityValue = serviceUtil.getValueFromIdentity(
								demographicEntity.getApplicantDetailJson(), RequestCodes.FullName.toString());
						viewDto = new PreRegistrationViewDTO();
						viewDto.setPreId(demographicEntity.getPreRegistrationId());
						viewDto.setFullname(identityValue);
						viewDto.setStatusCode(demographicEntity.getStatusCode());

						BookingRegistrationDTO bookingRegistrationDTO = callGetAppointmentDetailsRestService(
								demographicEntity.getPreRegistrationId());
						if (bookingRegistrationDTO != null) {
							viewDto.setBookingRegistrationDTO(bookingRegistrationDTO);
						}
						viewList.add(viewDto);
					}
					response.setResponse(viewList);
					response.setResTime(new Timestamp(System.currentTimeMillis()));
					response.setStatus(trueStatus);
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.name());
				}
			}

		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		return response;
	}

	/**
	 * This Method is used to fetch status of particular preId
	 * 
	 * @param preId
	 * @return ResponseDto<StatusDto>
	 * 
	 * 
	 */
	public ResponseDTO<PreRegistartionStatusDTO> getApplicationStatus(String preRegId) {
		PreRegistartionStatusDTO statusdto = new PreRegistartionStatusDTO();
		ResponseDTO<PreRegistartionStatusDTO> response = new ResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				if (demographicEntity != null) {
					statusdto.setPreRegistartionId(demographicEntity.getPreRegistrationId());
					statusdto.setStatusCode(demographicEntity.getStatusCode());
					statusList.add(statusdto);
					response.setResponse(statusList);
					response.setResTime(new Date(System.currentTimeMillis()));
					response.setStatus(trueStatus);
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.name());
				}
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		return response;
	}

	/**
	 * This Method is used to delete the Individual Application and documents
	 * associated with it
	 * 
	 * @param preregId
	 * @return ResponseDto<DeleteDto>
	 */
	public ResponseDTO<DeletePreRegistartionDTO> deleteIndividual(String preregId) {
		ResponseDTO<DeletePreRegistartionDTO> response = new ResponseDTO<>();
		List<DeletePreRegistartionDTO> deleteList = new ArrayList<>();
		DeletePreRegistartionDTO deleteDto = new DeletePreRegistartionDTO();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preregId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preregId);
				if (!serviceUtil.isNull(demographicEntity)) {
					if (serviceUtil.checkStatusForDeletion(demographicEntity.getStatusCode())) {
						callDocumentServiceToDeleteAllByPreId(preregId);
						int isDeletedDemo = demographicRepository.deleteByPreRegistrationId(preregId);
						if (isDeletedDemo > 0) {
							deleteDto.setPrId(demographicEntity.getPreRegistrationId());
							deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));
							deleteList.add(deleteDto);
						} else {
							throw new RecordFailedToDeleteException(ErrorCodes.PRG_PAM_APP_004.name(),
									ErrorMessages.FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD.name());
						}
					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
				}
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResTime(new Date(System.currentTimeMillis()));
		response.setStatus("true");
		response.setResponse(deleteList);
		return response;
	}

	/**
	 * This Method is used to retrieve the demographic
	 * 
	 * @param preregId
	 * @return ResponseDto<CreatePreRegistrationDTO>
	 */
	public ResponseDTO<CreateDemographicDTO> getDemographicData(String preRegId) {
		List<CreateDemographicDTO> createDtos = new ArrayList<>();
		ResponseDTO<CreateDemographicDTO> response = new ResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				CreateDemographicDTO createDto = serviceUtil.setterForCreateDTO(demographicEntity);
				createDtos.add(createDto);
				response.setResponse(createDtos);
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResTime(new Date(System.currentTimeMillis()));
		response.setStatus("true");
		response.setErr(null);
		return response;
	}

	/**
	 * This Method is used to update status of particular preId
	 * 
	 * @param preId
	 * @param preId
	 * @return UpdateResponseDTO<String>
	 * 
	 * 
	 */
	public UpdateResponseDTO<String> updatePreRegistrationStatus(String preRegId, String status) {
		UpdateResponseDTO<String> response = new UpdateResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preRegId);
			requestParamMap.put(RequestCodes.statusCode.toString(), status);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				demographicEntity.setStatusCode(StatusCodes.valueOf(status).toString());
				demographicRepository.update(demographicEntity);
				response.setResponse("Status Updated sucessfully");
				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus("true");
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		return response;
	}

	/**
	 * This Method is used to retrieve demographic data by date
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return UpdateResponseDTO<String>
	 * 
	 * 
	 */
	public ResponseDTO<String> getPreRegistrationByDate(String fromDate, String toDate) {
		ResponseDTO<String> response = new ResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		Map<String, String> reqDateRange = new HashMap<>();
		try {
			reqDateRange.put("FromDate", fromDate);
			reqDateRange.put("ToDate", toDate);
			if (ValidationUtil.requstParamValidator(reqDateRange)) {
				Map<String, Timestamp> reqTimeStamp = serviceUtil.dateSetter(reqDateRange, "yyyy-MM-dd HH:mm:ss");
				List<DemographicEntity> details = demographicRepository
						.findBycreateDateTimeBetween(reqTimeStamp.get("FromDate"), reqTimeStamp.get("ToDate"));
				for (DemographicEntity entity : details) {
					preIds.add(entity.getPreRegistrationId());
				}
				response.setResponse(preIds);
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResTime(new Date(System.currentTimeMillis()));
		response.setStatus("true");
		response.setErr(null);
		return response;
	}

	/**
	 * This private Method is used to retrieve booking data by date
	 * 
	 * @param preId
	 * @return BookingRegistrationDTO
	 * 
	 */
	private BookingRegistrationDTO callGetAppointmentDetailsRestService(String preId) {
		RestTemplate restTemplate = restTemplateBuilder.build();
		BookingResponseDTO<?> resultDto = null;
		BookingRegistrationDTO bookingRegistrationDTO = null;
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(appointmentResourseUrl)
					.queryParam("preRegID", preId);
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<BookingRegistrationDTO> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			ResponseEntity<?> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					BookingResponseDTO.class);
			if (respEntity.getStatusCode() == HttpStatus.OK) {
				resultDto = (BookingResponseDTO<?>) respEntity.getBody();
				if(!serviceUtil.isNull(resultDto)) {
					ObjectMapper mapper = new ObjectMapper();
				    bookingRegistrationDTO = mapper.convertValue(resultDto.getResponse(), BookingRegistrationDTO.class);
				}
			}
		} catch (RestClientException e) {
			return bookingRegistrationDTO;
		}

		return bookingRegistrationDTO;
	}

	/**
	 * This private Method is used to save and update demographic data
	 * 
	 * @param CreateDemographicDTO
	 * @param requestId
	 * @return ResponseDTO<CreatePreRegistrationDTO>
	 * 
	 */
	private ResponseDTO<CreateDemographicDTO> createOrUpdate(CreateDemographicDTO demographicRequest,
			String requestId) {
		ResponseDTO<CreateDemographicDTO> response = new ResponseDTO<>();
		List<CreateDemographicDTO> saveList = new ArrayList<>();
		if (serviceUtil.isNull(demographicRequest.getPreRegistrationId())) {
			demographicRequest.setPreRegistrationId(pridGenerator.generateId());
			DemographicEntity demographicEntity = serviceUtil.prepareDemographicEntity(demographicRequest, requestId,
					"save");
			demographicRepository.save(demographicEntity);
		} else {
			DemographicEntity demographicEntity = demographicRepository
					.findBypreRegistrationId(demographicRequest.getPreRegistrationId());
			if (!serviceUtil.isNull(demographicEntity)) {
				demographicEntity = serviceUtil.prepareDemographicEntity(demographicRequest, requestId, "update");
				demographicRepository.deleteByPreRegistrationId(demographicRequest.getPreRegistrationId());
				demographicRepository.save(demographicEntity);
			} else {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
						ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
			}
		}
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(trueStatus);
		saveList.add(demographicRequest);
		response.setResponse(saveList);
		return response;
	}

	/**
	 * This private Method is used to call rest service to delete document by preId
	 * 
	 * @param preregId
	 * @return boolean
	 * 
	 */
	private boolean callDocumentServiceToDeleteAllByPreId(String preregId) {
		ResponseEntity<?> responseEntity = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(resourceUrl + "pre-registration/deleteAllByPreRegId").queryParam("preId", preregId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<ResponseDTO<?>> httpEntity = new HttpEntity<>(headers);

			String strUriBuilder = uriBuilder.build().encode().toUriString();

			responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE, httpEntity, ResponseDTO.class);

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return true;
			} else {
				throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
						ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
			}
		} catch (RestClientException e) {
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
		}
	}
}