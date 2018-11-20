package io.mosip.authentication.service.impl.otpgen.facade;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.IdentityValue;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdInfoService;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoEntity;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.integration.SenderType;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.authentication.service.repository.DemoRepository;

/**
 * Test class for OTPFacadeImpl. Mockito with PowerMockito.
 *
 * @author Rakesh Roshan
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPFacadeImplTest {

	OtpRequestDTO otpRequestDto;

	@InjectMocks
	DateHelper dateHelper;
	@Mock
	OtpResponseDTO otpResponseDTO;
	@Mock
	OTPService otpService;
	@Autowired
	Environment env;
	@Mock
	AutnTxnRepository autntxnrepository;
	@Mock
	Date date;
	@Mock
	AutnTxn autnTxn;
	@Mock
	IdAuthService idAuthService;
	@InjectMocks
	IdTemplateManager idTemplateManager;
	@InjectMocks
	private RestRequestFactory restRequestFactory;
	@InjectMocks
	private RestHelper restHelper;

	@Mock
	IdInfoService idInfoService;
	@Mock
	private DemoHelper demoHelper;

	@Mock
	DemoRepository demoRepository;

	@InjectMocks
	NotificationManager notificationManager;

	@InjectMocks
	OTPFacadeImpl otpFacadeImpl;

	@Before
	public void before() {
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();

		ReflectionTestUtils.setField(otpFacadeImpl, "env", env);
		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(otpFacadeImpl, "dateHelper", dateHelper);
	}

	@Test
	public void testMaskedEmail() {
		String resultString = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "maskEmail", "umamahesh@gmail.com");
		assertEquals("XXaXXhXXh@gmail.com", resultString);
	}

	@Test
	public void testMaskedMobile() {
		String result = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "maskMobile", "8347899201");
		assertEquals("XXXXXX9201", result);
	}

	@Test
	public void test_GenerateOTP() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setEmail("abcd");
		demoEntity.setMobile("1234");
		Mockito.when(demoRepository.findById(Mockito.anyString())).thenReturn(Optional.of(demoEntity));
		String unqueId = otpRequestDto.getIdvId();
		String txnID = otpRequestDto.getTxnID();
		String productid = "IDA";
		String refId = "8765";
		String otp = "987654";
		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(otpFacadeImpl, "dateHelper", dateHelper);
		Mockito.when(idAuthService.validateUIN(unqueId)).thenReturn(refId);
		String otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn(otp);

		String email = "abcd";
		String mobileNumber = "1234";
		String otpGenerationTime = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "formatDate", new Date(),
				env.getProperty("datetime.pattern"));
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		list.add(new IdentityInfoDTO("en", "rakesh.roshan@mindtree.com"));
		list.add(new IdentityInfoDTO("en", "9500045836"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);

		IdentityValue identityValue = new IdentityValue("en", "mosip");
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo)).thenReturn(identityValue);
		identityValue.setLanguage("en");
		identityValue.setValue("rakesh.roshan@mindtree.com");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo)).thenReturn(identityValue);
		identityValue.setLanguage("en");
		identityValue.setValue("9500045836");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo)).thenReturn(identityValue);

		ReflectionTestUtils.setField(notificationManager, "environment", env);
		ReflectionTestUtils.setField(notificationManager, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(notificationManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(otpFacadeImpl, "notificationManager", notificationManager);

		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getEmail", refId);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getMobileNumber", refId);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "sendOtpNotification", otpRequestDto, otp, refId,
				otpGenerationTime, "rakesh.roshan@mindtree.com", "9500045836");

		otpFacadeImpl.generateOtp(otpRequestDto);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOtpIsFlooded_ThrowException() throws IdAuthenticationBusinessException {
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(5);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "isOtpFlooded", otpRequestDto);
		Mockito.when(otpFacadeImpl.generateOtp(otpRequestDto))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED));
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOTPIsNull_ThrowException() throws IdAuthenticationBusinessException {
		String unqueId = otpRequestDto.getIdvId();
		String txnID = otpRequestDto.getTxnID();
		String productid = "IDA";
		String refId = "8765";
		String otp = null;

		Mockito.when(idAuthService.validateUIN(unqueId)).thenReturn(refId);
		String otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn(otp);
		Mockito.when(otpFacadeImpl.generateOtp(otpRequestDto))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED));
	}

	@Test
	public void testIsOtpFlooded_False() throws IDDataValidationException {
		String uniqueID = otpRequestDto.getIdvId();
		Date requestTime = dateHelper.convertStringToDate(otpRequestDto.getReqTime());
		Date addMinutesInOtpRequestDTime = new Date();

		ReflectionTestUtils.setField(otpFacadeImpl, "autntxnrepository", autntxnrepository);
		ReflectionTestUtils.invokeMethod(autntxnrepository, "countRequestDTime", requestTime,
				addMinutesInOtpRequestDTime, uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "isOtpFlooded", otpRequestDto);
	}

	@Test
	public void testAddMinute() {
		otpRequestDto.getReqTime();
	}

	@Test
	public void testSaveAutnTxn() {
		String refId = "8765";
		ReflectionTestUtils.invokeMethod(autntxnrepository, "saveAndFlush", autnTxn);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "saveAutnTxn", otpRequestDto,refId);
	}

	@Test
	public void testGetRefIdForUIN() {
		String uniqueID = otpRequestDto.getIdvId();
		String actualrefid = ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		String expactedRefId = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
		assertEquals(actualrefid, expactedRefId);
	}

	@Test
	public void test_WhenInvalidID_ForUIN_RefIdIsNull() throws IdAuthenticationBusinessException {
		otpRequestDto.setIdvId("cvcvcjhg76");
		String uniqueID = otpRequestDto.getIdvId();
		ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
	}

	@Test
	public void testGetRefIdForVID() {
		String uniqueID = otpRequestDto.getIdvId();
		otpRequestDto.setIdvIdType(IdType.VID.getType());
		String actualrefid = ReflectionTestUtils.invokeMethod(idAuthService, "validateVID", uniqueID);
		String expactedRefId = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);

		assertEquals(actualrefid, expactedRefId);
	}

	@Test
	public void test_WhenInvalidID_ForVID_RefIdIsNull() throws IdAuthenticationBusinessException {
		otpRequestDto.setIdvId("cvcvcjhg76");
		otpRequestDto.setIdvIdType(IdType.VID.getType());
		String uniqueID = otpRequestDto.getIdvId();
		ReflectionTestUtils.invokeMethod(idAuthService, "validateVID", uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
	}

	@Test
	public void testSendOtpNotification() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setIdvId("8765");
		String otp = "987654";
		String refId = "8765";

		String otpGenerationTime = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "formatDate", new Date(),
				env.getProperty("datetime.pattern"));

		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);

		IdentityValue identityValue = new IdentityValue("en", "mosip");
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo)).thenReturn(identityValue);

		String email = "rakesh.roshan@mindtree.com";
		String mobileNumber = "9500045836";

		ReflectionTestUtils.setField(notificationManager, "environment", env);
		ReflectionTestUtils.setField(notificationManager, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(notificationManager, "restHelper", restHelper);

		ReflectionTestUtils.setField(otpFacadeImpl, "notificationManager", notificationManager);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "sendOtpNotification", otpRequestDto, otp, refId,
				otpGenerationTime, email, mobileNumber);
	}

	@Test
	public void testEmail() throws IdAuthenticationDaoException {
		String refId = "8765";
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "rakesh.roshan@mindtree.com"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("email", list);

		IdentityValue identityValue = new IdentityValue("en", "rakesh.roshan@mindtree.com");
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo)).thenReturn(identityValue);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getEmail", refId);
	}

	@Test
	public void testMobileNumber() throws IdAuthenticationDaoException {
		String refId = "8765";
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "9500045836"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("phone", list);

		IdentityValue identityValue = new IdentityValue("en", "9500045836");
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo)).thenReturn(identityValue);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getMobileNumber", refId);
	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setMuaCode("2345678901234");
		otpRequestDto.setIdvIdType(IdType.UIN.getType());
		otpRequestDto.setReqTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTxnID("2345678901234");
		otpRequestDto.setIdvId("2345678901234");
		otpRequestDto.setVer("1.0");

		return otpRequestDto;
	}

	private OtpResponseDTO getOtpResponseDTO() {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		otpResponseDTO.setStatus("OTP_GENERATED");
		otpResponseDTO.setResTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));

		return otpResponseDTO;
	}
}
