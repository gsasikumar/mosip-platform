package io.mosip.authentication.service.impl.indauth.facade;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycResponseDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.spi.indauth.service.PinAuthService;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdRepoManager;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.service.KycServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.notification.service.NotificationServiceImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * The class validates AuthFacadeImpl.
 *
 * @author Arun Bose
 * 
 * 
 * @author Prem Kumar
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })

public class AuthFacadeImplTest {

	/** The auth facade impl. */
	@InjectMocks
	private AuthFacadeImpl authFacadeImpl;
	@Mock
	private AuthFacadeImpl authFacadeMock;
	/** The env. */
	@Autowired
	private Environment env;

	@InjectMocks
	private KycServiceImpl kycServiceImpl;

	/** The otp auth service impl. */
	@Mock
	private OTPAuthService otpAuthServiceImpl;
	/** The IdAuthService */
	@Mock
	private IdAuthService<AutnTxn> idAuthService;
	/** The KycService **/
	@Mock
	private KycService kycService;
	/** The IdInfoHelper **/
	@Mock
	private IdInfoHelper idInfoHelper;
	/** The IdRepoService **/
	@Mock
	private IdAuthService idInfoService;
	/** The DemoAuthService **/
	@Mock
	private DemoAuthService demoAuthService;

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	@Mock
	private IDAMappingConfig idMappingConfig;

	@InjectMocks
	NotificationServiceImpl notificationService;

	@Mock
	NotificationManager notificationManager;

	@Mock
	private IdTemplateManager idTemplateManager;

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	private RestHelper restHelper;

	@InjectMocks
	private OTPManager otpManager;

	@Mock
	private BioAuthService bioAuthService;
	@Mock
	private AuditHelper auditHelper;
	@Mock
	private IdRepoManager idRepoManager;
	@Mock
	private AutnTxnRepository autntxnrepository;

	@Mock
	private PinAuthService pinAuthService;

