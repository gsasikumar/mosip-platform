package io.mosip.preregistration.booking.service;

import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.booking.code.StatusCodes;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingDTO;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.ExceptionJSONInfo;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.PreRegResponseDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterHolidayDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.dto.SlotDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingPK;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeBookedException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotAlreadyBooked;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.DemographicGetStatusException;
import io.mosip.preregistration.booking.exception.DemographicStatusUpdationException;
import io.mosip.preregistration.booking.exception.InvalidDateTimeFormatException;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.core.exceptions.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * @author M1046129
 *
 */
@Component
public class BookingService {

	private RestTemplate restTemplate;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	@Autowired
	BookingAvailabilityRepository bookingAvailabilityRepository;

	@Autowired
	@Qualifier("registrationBookingRepository")
	RegistrationBookingRepository registrationBookingRepository;

	@Value("${regCenter.url}")
	String regCenterUrl;

	@Value("${holiday.url}")
	String holidayListUrl;

	@Value("${noOfDays}")
	int noOfDays;

	@Value("${version}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	Timestamp resTime = new Timestamp(System.currentTimeMillis());
	Map<String, String> requiredRequestMap = new HashMap<>();

	@PostConstruct
	public void setupBookingService() {
		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

	}

	/**
	 * @return
	 */

	public ResponseDto<String> addAvailability() {
		ResponseDto<String> response = new ResponseDto<>();

		try {
			restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(regCenterUrl);
			LocalDate endDate = LocalDate.now().plusDays(noOfDays);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<RegistrationCenterResponseDto> entity = new HttpEntity<>(headers);
			String uriBuilder = regbuilder.build().encode().toUriString();

			ResponseEntity<RegistrationCenterResponseDto> responseEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, entity, RegistrationCenterResponseDto.class);

			List<RegistrationCenterDto> regCenter = responseEntity.getBody().getRegistrationCenters();

			if (regCenter.isEmpty()) {
				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus(false);
				response.setResponse("No data is present in registration center master table");
				return response;
			} else {
				for (RegistrationCenterDto regDto : regCenter) {
					String holidayUrl = holidayListUrl + regDto.getLanguageCode() + "/" + regDto.getId() + "/"
							+ LocalDate.now().getYear();
					UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(holidayUrl);

					HttpEntity<RegistrationCenterHolidayDto> entity2 = new HttpEntity<>(headers);

					String uriBuilder2 = builder2.build().encode().toUriString();
					ResponseEntity<RegistrationCenterHolidayDto> responseEntity2 = restTemplate.exchange(uriBuilder2,
							HttpMethod.GET, entity2, RegistrationCenterHolidayDto.class);
					List<String> holidaylist = new ArrayList<>();
					if (!responseEntity2.getBody().getHolidays().isEmpty()) {
						for (HolidayDto holiday : responseEntity2.getBody().getHolidays()) {
							holidaylist.add(holiday.getHolidayDate());
						}
					}

					for (LocalDate sDate = LocalDate.now(); (sDate.isBefore(endDate)
							|| sDate.isEqual(endDate)); sDate = sDate.plusDays(1)) {
						if (holidaylist.contains(sDate.toString())) {
							DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
							String text = "2016-11-09 00:00:00";
							LocalDateTime localDateTime = LocalDateTime.parse(text, format);
							LocalTime localTime = localDateTime.toLocalTime();
							saveAvailability(regDto, sDate, localTime, localTime);

						} else {

							int loop1 = ((regDto.getLunchStartTime().getHour() * 60
									+ regDto.getLunchStartTime().getMinute())
									- (regDto.getCenterStartTime().getHour() * 60
											+ regDto.getCenterStartTime().getMinute()))
									/ regDto.getPerKioskProcessTime().getMinute();

							int loop2 = ((regDto.getCenterEndTime().getHour() * 60
									+ regDto.getCenterEndTime().getMinute())
									- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
									/ regDto.getPerKioskProcessTime().getMinute();

							int extraTime1 = ((regDto.getLunchStartTime().getHour() * 60
									+ regDto.getLunchStartTime().getMinute())
									- (regDto.getCenterStartTime().getHour() * 60
											+ regDto.getCenterStartTime().getMinute()))
									% regDto.getPerKioskProcessTime().getMinute();

							int extraTime2 = ((regDto.getCenterEndTime().getHour() * 60
									+ regDto.getCenterEndTime().getMinute())
									- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
									% regDto.getPerKioskProcessTime().getMinute();

							LocalTime currentTime1 = regDto.getCenterStartTime();
							for (int i = 0; i < loop1; i++) {
								if (i == (loop1 - 1)) {
									LocalTime toTime = currentTime1
											.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
											.plusMinutes(extraTime1);
									saveAvailability(regDto, sDate, currentTime1, toTime);

								} else {
									LocalTime toTime = currentTime1
											.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
									saveAvailability(regDto, sDate, currentTime1, toTime);
								}
								currentTime1 = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
							}

							LocalTime currentTime2 = regDto.getLunchEndTime();
							for (int i = 0; i < loop2; i++) {
								if (i == (loop2 - 1)) {
									LocalTime toTime = currentTime2
											.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
											.plusMinutes(extraTime2);
									saveAvailability(regDto, sDate, currentTime2, toTime);

								} else {
									LocalTime toTime = currentTime2
											.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
									saveAvailability(regDto, sDate, currentTime2, toTime);
								}
								currentTime2 = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
							}
						}
					}
				}
			}
		} catch (HttpClientErrorException e) {

		} catch (DataAccessException e) {
			throw new DemographicStatusUpdationException("Table not accessable ");
		} catch (NullPointerException e) {

		}

		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(true);
		response.setResponse("Master Data is synched successfully");
		return response;

	}

	/**
	 * @param regID
	 * @return
	 */
	public ResponseDto<AvailabilityDto> getAvailability(String regID) {
		ResponseDto<AvailabilityDto> response = new ResponseDto<>();
		LocalDate endDate = LocalDate.now().plusDays(noOfDays);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		System.out.println("date " + endDate);
		try {
			List<java.sql.Date> dateList = bookingAvailabilityRepository.findDate(regID, endDate);
			if (!dateList.isEmpty()) {
				AvailabilityDto availability = new AvailabilityDto();
				List<DateTimeDto> dateTimeList = new ArrayList<>();
				for (int i = 0; i < dateList.size(); i++) {
					DateTimeDto dateTime = new DateTimeDto();
					List<AvailibityEntity> entity = bookingAvailabilityRepository.findByRegcntrIdAndRegDate(regID, dateList.get(i).toLocalDate());
					if (!entity.isEmpty()) {
						List<SlotDto> slotList = new ArrayList<>();
						for (AvailibityEntity en : entity) {
							SlotDto slots = new SlotDto();
							slots.setAvailability(en.getAvailableKiosks());
							slots.setFromTime(en.getFromTime());
							slots.setToTime(en.getToTime());
							slotList.add(slots);
						}
						if (entity.get(0).getIsActive()) {
							dateTime.setHoliday(false);
						} else {
							dateTime.setHoliday(true);
						}
						dateTime.setTimeSlots(slotList);
						dateTime.setDate(dateList.get(i).toString());
						dateTimeList.add(dateTime);
					} else {
						ExceptionJSONInfo exception = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_013.toString(), ErrorMessages.NO_SLOTS_AVAILABLE_FOR_THAT_DATE.toString());
						response.setErr(exception);
						response.setResTime(new Timestamp(System.currentTimeMillis()));
						response.setStatus(false);
						return response;
					}

				}
				availability.setCenterDetails(dateTimeList);
				availability.setRegCenterId(regID);

				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus(true);
				response.setResponse(availability);

			} else {
				ExceptionJSONInfo exception = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_014.toString(),
						ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.toString());
				response.setErr(exception);
				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus(false);

			}
		} catch (DataAccessException e) {
			throw new DemographicStatusUpdationException("Table not accessable ");
		} catch (NullPointerException e) {

		}
		return response;
	}

