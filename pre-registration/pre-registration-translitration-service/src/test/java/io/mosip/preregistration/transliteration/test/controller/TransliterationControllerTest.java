package io.mosip.preregistration.transliteration.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;

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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.preregistration.transliteration.dto.TransliterationApplicationDTO;
import io.mosip.preregistration.transliteration.dto.ResponseDTO;
import io.mosip.preregistration.transliteration.dto.RequestDTO;
import io.mosip.preregistration.transliteration.service.impl.TransliterationServiceImpl;

/**
 * 
 * Test class to test the pre-registration transliteration Controller methods
 * 
 * @author Kishan rathore
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest(TransliterationControllerTest.class)
public class TransliterationControllerTest {
	
	/**
	 * Autowired reference for {@link #MockMvc}
	 */
	@Autowired
	private MockMvc mockMvc;

	/**
	 * Creating Mock Bean for transliteration Service
	 */
	@MockBean
	private TransliterationServiceImpl serviceImpl;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Object jsonObject = null;
	
	private RequestDTO<TransliterationApplicationDTO> requestDto;
	
	@Before
	public void setup() {
		requestDto=new RequestDTO<>();
		requestDto.setId("mosip.pre-registration.translitration.translitrate");
		requestDto.setReqTime(new Timestamp(System.currentTimeMillis()));
		requestDto.getRequest().setFromFieldLang("English");
		requestDto.getRequest().setFromFieldName("Name1");
		requestDto.getRequest().setFromFieldValue("Kishan");
		requestDto.getRequest().setToFieldLang("Arabic");
		requestDto.getRequest().setToFieldName("Name2");
		requestDto.getRequest().setToFieldValue("");
	}
	
	@Test
	public void successTest() throws Exception {
		
		ResponseDTO<TransliterationApplicationDTO> response=new ResponseDTO<>();
		
		TransliterationApplicationDTO dto=new TransliterationApplicationDTO();
		dto.setToFieldValue("كِسهَن");
		response.setResponse(dto);
		
		Mockito.when(serviceImpl.translitratorService(Mockito.any())).thenReturn(response);
		logger.info("Resonse " + response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/translitrate")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestDto.toString());
		logger.info("Resonse " + response);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
		
	}

}
