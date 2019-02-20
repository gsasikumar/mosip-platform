package io.mosip.authentication.service.impl.otpgen.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.KycServiceImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for KycServiceImpl.
 *
 * @author Sanjay Murali
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
@WebMvcTest
@Import(IDAMappingConfig.class)
@TestPropertySource("classpath:sample-output-test.properties")
public class KycServiceImplTest {

	@Autowired
	Environment env;

	@Autowired
	Environment environment;

	@InjectMocks
	private IdInfoHelper demoHelper;

	@Autowired
	private IDAMappingConfig idMappingConfig;

	@InjectMocks
	private KycServiceImpl kycServiceImpl;

	@Mock
	IdTemplateManager idTemplateManager;

	PDFGenerator pdfGenerator = new PDFGeneratorImpl();

	@Value("${sample.demo.entity}")
	String value;

	Map<String, List<IdentityInfoDTO>> idInfo;

	@Before
	public void before() throws IdAuthenticationDaoException {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("eKycPDFTemplate");
		ReflectionTestUtils.setField(kycServiceImpl, "messageSource", source);
		ReflectionTestUtils.setField(kycServiceImpl, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(kycServiceImpl, "pdfGenerator", pdfGenerator);
		ReflectionTestUtils.setField(kycServiceImpl, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(demoHelper, "environment", environment);
		ReflectionTestUtils.setField(demoHelper, "idMappingConfig", idMappingConfig);
		idInfo = getIdInfo("12232323121");

	}

	@Test
	public void validUIN() throws IOException {
		try {
			deleteBootStrapFile();
			Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any())).thenReturn("test");

			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN1() {
		try {
			deleteBootStrapFile();
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, false, true, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN2() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, true, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN3() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.FULL, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN4() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.FULL, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN5() throws IdAuthenticationDaoException {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.FULL, true, true, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN6() throws IdAuthenticationDaoException, IOException, IdAuthenticationBusinessException {
		MockEnvironment environment = new MockEnvironment();
		ReflectionTestUtils.setField(kycServiceImpl, "env", environment);
		KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.FULL, true, true, idInfo);
	}

	@Test(expected = NullPointerException.class)
	public void validUIN7() throws IdAuthenticationDaoException, IOException, IdAuthenticationBusinessException {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("mosip.primary.lang-code", "ara");
		environment.setProperty("ekyc.type.fullkyc",
				"fullName,firstName,middleName,lastName,dateOfBirth,gender,phone,email,addressLine1,addressLine2,addressLine3,city,province,region,postalCode,face");
		ReflectionTestUtils.setField(kycServiceImpl, "env", environment);
		kycServiceImpl.retrieveKycInfo("12232323121", KycType.FULL, true, true, idInfo);
	}

	@Test
	public void validUIN8()
			throws IdAuthenticationDaoException, IOException, IdAuthenticationBusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		MockEnvironment environment = new MockEnvironment();
		ReflectionTestUtils.setField(kycServiceImpl, "env", environment);
		Map<String, Object> identity = new HashMap<>();
		Method declaredMethod = KycServiceImpl.class.getDeclaredMethod("generatePrintableKyc", KycType.class, Map.class,
				boolean.class);
		declaredMethod.setAccessible(true);
		String str = (String) declaredMethod.invoke(kycServiceImpl, null, identity, false);
		assertNull(str);
	}

	@Test
	public void invalidUIN9()
			throws IdAuthenticationDaoException, IOException, IdAuthenticationBusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		IdTemplateManager idTemplateManager1 = mock(IdTemplateManager.class);
		ReflectionTestUtils.setField(kycServiceImpl, "idTemplateManager", idTemplateManager1);
		Map<String, Object> identity = new HashMap<>();
		identity.put("asdjsa", "sadfsa");
		Mockito.when(idTemplateManager1.applyTemplate("ekyc-full-pri-template.html", identity))
				.thenThrow(IOException.class);
		Method declaredMethod = KycServiceImpl.class.getDeclaredMethod("generatePrintableKyc", KycType.class, Map.class,
				boolean.class);
		declaredMethod.setAccessible(true);
		try {
			declaredMethod.invoke(kycServiceImpl, null, identity, false);
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().getClass().equals(IdAuthenticationBusinessException.class));
		}
	}

	@Test
	public void invalidUIN10()
			throws IdAuthenticationDaoException, IOException, IdAuthenticationBusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, Object> identity = new HashMap<>();
		identity.put("photoUrl", null);
		Method declaredMethod = KycServiceImpl.class.getDeclaredMethod("deleteFileOnExit", Map.class);
		declaredMethod.setAccessible(true);
		declaredMethod.invoke(kycServiceImpl, identity);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, List<IdentityInfoDTO>> getIdInfo(String uinRefId) throws IdAuthenticationDaoException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> outputMap = mapper.readValue(value, new TypeReference<Map>() {
			});

			return outputMap.entrySet().parallelStream()
					.filter(entry -> entry.getKey().equals("response") && entry.getValue() instanceof Map)
					.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream())
					.filter(entry -> entry.getKey().equals("identity") && entry.getValue() instanceof Map)
					.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream())
					.collect(Collectors.toMap(Entry<String, Object>::getKey, entry -> {
						Object val = entry.getValue();
						if (val instanceof List) {
							List<Map> arrayList = (List) val;
							return arrayList.stream().filter(elem -> elem instanceof Map)
									.map(elem -> (Map<String, Object>) elem).map(map1 -> {
										IdentityInfoDTO idInfo = new IdentityInfoDTO();
										idInfo.setLanguage(
												map1.get("language") != null ? String.valueOf(map1.get("language"))
														: null);
										idInfo.setValue(String.valueOf(map1.get("value")));
										return idInfo;
									}).collect(Collectors.toList());

						}
						return Collections.emptyList();
					}));
		} catch (IOException e) {
			throw new IdAuthenticationDaoException();
		}

	}

	private void deleteBootStrapFile() {
		String property = System.getProperty("java.io.tmpdir");
		property = property.concat("/bootstrap.min.css");
		File file = new File(property);
		if (file.exists()) {
			file.delete();
		}
	}

}
