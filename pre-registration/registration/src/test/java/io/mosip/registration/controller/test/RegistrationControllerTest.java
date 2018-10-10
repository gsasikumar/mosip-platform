package io.mosip.registration.controller.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.controller.RegistrationController;
import io.mosip.registration.core.exceptions.TablenotAccessibleException;
import io.mosip.registration.dto.AddressDto;
import io.mosip.registration.dto.ApplicationDto;
import io.mosip.registration.dto.ContactDto;
import io.mosip.registration.dto.NameDto;
import io.mosip.registration.dto.RegistrationDto;
import io.mosip.registration.dto.ResponseDto;
import io.mosip.registration.exception.PrimaryValidationFailed;
import io.mosip.registration.helper.ApplicationHelper;

@RunWith(SpringRunner.class)
@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ApplicationHelper helper;
	
	private RegistrationDto regDto= new RegistrationDto();
	private NameDto nameDto= new NameDto();
	private ContactDto contactDto= new ContactDto();
	private AddressDto addrDto= new AddressDto();
	
	private ApplicationDto appDto= new ApplicationDto();
	
	private List<RegistrationDto> applicationForms= new ArrayList<>();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Before
	public void setup() {

		nameDto.setFirstname("Rajath");
		nameDto.setFullname("Rajath Kumar");
		
		contactDto.setEmail("rajath.kr1249@gmail.com");
		contactDto.setMobile("9480548558");
		
		addrDto.setAddrLine1("global");
		addrDto.setAddrLine2("Village");
		addrDto.setLocationCode("1234");
		
		regDto.setAddress(addrDto);
		regDto.setContact(contactDto);
		regDto.setName(nameDto);
		
		regDto.setAge(10);
		regDto.setCreatedBy("Rajath");
        regDto.setIsPrimary(true);
        
        applicationForms.add(regDto);
        appDto.setApplications(applicationForms);
	}
	
//	@Test
//	public void successSave() throws Exception {
//		logger.info("----------Successful save of application-------");		
//        ResponseDto response= new ResponseDto();
//        ObjectMapper mapperObj = new ObjectMapper();
//        String jsonStr="";
//        
//        try {
//             jsonStr = mapperObj.writeValueAsString(appDto);
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//        response.setPrId("22893647484937");
//        response.setGroupId("986453847462");
//        List<ResponseDto> resList= new ArrayList<>();
//        resList.add(response);
//		Mockito.when(helper.Helper(appDto)).thenReturn(resList);
//
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/registration/save")
//				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
//				.content(jsonStr);
//		MvcResult result=mockMvc.perform(requestBuilder).andReturn();
//		mockMvc.perform(requestBuilder).andExpect(status().isOk());
//	}
	
	
	@Test
	public void FailureSave() throws Exception {
		logger.info("----------Unsuccessful save of application-------");		
        ObjectMapper mapperObj = new ObjectMapper();
        
        String jsonStr="";
        
        try {
             jsonStr = mapperObj.writeValueAsString(appDto);

        } catch (IOException e) {

            e.printStackTrace();
        }
		Mockito.doThrow(PrimaryValidationFailed.class).when(helper).Helper(appDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/registration/save")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonStr);
		mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
	}

}
