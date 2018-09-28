package org.mosip.kernel.otpmanagerservice.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mosip.kernel.otpmanagerservice.OtpmanagerServiceApplication;
import org.mosip.kernel.otpmanagerservice.exceptionhandler.MosipOtpInvalidArgumentExceptionHandler;
import org.mosip.kernel.otpmanagerservice.service.impl.OtpGeneratorServiceImpl;
import org.mosip.kernel.otpmanagerservice.service.impl.OtpValidatorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = OtpmanagerServiceApplication.class)
public class GeneratorExceptionTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OtpGeneratorServiceImpl service;

	@MockBean
	private OtpValidatorServiceImpl validatorService;

	@Test
	public void throwMosipOtpInvalidArgumentExceptionHandlerWhenKeyNull() throws Exception {
		when(service.getOtp(Mockito.any())).thenThrow(MosipOtpInvalidArgumentExceptionHandler.class);
		String json = "{\"key\":null}";
		mockMvc.perform(post("/otpmanager/otps").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$.errors[0].errorCode", is("KER-OTG-001")));
	}

	@Test
	public void throwMosipOtpInvalidArgument() throws Exception {
		when(validatorService.validateOtp(Mockito.any(), Mockito.any()))
				.thenThrow(MosipOtpInvalidArgumentExceptionHandler.class);
		mockMvc.perform(get("/otpmanager/otps?key=sa&otp=3212").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable());
	}

}