	private void saveAvailability(RegistrationCenterDto regDto, LocalDate date, LocalTime currentTime, LocalTime toTime)
			throws DemographicStatusUpdationException {
		AvailibityEntity avaEntity = new AvailibityEntity();
		avaEntity.setRegDate(date);
		avaEntity.setRegcntrId(regDto.getId());
		avaEntity.setFromTime(currentTime);
		avaEntity.setToTime(toTime);

		avaEntity.setCrBy(regDto.getContactPerson());
		if (currentTime.equals(toTime)) {
			avaEntity.setIsActive(false);
			avaEntity.setAvailableKiosks(0);
		} else {
			avaEntity.setAvailableKiosks(regDto.getNumberOfKiosks());
			avaEntity.setIsActive(true);
		}
		bookingAvailabilityRepository.save(avaEntity);
	}

	/**
	 * @param bookingDTO
	 * @return response with status code
	 * @throws java.text.ParseException
	 */
	@Transactional(rollbackFor = { DataAccessException.class, AppointmentBookingFailedException.class,
			BookingTimeSlotAlreadyBooked.class, AvailablityNotFoundException.class,
			AppointmentCannotBeBookedException.class })
	public ResponseDto<List<BookingStatusDTO>> bookAppointment(BookingDTO bookingDTO) {
		Map<String, String> requestMap = new HashMap<>();
		ResponseDto<List<BookingStatusDTO>> responseDTO = new ResponseDto<>();
		RegistrationBookingEntity entity = new RegistrationBookingEntity();
		RegistrationBookingPK bookingPK = new RegistrationBookingPK();
		InvalidRequestParameterException parameterException = null;
		List<BookingStatusDTO> respList = new ArrayList<>();
		try {
			requestMap.put("id", bookingDTO.getId());
			requestMap.put("ver", bookingDTO.getVer());
			requestMap.put("reqTime", bookingDTO.getReqTime());
			requestMap.put("request", bookingDTO.getRequest().toString());
			parameterException = ValidationUtil.requestValidator(requestMap, requiredRequestMap);
			if (parameterException == null) {
				for (BookingRequestDTO bookingRequestDTO : bookingDTO.getRequest()) {
					if (mandatoryParameterCheck(bookingRequestDTO)) {
						String preRegStatusCode = callGetStatusRestService(bookingRequestDTO.getPre_registration_id());
						if (preRegStatusCode != null && preRegStatusCode.trim()
								.equalsIgnoreCase(StatusCodes.Pending_Appointment.toString().trim())) {
							// booking flow
							synchronized (bookingRequestDTO) {
								AvailibityEntity availableEntity = bookingAvailabilityRepository
										.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
												LocalTime.parse(bookingRequestDTO.getSlotFromTime().toString()),
												LocalTime.parse(bookingRequestDTO.getSlotToTime().toString()),
												bookingRequestDTO.getReg_date().toString(),
												bookingRequestDTO.getRegistration_center_id());

								if (availableEntity != null && availableEntity.getAvailableKiosks() > 0) {

									boolean slotExistsFlag = registrationBookingRepository.existsByPreIdandStatusCode(
											bookingRequestDTO.getPre_registration_id(), StatusCodes.Booked.toString());

									if (!slotExistsFlag) {
										bookingPK.setPreregistrationId(bookingRequestDTO.getPre_registration_id());

										DateTimeFormatter format = DateTimeFormatter
												.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
										bookingPK.setBookingDateTime(
												LocalDateTime.parse(bookingDTO.getReqTime(), format));

										entity.setBookingPK(bookingPK);
										entity.setRegistrationCenterId(bookingRequestDTO.getRegistration_center_id());
										entity.setStatus_code(StatusCodes.Booked.toString().trim());
										entity.setLang_code("12L");
										entity.setCrBy("987654321");
										entity.setCrDate(LocalDateTime.parse(bookingDTO.getReqTime()));
										entity.setRegDate(LocalDate.parse(bookingRequestDTO.getReg_date()));
										entity.setSlotFromTime(LocalTime.parse(bookingRequestDTO.getSlotFromTime()));
										entity.setSlotToTime(LocalTime.parse(bookingRequestDTO.getSlotToTime()));

										RegistrationBookingEntity registrationBookingEntity = registrationBookingRepository
												.save(entity);

										if (registrationBookingEntity != null) {
											/* Pre registration status code update */
											callUpdateStatusRestService(bookingRequestDTO.getPre_registration_id(),
													StatusCodes.Booked.toString().trim());

											/* No. of Availability. update */
											availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() - 1);
											bookingAvailabilityRepository.update(availableEntity);

											BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();
											bookingStatusDTO
													.setPre_registration_id(bookingRequestDTO.getPre_registration_id());
											bookingStatusDTO.setBooking_status(StatusCodes.Booked.toString());
											bookingStatusDTO.setBooking_message("APPOINTMENT_SUCCESSFULLY_BOOKED");

											respList.add(bookingStatusDTO);

										} else {
											throw new AppointmentBookingFailedException(
													ErrorCodes.PRG_BOOK_RCI_005.toString(),
													ErrorMessages.APPOINTMENT_BOOKING_FAILED.toString());
										}
									} else {
										throw new BookingTimeSlotAlreadyBooked(ErrorCodes.PRG_BOOK_RCI_004.toString(),
												ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED.toString());

									}
								} else {
									throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.toString(),
											ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.toString());
								}
							}
						} else {
							throw new AppointmentCannotBeBookedException(ErrorCodes.PRG_BOOK_RCI_001.toString(),
									ErrorMessages.APPOINTMENT_CANNOT_BE_BOOKED.toString());
						}
					}

				}
				responseDTO.setStatus(true);
				responseDTO.setResTime(resTime);
				responseDTO.setErr(null);
				responseDTO.setResponse(respList);

			} else {
				throw parameterException;
			}

		} catch (DataAccessLayerException e) {
			throw new DemographicStatusUpdationException("Table not accessable");
		} catch (DateTimeException e) {
			e.printStackTrace();
			throw new InvalidDateTimeFormatException(ErrorCodes.PRG_BOOK_RCI_009.toString(),
					ErrorMessages.INVALID_DATE_TIME_FORMAT.toString());
		}
		return responseDTO;
	}

	/**
	 * @param field
	 * @return true or false
	 */
	public boolean isMandatory(String field) {
		if (field == null || field.equals(null) || field.toString().trim().length() == 0) {
			return false;
		}
		return true;

	}

	/**
	 * @param bookingDto
	 * @return true or false
	 * @throws java.text.ParseException
	 */
	public boolean mandatoryParameterCheck(BookingRequestDTO requestDTO) {
		boolean flag = true;
		try {
			if (!isMandatory(requestDTO.getPre_registration_id())) {
				throw new BookingPreIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_006.toString(),
						ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());
			} else if (!isMandatory(requestDTO.getRegistration_center_id())) {
				throw new BookingRegistrationCenterIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_007.toString(),
						ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());
			} else if (!isMandatory(requestDTO.getSlotFromTime()) && !isMandatory(requestDTO.getSlotToTime())) {
				throw new BookingTimeSlotNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_003.toString(),
						ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;

	}

	/**
	 * @param preId
	 * @param status
	 * @return response entity
	 */
	@SuppressWarnings("rawtypes")
	public ResponseEntity<ResponseDto> callUpdateStatusRestService(String preId, String status) {
		ResponseEntity<ResponseDto> resp = null;
		try {
			restTemplate = restTemplateBuilder.build();

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applications")
					.queryParam("preRegId", preId).queryParam("status", status);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<ResponseDto<String>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			resp = restTemplate.exchange(uriBuilder, HttpMethod.PUT, httpEntity, ResponseDto.class);
		} catch (RestClientException e) {
			throw new DemographicGetStatusException(ErrorCodes.PRG_BOOK_RCI_011.toString(),
					ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.toString(), e.getCause());
		}
		return resp;
	}

	/**
	 * @param preId
	 * @return status code
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String callGetStatusRestService(String preId) {

		restTemplate = restTemplateBuilder.build();
		String statusCode = "";
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applicationStatus")
					.queryParam("preId", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<PreRegResponseDto> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			ResponseEntity<PreRegResponseDto> respEntity = (ResponseEntity) restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity, PreRegResponseDto.class);
			System.out.println("respEntity.getBody() : " + respEntity);
			Map<String, String> mapValues = (Map<String, String>) respEntity.getBody().getResponse().get(0);
			statusCode = mapValues.get("statusCode").toString().trim();
		} catch (RestClientException e) {
			throw new DemographicStatusUpdationException(ErrorCodes.PRG_BOOK_RCI_012.toString(),
					ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.toString(), e.getCause());
		}
		return statusCode;
	}

}