package io.mosip.authentication.service.impl.indauth.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.integration.MasterDataManager;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;

/**
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	Errors errors;

	@InjectMocks
	RollingFileAppender appender;

	@InjectMocks
	private InternalAuthRequestValidator internalAuthRequestValidator;

	@Mock
	IdInfoHelper idinfoHelper;

	@Mock
	UinValidatorImpl uinValidator;

	@Mock
	VidValidatorImpl vidValidator;

	@Autowired
	Environment env;

	@Mock
	private MasterDataManager masterDataManager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(internalAuthRequestValidator, "env", env);
		ReflectionTestUtils.setField(internalAuthRequestValidator, "idInfoHelper", idinfoHelper);
		ReflectionTestUtils.setField(idinfoHelper, "environment", env);
	}

	@Test
	public void testSupportTrue() {
		assertTrue(internalAuthRequestValidator.supports(AuthRequestDTO.class));
	}

	@Test
	public void testSupportFalse() {
		assertFalse(internalAuthRequestValidator.supports(OtpRequestDTO.class));
	}
	
	@Test
	public void testinValidInternalAuthRequestValidator() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().plus(2, ChronoUnit.DAYS).atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		authTypeDTO.setBio(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("internal.allowed.auth.type", "fulladdress");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		ReflectionTestUtils.setField(internalAuthRequestValidator, "env", mockenv);
		/*
		 * Environment env = mock(Environment.class);
		 * Mockito.when(env.getProperty("internal.allowed.auth.type")).thenReturn(
		 * "fulladdresss");
		 */
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testinValidInternalAuthRequestValidator2() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		authTypeDTO.setBio(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("internal.allowed.auth.type", "fulladdress");
		ReflectionTestUtils.setField(internalAuthRequestValidator, "env", mockenv);
		/*
		 * Environment env = mock(Environment.class);
		 * Mockito.when(env.getProperty("internal.allowed.auth.type")).thenReturn(
		 * "fulladdresss");
		 */
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testValidInternalAuthRequestValidator2() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	
	@Test
	public void testinValiddata() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testValidInternalAuthRequestValidator() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
		authTypeDTO.setBio(true);
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		fingerValue.setValue("finger");
		fingerValue.setSubType("Thumb");
		fingerValue.setType("FIR");
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		irisValue.setValue("iris img");
		irisValue.setSubType("left");
		irisValue.setType("IIR");
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		faceValue.setValue("face img");
		faceValue.setSubType("Thumb");
		faceValue.setType("FID");
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		BioInfo bioinfo = new BioInfo();
		bioinfo.setBioType(BioAuthType.FGR_IMG.getType());
		bioinfo.setDeviceId("123456789");
		bioinfo.setDeviceProviderID("1234567890");

		List<BioInfo> bioInfoList = new ArrayList<BioInfo>();
		bioInfoList.add(bioinfo);
		authRequestDTO.setBioMetadata(bioInfoList);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(requestDTO);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		System.err.println(errors);
		assertFalse(errors.hasErrors());
	}

	
	@Test
	public void testValidInternalAuthRequestValidatorEmptyID() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(Instant.now().toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		IdentityDTO identitydto = new IdentityDTO();
		authRequestDTO.setIndividualId("");
		authRequestDTO.setIndividualIdType("D");
		authTypeDTO.setDemo(true);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(identitydto);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testInvalidInternalAuthRequestValidator() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		authRequestDTO.setRequestTime("2018-11-23T17:00:57.086+0530");
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);

		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testInvalidDate() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		authRequestDTO.setRequestTime(Instant.now().toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		// authTypeDTO.setFace(true);
		// authTypeDTO.setFingerPrint(true);
		// authTypeDTO.setIris(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		authRequestDTO.setIndividualIdType("D");
		authRequestDTO.setIndividualId("274390482564");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestInvalidTimeFormat() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime("a2018-11-11");
		Mockito.when(idinfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		internalAuthRequestValidator.validateDate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

}