	@Mock
	private TokenIdGenerator<String, String> tokenIdGenerator;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(authFacadeImpl, "otpService", otpAuthServiceImpl);
		ReflectionTestUtils.setField(authFacadeImpl, "tokenIdGenerator", tokenIdGenerator);
		ReflectionTestUtils.setField(authFacadeImpl, "pinAuthService", pinAuthService);
		ReflectionTestUtils.setField(authFacadeImpl, "kycService", kycService);
		ReflectionTestUtils.setField(authFacadeImpl, "bioAuthService", bioAuthService);
		ReflectionTestUtils.setField(authFacadeImpl, "auditHelper", auditHelper);
		ReflectionTestUtils.setField(authFacadeImpl, "env", env);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);

		ReflectionTestUtils.setField(kycServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(kycServiceImpl, "env", env);
		ReflectionTestUtils.setField(authFacadeImpl, "notificationService", notificationService);
		ReflectionTestUtils.setField(authFacadeImpl, "idInfoHelper", idInfoHelper);

		ReflectionTestUtils.setField(notificationService, "env", env);
		ReflectionTestUtils.setField(notificationService, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationService, "notificationManager", notificationManager);
		ReflectionTestUtils.setField(idInfoHelper, "environment", env);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
	}

	/**
	 * This class tests the authenticateApplicant method where it checks the IdType
	 * and DemoAuthType.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationDaoException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */

	@Test
	public void authenticateApplicantTest() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
			NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger=new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris=new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType(BioAuthType.IRIS_IMG.getType());
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace=new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType(BioAuthType.FACE_IMG.getType());
		faceValue.setData(dataDTOFace);
		
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, uin, Collections.emptyMap(),"123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		Mockito.when(idAuthService.getIdRepoByUIN(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());
		Mockito.when(idInfoService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setStatus(Boolean.TRUE);

		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdGenerator.generateId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(authRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(idInfoHelper.getUinOrVidType(authRequestDTO)).thenReturn(values);
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo,"123456")).thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any())).thenReturn("test");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		authFacadeImpl.authenticateApplicant(authRequestDTO, true,"123456","123456");

	}

	/**
	 * This class tests the processAuthType (OTP) method where otp validation
	 * failed.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */

	@Test
	public void processAuthTypeTestFail() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(false);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setId("1234567");

		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
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
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setTransactionID("1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(authRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(idInfoHelper.getUinOrVidType(authRequestDTO)).thenReturn(values);
		List<AuthStatusInfo> authStatusList = ReflectionTestUtils.invokeMethod(authFacadeImpl, "processAuthType",
				authRequestDTO, idInfo, "1233", true, "247334310780728918141754192454591343","123456");

		assertTrue(authStatusList.stream().noneMatch(status -> status.isStatus()));
	}

	/**
	 * This class tests the processAuthType (OTP) method where otp validation gets
	 * successful.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Test
	public void processAuthTypeTestSuccess() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("1234567");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authTypeDTO.setBio(true);
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger=new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO fingerValue2 = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger2=new DataDTO();
		dataDTOFinger2.setBioValue("");
		dataDTOFinger2.setBioSubType("Thumb");
		dataDTOFinger2.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger2.setDeviceProviderID("1234567890");
		fingerValue2.setData(dataDTOFinger2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(fingerValue2);

		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		reqDTO.setBiometrics(fingerIdentityInfoDtoList);
		reqDTO.setOtp("456789");
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setId("1234567");
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(authRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(idInfoHelper.getUinOrVidType(authRequestDTO)).thenReturn(values);

		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, "1242", Collections.emptyMap(),"123456"))
				.thenReturn(AuthStatusInfoBuilder.newInstance().setStatus(true).build());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		List<AuthStatusInfo> authStatusList = ReflectionTestUtils.invokeMethod(authFacadeImpl, "processAuthType",
				authRequestDTO, idInfo, "1242", true, "247334310780728918141754192454591343","123456");
		assertTrue(authStatusList.stream().anyMatch(status -> status.isStatus()));
	}

	@Test
	public void processAuthTypeTestFailure() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("1234567");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);

		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setOtp("456789");		
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setId("1234567");
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(authRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(idInfoHelper.getUinOrVidType(authRequestDTO)).thenReturn(values);
		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, "1242", Collections.emptyMap(),"123456"))
				.thenReturn(AuthStatusInfoBuilder.newInstance().setStatus(true).build());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		List<AuthStatusInfo> authStatusList = ReflectionTestUtils.invokeMethod(authFacadeImpl, "processAuthType",
				authRequestDTO, idInfo, "1242", false, "247334310780728918141754192454591343","123456");
		assertTrue(authStatusList.stream().anyMatch(status -> status.isStatus()));
	}

	@Test
	public void processKycAuthValid() throws IdAuthenticationBusinessException {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO request = new RequestDTO();
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setOtp("456789");	
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(request);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(request);

		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setErrors(null);
		kycResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
		Map<String, ? extends Object> identity = null;
		kycAuthResponseDTO.setStatus(Boolean.TRUE);

		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		kycResponseDTO.setIdentity(idInfo);
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setStatus(Boolean.TRUE);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authResponseDTO.setStaticToken("234567890");
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		authResponseDTO.setVersion("1.0");
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(kycAuthRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(kycService.retrieveKycInfo(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(kycResponseDTO);
		Mockito.when(idInfoHelper.getUinOrVidType(kycAuthRequestDTO)).thenReturn(values);
		assertNotNull(authFacadeImpl.processKycAuth(kycAuthRequestDTO, authResponseDTO,"123456"));

	}

	@Test
	public void processKycAuthInValid() throws IdAuthenticationBusinessException {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO request = new RequestDTO();
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setOtp("456789");	
		request.setDemographics(idDTO);
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(request);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(request);
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setErrors(null);
		kycResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
		Map<String, ? extends Object> identity = null;
		kycAuthResponseDTO.setStatus(Boolean.TRUE);

		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		kycResponseDTO.setIdentity(idInfo);
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setStatus(Boolean.TRUE);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authResponseDTO.setStaticToken("234567890");
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		authResponseDTO.setVersion("1.0");
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(kycAuthRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(kycService.retrieveKycInfo(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(kycResponseDTO);
		Mockito.when(idInfoHelper.getUinOrVidType(kycAuthRequestDTO)).thenReturn(values);
		Map<String, List<IdentityInfoDTO>> entityValue = new HashMap<>();
		Mockito.when(idInfoService.getIdInfo(Mockito.any())).thenReturn(entityValue);
		assertNotNull(authFacadeImpl.processKycAuth(kycAuthRequestDTO, authResponseDTO,"123456"));

	}

	@Test
	public void processKycAuthRequestNull() throws IdAuthenticationBusinessException {
	KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(null);
		idInfoList.add(null);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO request = new RequestDTO();
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setOtp("456789");	
		request.setDemographics(idDTO);
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(null);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(request);
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setErrors(null);
		kycResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
		Map<String, ? extends Object> identity = null;
		kycAuthResponseDTO.setStatus(Boolean.TRUE);

		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		kycResponseDTO.setIdentity(idInfo);
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setStatus(Boolean.TRUE);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authResponseDTO.setStaticToken("234567890");
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		authResponseDTO.setVersion("1.0");
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(kycAuthRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(kycService.retrieveKycInfo(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(kycResponseDTO);
		Mockito.when(idInfoHelper.getUinOrVidType(kycAuthRequestDTO)).thenReturn(values);
		Map<String, List<IdentityInfoDTO>> entityValue = new HashMap<>();
		Mockito.when(idInfoService.getIdInfo(Mockito.any())).thenReturn(entityValue);
		assertNotNull(authFacadeImpl.processKycAuth(kycAuthRequestDTO, authResponseDTO,"123456"));

	}

	@Test
	public void testGetAuditEvent() {
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "getAuditEvent", true);
	}

	@Test
	public void testGetAuditEventInternal() {
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "getAuditEvent", false);
	}

	@Test
	public void testProcessBioAuthType() throws IdAuthenticationBusinessException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger=new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris=new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType(BioAuthType.IRIS_IMG.getType());
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace=new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType(BioAuthType.FACE_IMG.getType());
		faceValue.setData(dataDTOFace);
		
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, uin, Collections.emptyMap(),"123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		Mockito.when(idAuthService.getIdRepoByUIN(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());
		Mockito.when(idInfoService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setStatus(Boolean.TRUE);

		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdGenerator.generateId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(authRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(idInfoHelper.getUinOrVidType(authRequestDTO)).thenReturn(values);
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo,"123456")).thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any())).thenReturn("test");
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "saveAndAuditBioAuthTxn", authRequestDTO, true, uin,
				IdType.UIN, true, "247334310780728918141754192454591343");
	}

	@Test
	public void testProcessBioAuthTypeFinImg() throws IdAuthenticationBusinessException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger=new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris=new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType(BioAuthType.IRIS_IMG.getType());
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace=new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType(BioAuthType.FACE_IMG.getType());
		faceValue.setData(dataDTOFace);
		
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, uin, Collections.emptyMap(),"123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		Mockito.when(idAuthService.getIdRepoByUIN(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());
		Mockito.when(idInfoService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setStatus(Boolean.TRUE);

		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdGenerator.generateId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(authRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(idInfoHelper.getUinOrVidType(authRequestDTO)).thenReturn(values);
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo,"123456")).thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any())).thenReturn("test");
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "saveAndAuditBioAuthTxn", authRequestDTO, true, uin,
				IdType.UIN, true, "247334310780728918141754192454591343");
	}

	@Test
	public void testProcessPinDetails_pinValidationStatusNull() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		String uin = "794138547620";
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = null;
		RequestDTO request = new RequestDTO();
		authRequestDTO.setIndividualId("5134256294");
		request.setStaticPin(pin);	
		authRequestDTO.setRequest(request);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.UIN.getType(), uin, false)).thenReturn(idRepo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdRepoByUIN(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		AuthStatusInfo pinValidationStatus = new AuthStatusInfo();
		pinValidationStatus.setStatus(true);
		pinValidationStatus.setErr(Collections.emptyList());
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(authRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(idInfoHelper.getUinOrVidType(authRequestDTO)).thenReturn(values);
		Mockito.when(pinAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(),"123456"))
				.thenReturn(pinValidationStatus);
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "processPinAuth", authRequestDTO, uin, true, authStatusList,
				IdType.UIN, "247334310780728918141754192454591343","123456");

	}

	@Test
	public void testProcessPinDetails_pinValidationStatusNotNull() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		String uin = "794138547620";
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "456789";
		RequestDTO request = new RequestDTO();
		authRequestDTO.setIndividualId("5134256294");
		request.setStaticPin(pin);	
		authRequestDTO.setRequest(request);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.UIN.getType(), uin, false)).thenReturn(idRepo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdRepoByUIN(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		AuthStatusInfo pinValidationStatus = new AuthStatusInfo();
		pinValidationStatus.setStatus(true);
		pinValidationStatus.setErr(Collections.emptyList());
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(authRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(idInfoHelper.getUinOrVidType(authRequestDTO)).thenReturn(values);
		Mockito.when(pinAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(),"123456"))
				.thenReturn(pinValidationStatus);
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "processPinAuth", authRequestDTO, uin, true, authStatusList,
				IdType.UIN, "247334310780728918141754192454591343","123456");

	}

	@Test
	public void testProcessPinDetails_pinValidationStatus_false() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		String uin = "794138547620";
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "456789";
		RequestDTO request = new RequestDTO();
		authRequestDTO.setIndividualId("5134256294");
		request.setStaticPin(pin);	
		authRequestDTO.setRequest(request);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		Mockito.when(idInfoHelper.getUTCTime(Mockito.anyString())).thenReturn("2019-02-18T12:28:17.078");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.UIN.getType(), uin, false)).thenReturn(idRepo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdRepoByUIN(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		AuthStatusInfo pinValidationStatus = new AuthStatusInfo();
		pinValidationStatus.setStatus(true);
		pinValidationStatus.setErr(Collections.emptyList());
		Optional<String> value = Optional.of("12345678");
		Mockito.when(idInfoHelper.getUinOrVid(authRequestDTO)).thenReturn(value);
		IdType values = IdType.UIN;
		Mockito.when(idInfoHelper.getUinOrVidType(authRequestDTO)).thenReturn(values);
		pinValidationStatus.setStatus(false);
		pinValidationStatus.setErr(Collections.emptyList());
		Mockito.when(pinAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(),"123456"))
				.thenReturn(pinValidationStatus);
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "processPinAuth", authRequestDTO, uin, true, authStatusList,
				IdType.UIN, "247334310780728918141754192454591343","123456");

	}

	private Map<String, Object> repoDetails() {
		Map<String, Object> map = new HashMap<>();
		map.put("uin", "863537");
		return map;
	}
}
