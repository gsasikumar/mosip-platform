package io.mosip.authentication.service.filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.service.integration.KeyManager;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthFilterTest {

	@Autowired
	Environment env;

	InternalAuthFilter internalAuthFilter = new InternalAuthFilter();
	
	Map<String, Object> requestBody = new HashMap<>();

	Map<String, Object> responseBody = new HashMap<>();

	@Autowired
	ObjectMapper mapper;

	@Before
	public void before() {
		ReflectionTestUtils.setField(internalAuthFilter, "mapper", mapper);
		ReflectionTestUtils.setField(internalAuthFilter, "env", env);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValidDecodedRequest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException, IdAuthenticationAppException {
		KeyManager keyManager = Mockito.mock(KeyManager.class);
		ReflectionTestUtils.setField(internalAuthFilter, "keyManager", keyManager);
		requestBody.put("request",
				"ew0KCSJhdXRoVHlwZSI6IHsNCgkJImFkZHJlc3MiOiAidHJ1ZSIsDQoJCSJiaW8iOiAidHJ1ZSIsDQoJCSJmYWNlIjogInRydWUiLA0KCQkiZmluZ2VycHJpbnQiOiAidHJ1ZSIsDQoJCSJmdWxsQWRkcmVzcyI6ICJ0cnVlIiwNCgkJImlyaXMiOiAidHJ1ZSIsDQoJCSJvdHAiOiAidHJ1ZSIsDQoJCSJwZXJzb25hbElkZW50aXR5IjogInRydWUiLA0KCQkicGluIjogInRydWUiDQoJfQ0KfQ==");
		responseBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		Mockito.when(keyManager.requestData(Mockito.any(), Mockito.any())).thenReturn(new ObjectMapper().readValue(
				"{\"authType\":{\"address\":\"true\",\"bio\":\"true\",\"face\":\"true\",\"fingerprint\":\"true\",\"fullAddress\":\"true\",\"iris\":\"true\",\"otp\":\"true\",\"personalIdentity\":\"true\",\"pin\":\"true\"}}"
						.getBytes(),
				Map.class));
		assertEquals(responseBody.toString(), internalAuthFilter.decipherRequest(requestBody).toString());

	}

	@Test
	public void testValidEncodedRequest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException, IdAuthenticationAppException {
		requestBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		responseBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		assertEquals(requestBody.toString(), internalAuthFilter.encipherResponse(responseBody).toString());

	}

	@Test
	public void testTxnId() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			IOException, NoSuchMethodException, SecurityException, IdAuthenticationAppException {
		requestBody.put("txnId", null);
		responseBody.put("txnId", "1234");
		assertEquals(responseBody.toString(), internalAuthFilter.setResponseParams(requestBody, responseBody).toString());
	}
	
	@Test
	public void testSign() throws IdAuthenticationAppException {
		assertEquals(true, internalAuthFilter.validateSignature("something", "something".getBytes()));
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void testInValidDecodedRequest() throws IdAuthenticationAppException, JsonParseException, JsonMappingException, IOException {
		KeyManager keyManager = Mockito.mock(KeyManager.class);
		ReflectionTestUtils.setField(internalAuthFilter, "keyManager", keyManager);
		requestBody.put("request",
				123214214);
		internalAuthFilter.decipherRequest(requestBody);
	}

}
