package io.mosip.admin.test.controller;

import static io.mosip.admin.navigation.constant.LoginUri.INVALIDATE_TOKEN;
import static io.mosip.admin.navigation.constant.LoginUri.VALIDATE_USER;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.admin.TestBootApplication;
import io.mosip.admin.navigation.dto.UserRequestDTO;
import io.mosip.admin.navigation.dto.UserResponseDTO;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;

@SpringBootTest(classes=TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class LoginLogoutTest {

    private static final String SET_COOKIE_STRING = "Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmVyZWd1c2VyIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJ0c3BAbW9zaXAuaW8iLCJyb2xlIjoiSU5ESVZJRFVBTCIsIm5hbWUiOiJwcmVyZWciLCJpYXQiOjE1NTc0NzQ1NjIsImV4cCI6MTU1NzQ4MDU2Mn0.hhfOFk4aU86y-i8Wqj6-j05rheD0Vg2xRP6pqGZj2tl1_wWX1nHk_c43ozL1WEB4QQScNUTCE9NekgFa-d_Xqw; Max-Age=6000000; Expires=Thu, 18-Jul-2019 18:29:22 GMT; Path=/; Secure; HttpOnly";
    private static final String COOKIE_STRING = "Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmVyZWd1c2VyIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJ0c3BAbW9zaXAuaW8iLCJyb2xlIjoiSU5ESVZJRFVBTCIsIm5hbWUiOiJwcmVyZWciLCJpYXQiOjE1NTc0Njg5NDcsImV4cCI6MTU1NzQ3NDk0N30.Ev1XNeKrAOceu4n4uIr8HAaIuNCZwmfTCnd0mVaeQdmt1f1v3LXiiUMRMLoxe4A5Fzo9IRbMcjROtVbo2WhSxQ";
    private static final String SUCCESS_MESSAGE = "success";
    private static final String LOGIN_SUCCESS_MESSAGE = "Username and password combination had been validated successfully";
    private static final String LOGOUT_SUCCESS_MESSAGE = "Token has been invalidated successfully";
    private static final String APP_ID = "testAppId";
    private static final String USER_NAME = "testUsername";
    private static final String PASSWORD = "testPassword";
    @Value("${mosip.admin.navigation.base-uri}")
    private String baseUri;
    @Value("${mosip.admin.navigation.authmanager-uri}")
    private String authmanagerUri;
    @Value("${mosip.admin.navigation.userIdPwd-uri}")
    private String pwdUri;
    @Value("${mosip.admin.navigation.invalidateToken-uri}")
    private String invalidateTokenUri;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @MockBean
    private RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);

    private HttpHeaders loginHeaders = new HttpHeaders();
    private HttpHeaders logoutHeaders = new HttpHeaders();
    private RequestWrapper<UserRequestDTO> loginSuccessRequest;
    private ResponseWrapper<UserResponseDTO> loginSuccessResponse;
    private ResponseWrapper<UserResponseDTO> logoutSuccessResponse;

    @Before
    public void setUp() {
	loginHeaders.add(HttpHeaders.SET_COOKIE, SET_COOKIE_STRING);
	logoutHeaders.add(HttpHeaders.COOKIE, COOKIE_STRING);
	mapper = new ObjectMapper();
	mapper.registerModule(new JavaTimeModule());

	loginSuccessRequest = new RequestWrapper<>();
	loginSuccessResponse = new ResponseWrapper<>();
	logoutSuccessResponse = new ResponseWrapper<>();

	UserRequestDTO loginSuccessRequestDTO = new UserRequestDTO();
	loginSuccessRequestDTO.setAppId(APP_ID);
	loginSuccessRequestDTO.setUserName(USER_NAME);
	loginSuccessRequestDTO.setPassword(PASSWORD);
	loginSuccessRequest.setRequest(loginSuccessRequestDTO);

	UserResponseDTO loginSuccessResponseDTO = new UserResponseDTO();
	loginSuccessResponseDTO.setStatus(SUCCESS_MESSAGE);
	loginSuccessResponseDTO.setMessage(LOGIN_SUCCESS_MESSAGE);
	loginSuccessResponse.setResponse(loginSuccessResponseDTO);

	UserResponseDTO logoutSuccessResponseDTO = new UserResponseDTO();
	logoutSuccessResponseDTO.setStatus(SUCCESS_MESSAGE);
	logoutSuccessResponseDTO.setMessage(LOGOUT_SUCCESS_MESSAGE);
	logoutSuccessResponse.setResponse(logoutSuccessResponseDTO);
    }

    @Test
    @WithUserDetails("zonal-admin")
    public void testValidate() throws Exception {
	when(responseEntity.getBody())
		.thenReturn(mapper.writeValueAsString(loginSuccessResponse));
	when(responseEntity.getHeaders()).thenReturn(loginHeaders);
	when(restTemplate.exchange(
		baseUri.concat(authmanagerUri).concat(pwdUri),
		HttpMethod.POST,
		new HttpEntity<RequestWrapper<UserRequestDTO>>(
			loginSuccessRequest),
		String.class)).thenReturn(responseEntity);

	String successRequestString = mapper
		.writeValueAsString(loginSuccessRequest);
	MvcResult mvcResult = mockMvc
		.perform(post(VALIDATE_USER)
			.contentType(MediaType.APPLICATION_JSON)
			.content(successRequestString))
		.andExpect(status().isOk())
		.andReturn();
	ResponseWrapper<UserResponseDTO> responseWrapper = mapper.readValue(
		mvcResult.getResponse().getContentAsString(),
		new TypeReference<ResponseWrapper<UserResponseDTO>>() {
		});
	UserResponseDTO userResponse = responseWrapper.getResponse();
	assertTrue(userResponse.getStatus().equals(SUCCESS_MESSAGE));
	assertTrue(userResponse.getMessage().equals(LOGIN_SUCCESS_MESSAGE));
    }

    @Test
    @WithUserDetails("zonal-admin")
    public void testInValidate() throws Exception {
	when(responseEntity.getBody())
		.thenReturn(mapper.writeValueAsString(logoutSuccessResponse));
	when(restTemplate.exchange(
		baseUri.concat(authmanagerUri).concat(invalidateTokenUri),
		HttpMethod.POST,
		new HttpEntity<String>(null, logoutHeaders),
		String.class)).thenReturn(responseEntity);
	MvcResult mvcResult = mockMvc
		.perform(post(INVALIDATE_TOKEN).headers(logoutHeaders)
			.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn();
	ResponseWrapper<UserResponseDTO> responseWrapper = mapper.readValue(
		mvcResult.getResponse().getContentAsString(),
		new TypeReference<ResponseWrapper<UserResponseDTO>>() {
		});
	UserResponseDTO userResponse = responseWrapper.getResponse();
	assertTrue(userResponse.getStatus().equals(SUCCESS_MESSAGE));
	assertTrue(userResponse.getMessage().equals(LOGOUT_SUCCESS_MESSAGE));
    }

}
