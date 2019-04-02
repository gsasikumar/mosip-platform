package io.mosip.preregistration.booking.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.service.BookingService;
import io.mosip.preregistration.booking.service.util.BookingServiceUtil;
import io.mosip.preregistration.booking.test.BookingApplicationTest;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DeleteBookingDTO;
import io.mosip.preregistration.core.common.dto.MainListRequestDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/**
 * Booking Controller Test
 * 
 * @author Kishan Rathore
 *
 */
@SpringBootTest(classes = { BookingApplicationTest.class })
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookingService service;

	@MockBean
	private BookingServiceUtil serviceUtil;

	private AvailabilityDto availabilityDto;

	MainListRequestDTO bookingDTO = new MainListRequestDTO();
	List<BookingRequestDTO> bookingList = new ArrayList<>();
	BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
	BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
	BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
	Timestamp resTime = new Timestamp(System.currentTimeMillis());
	@SuppressWarnings("rawtypes")
	MainResponseDTO responseDto = new MainResponseDTO();
	private Object jsonObject = null;

	private Object jsonObject1 = null;
	CancelBookingResponseDTO cancelBookingResponseDTO = new CancelBookingResponseDTO();
	CancelBookingDTO cancelbookingDto = new CancelBookingDTO();
	MainRequestDTO<CancelBookingDTO> dto = new MainRequestDTO<>();
	MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDTO = new MainRequestDTO<>();
	PreRegIdsByRegCenterIdResponseDTO preRegIdsResponseDTO = new PreRegIdsByRegCenterIdResponseDTO();
	List<PreRegIdsByRegCenterIdResponseDTO> respList = new ArrayList<>();
	PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();

	@SuppressWarnings({ "deprecation" })
	@Before
	public void setup() throws FileNotFoundException, ParseException, URISyntaxException {
		availabilityDto = new AvailabilityDto();
		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();

		URI dataSyncUri = new URI(
				classLoader.getResource("booking.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(dataSyncUri.getPath());
		jsonObject = parser.parse(new FileReader(file));

		bookingRequestDTO.setPreRegistrationId("23587986034785");
		bookingRequestDTO.setNewBookingDetails(new BookingRegistrationDTO());
		bookingRequestDTO.setOldBookingDetails(new BookingRegistrationDTO());
		// bookingRequestDTOB.setPre_registration_id("31496715428069");
		// bookingRequestDTOB.setRegistration_center_id("1");
		// bookingRequestDTOB.setSlotFromTime("09:00");
		// bookingRequestDTOB.setSlotToTime("09:13");
		// bookingRequestDTOB.setReg_date("2018-12-06");

		bookingDTO.setRequest(bookingList);

		responseDto.setErrors(null);

		URI cancelUri = new URI(
				classLoader.getResource("cancelAppointment.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file1 = new File(cancelUri.getPath());
		jsonObject1 = parser.parse(new FileReader(file1));

		cancelbookingDto.setPreRegistrationId("12345");
		cancelbookingDto.setRegistrationCenterId("2");
		cancelbookingDto.setSlotFromTime("09:00");
		cancelbookingDto.setSlotToTime("09:20");
		String restime = "2018-12-04T07:22:57.086+0000";
		cancelbookingDto.setRegDate(restime);

		dto.setRequest(cancelbookingDto);
		requestDTO.setRequest(preRegIdsByRegCenterIdDTO);
		List<String> respList = new ArrayList<>();
		respList.add("Reterived all pre-registration ids successfully");
		preRegIdsResponseDTO.setRegistrationCenterId("1");
		preRegIdsResponseDTO.setPreRegistrationIds(respList);
	}

	@WithUserDetails("individual")
	@Test
	public void getAvailability() throws Exception {
		MainResponseDTO<AvailabilityDto> response = new MainResponseDTO<>();
		Mockito.when(service.getAvailability(Mockito.any())).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/appointment/availability")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("registration_center_id", "1");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@WithUserDetails("PRE_REGISTRATION_ADMIN")
	@Test
	public void saveAvailability() throws Exception {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		Mockito.when(service.addAvailability()).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/appointment/availability/sync")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	@WithUserDetails("individual")
	public void successBookingTest() throws Exception {

		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
		List<String> respList = new ArrayList<>();
		respList.add("APPOINTMENT_SUCCESSFULLY_BOOKED");
		responseDto.setResponse(respList);

		Mockito.when(service.bookAppointment(bookingDTO)).thenReturn(responseDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/appointment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	@WithUserDetails("individual")
	public void failureBookingTest() throws Exception {

		bookingDTO.setRequest(null);
		Mockito.when(service.bookAppointment(bookingDTO)).thenReturn(responseDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/appointment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	@WithUserDetails("individual")
	public void cancelAppointmentSuccessTest() throws Exception {

		responseDto.setErrors(null);
		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
		cancelBookingResponseDTO.setMessage("APPOINTMENT_SUCCESSFULLY_CANCELED");
		cancelBookingResponseDTO.setTransactionId("375765");
		responseDto.setResponse(cancelBookingResponseDTO);

		Mockito.when(service.cancelAppointment(dto)).thenReturn(responseDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/appointment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject1.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	@WithUserDetails("individual")
	public void cancelAppointmentFailureTest() throws Exception {

		dto.setRequest(null);
		Mockito.when(service.cancelAppointment(dto)).thenReturn(responseDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/appointment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject1.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("individual")
	public void getAppointmentDetails() throws Exception {
		MainResponseDTO<BookingRegistrationDTO> response = new MainResponseDTO<>();
		Mockito.when(service.getAppointmentDetails("12345")).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/appointment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("pre_registration_id", "12345");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}


	@Test
	@WithUserDetails("individual")
	public void deleteBookingTest() throws Exception {
		String preId = "3";
		MainListResponseDTO<DeleteBookingDTO> response = new MainListResponseDTO<>();
		List<DeleteBookingDTO> DeleteList = new ArrayList<DeleteBookingDTO>();
		DeleteBookingDTO deleteDto = new DeleteBookingDTO();

		deleteDto.setPreRegistrationId("3");
		deleteDto.setDeletedBy("9527832358");
		DeleteList.add(deleteDto);
		response.setResponse(DeleteList);
		Mockito.when(service.deleteBooking(preId)).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/appointment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("pre_registration_id", preId);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 *             on error
	 */
	@Test
	@WithUserDetails("individual")
	public void getAllApplicationByDateTest() throws Exception {

		String fromDate = "2018-12-06";
		String toDate = "2018-12-06";
		MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> response = new MainResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		preIds.add("1234");
		PreRegIdsByRegCenterIdResponseDTO byRegCenterIdResponseDTO = new PreRegIdsByRegCenterIdResponseDTO();
		byRegCenterIdResponseDTO.setPreRegistrationIds(preIds);
		byRegCenterIdResponseDTO.setRegistrationCenterId("10001");
		response.setResponse(byRegCenterIdResponseDTO);

		Mockito.when(service.getBookedPreRegistrationByDate(Mockito.any(), Mockito.any(), Mockito.anyString()))
				.thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/appointment/byDateAndRegCenterId/")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("from_date", fromDate)
				.accept(MediaType.APPLICATION_JSON_VALUE).param("to_date", toDate)
				.accept(MediaType.APPLICATION_JSON_VALUE).param("reg_center_id", "10001");

		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}
}
