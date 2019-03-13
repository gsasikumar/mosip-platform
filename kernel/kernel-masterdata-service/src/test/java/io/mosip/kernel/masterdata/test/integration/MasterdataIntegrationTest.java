
package io.mosip.kernel.masterdata.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.IdTypeDto;
import io.mosip.kernel.masterdata.dto.IndividualTypeDto;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.PostReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.RegCenterMachineUserReqDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.masterdata.dto.TemplateTypeDto;
import io.mosip.kernel.masterdata.dto.TitleDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentDto;
import io.mosip.kernel.masterdata.dto.getresponse.IdTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.IndividualTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterHistoryResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.BiometricAttribute;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.DeviceHistory;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.Gender;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.IdType;
import io.mosip.kernel.masterdata.entity.IndividualType;
import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.entity.ReasonList;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistoryPk;
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDeviceHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.TemplateFileFormat;
import io.mosip.kernel.masterdata.entity.TemplateType;
import io.mosip.kernel.masterdata.entity.Title;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.CodeLangCodeAndRsnCatCodeID;
import io.mosip.kernel.masterdata.entity.id.GenderID;
import io.mosip.kernel.masterdata.entity.id.HolidayID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterDeviceID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineDeviceHistoryID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineDeviceID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineHistoryID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserHistoryID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.ApplicantValidDocumentRepository;
import io.mosip.kernel.masterdata.repository.BiometricAttributeRepository;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.repository.DeviceHistoryRepository;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.repository.DocumentTypeRepository;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;
import io.mosip.kernel.masterdata.repository.IndividualTypeRepository;
import io.mosip.kernel.masterdata.repository.LanguageRepository;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.repository.MachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;
import io.mosip.kernel.masterdata.repository.ReasonCategoryRepository;
import io.mosip.kernel.masterdata.repository.ReasonListRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineDeviceHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineUserRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserMachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.TemplateFileFormatRepository;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.repository.TemplateTypeRepository;
import io.mosip.kernel.masterdata.repository.TitleRepository;
import io.mosip.kernel.masterdata.repository.ValidDocumentRepository;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * 
 * @author Sidhant Agarwal
 * @author Urvil Joshi
 * @author Dharmesh Khandelwal
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @author Abhishek Kumar
 * @author Bal Vikash Sharma
 * @author Uday Kumar
 * @author Megha Tanga
 * @author Srinivasan
 * @author Neha
 * @since 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MasterdataIntegrationTest {

	// private static final String JSON_STRING_RESPONSE =
	// "{\"uinLength\":24,\"numberOfWrongAttemptsForOtp\":5,\"accountFreezeTimeoutInHours\":10,\"mobilenumberlength\":10,\"archivalPolicy\":\"arc_policy_2\",\"tokenIdLength\":23,\"restrictedNumbers\":[\"8732\",\"321\",\"65\"],\"registrationCenterId\":\"KDUE83CJ3\",\"machineId\":\"MCBD3UI3\",\"supportedLanguages\":[\"eng\",\"hin\",\"ara\",\"deu\",\"fra\"],\"tspIdLength\":24,\"otpTimeOutInMinutes\":2,\"notificationtype\":\"SMS|EMAIL\",\"pridLength\":32,\"vidLength\":32}";

	private static final String JSON_STRING_RESPONSE = "{\r\n" + "\"registrationConfiguration\":\r\n"
			+ "							{\"keyValidityPeriodPreRegPack\":\"3\",\"smsNotificationTemplateRegCorrection\":\"OTP for your request is $otp\",\"defaultDOB\":\"1-Jan\",\"smsNotificationTemplateOtp\":\"OTP for your request is $otp\",\"supervisorVerificationRequiredForExceptions\":\"true\",\"keyValidityPeriodRegPack\":\"3\",\"irisRetryAttempts\":\"10\",\"fingerprintQualityThreshold\":\"120\",\"multifactorauthentication\":\"true\",\"smsNotificationTemplateUpdateUIN\":\"OTP for your request is $otp\",\"supervisorAuthType\":\"password\",\"maxDurationRegPermittedWithoutMasterdataSyncInDays\":\"10\",\"modeOfNotifyingIndividual\":\"mobile\",\"emailNotificationTemplateUpdateUIN\":\"Hello $user the OTP is $otp\",\"maxDocSizeInMB\":\"150\",\"emailNotificationTemplateOtp\":\"Hello $user the OTP is $otp\",\"emailNotificationTemplateRegCorrection\":\"Hello $user the OTP is $otp\",\"faceRetry\":\"12\",\"noOfFingerprintAuthToOnboardUser\":\"10\",\"smsNotificationTemplateLostUIN\":\"OTP for your request is $otp\",\"supervisorAuthMode\":\"IRIS\",\"operatorRegSubmissionMode\":\"fingerprint\",\"officerAuthType\":\"password\",\"faceQualityThreshold\":\"25\",\"gpsDistanceRadiusInMeters\":\"3\",\"automaticSyncFreqServerToClient\":\"25\",\"maxDurationWithoutMasterdataSyncInDays\":\"7\",\"loginMode\":\"bootable dongle\",\"irisQualityThreshold\":\"25\",\"retentionPeriodAudit\":\"3\",\"fingerprintRetryAttempts\":\"234\",\"emailNotificationTemplateNewReg\":\"Hello $user the OTP is $otp\",\"passwordExpiryDurationInDays\":\"3\",\"emailNotificationTemplateLostUIN\":\"Hello $user the OTP is $otp\",\"blockRegistrationIfNotSynced\":\"10\",\"noOfIrisAuthToOnboardUser\":\"10\",\"smsNotificationTemplateNewReg\":\"OTP for your request is $otp\"},\r\n"
			+ "\r\n" + "\"globalConfiguration\":\r\n"
			+ "						{\"mosip.kernel.crypto.symmetric-algorithm-name\":\"AES\",\"mosip.kernel.virus-scanner.port\":\"3310\",\"mosip.kernel.email.max-length\":\"50\",\"mosip.kernel.email.domain.ext-max-lenght\":\"7\",\"mosip.kernel.rid.sequence-length\":\"5\",\"mosip.kernel.uin.uin-generation-cron\":\"0 * * * * *\",\"mosip.kernel.rid.centerid-length\":\"5\",\"mosip.kernel.email.special-char\":\"!#$%&'*+-\\/=?^_`{|}~.\",\"mosip.kernel.rid.timestamp-length\":\"14\",\"mosip.kernel.vid.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.asymmetric-algorithm-length\":\"2048\",\"mosip.kernel.uin.min-unused-threshold\":\"100000\",\"mosip.kernel.prid.sequence-limit\":\"3\",\"auth.role.prefix\":\"ROLE_\",\"mosip.kernel.email.domain.ext-min-lenght\":\"2\",\"auth.server.validate.url\":\"http:\\/\\/localhost:8091\\/auth\\/validate_token\",\"mosip.kernel.machineid.length\":\"4\",\"mosip.supported-languages\":\"eng,ara,fra,hin,deu\",\"mosip.kernel.prid.length\":\"14\",\"auth.header.name\":\"Authorization\",\"mosip.kernel.crypto.asymmetric-algorithm-name\":\"RSA\",\"mosip.kernel.phone.min-length\":\"9\",\"mosip.kernel.uin.length\":\"10\",\"mosip.kernel.virus-scanner.host\":\"104.211.209.102\",\"mosip.kernel.email.min-length\":\"7\",\"mosip.kernel.rid.machineid-length\":\"5\",\"mosip.kernel.prid.repeating-block-limit\":\"3\",\"mosip.kernel.vid.length.repeating-block-limit\":\"2\",\"mosip.kernel.rid.length\":\"29\",\"mosip.kernel.phone.max-length\":\"15\",\"mosip.kernel.prid.repeating-limit\":\"2\",\"mosip.kernel.uin.restricted-numbers\":\"786,666\",\"mosip.kernel.email.domain.special-char\":\"-\",\"mosip.kernel.vid.length.repeating-limit\":\"2\",\"mosip.kernel.registrationcenterid.length\":\"4\",\"mosip.kernel.phone.special-char\":\"+ -\",\"mosip.kernel.uin.uins-to-generate\":\"200000\",\"mosip.kernel.vid.length\":\"16\",\"mosip.kernel.tokenid.length\":\"36\",\"mosip.kernel.uin.length.repeating-block-limit\":\"2\",\"mosip.kernel.tspid.length\":\"4\",\"mosip.kernel.tokenid.sequence-limit\":\"3\",\"mosip.kernel.uin.length.repeating-limit\":\"2\",\"mosip.kernel.uin.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.symmetric-algorithm-length\":\"256\",\"mosip.kernel.data-key-splitter\":\"#KEY_SPLITTER#\"}\r\n"
			+ "}";

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private RestTemplate restTemplate;
	@MockBean
	private BlacklistedWordsRepository wordsRepository;
	@MockBean
	private LocationRepository locationRepository;
	List<Location> locationHierarchies;

	@MockBean
	private DeviceRepository deviceRepository;

	@MockBean
	private DocumentTypeRepository documentTypeRepository;

	@MockBean
	private DocumentCategoryRepository documentCategoryRepository;

	@MockBean
	private ValidDocumentRepository validDocumentRepository;

	@MockBean
	private BiometricAttributeRepository biometricAttributeRepository;
	private BiometricAttributeDto biometricAttributeDto;
	private BiometricAttribute biometricAttribute;

	@MockBean
	private TemplateRepository templateRepository;
	private Template template;
	private TemplateDto templateDto;

	private TemplateFileFormatDto templateFileFormatDto;
	private TemplateFileFormat templateFileFormat;

	@MockBean
	private TemplateFileFormatRepository templateFileFormatRepository;

	private RequestDto<TemplateFileFormatDto> templateFileFormatRequestDto = new RequestDto<TemplateFileFormatDto>();

	@MockBean
	private TemplateTypeRepository templateTypeRepository;
	private TemplateType templateType;
	private TemplateTypeDto templateTypeDto;
	@MockBean
	private DeviceSpecificationRepository deviceSpecificationRepository;

	@MockBean
	DeviceTypeRepository deviceTypeRepository;

	@MockBean
	MachineSpecificationRepository machineSpecificationRepository;

	@MockBean
	MachineRepository machineRepository;

	@MockBean
	MachineTypeRepository machineTypeRepository;

	List<DocumentType> documentTypes;

	DocumentType type;

	List<RegistrationCenterType> regCenterTypes;

	RegistrationCenterType regCenterType;

	List<IdType> idTypes;

	IdType idType;

	List<DocumentCategory> entities;

	DocumentCategory category;

	List<BlacklistedWords> words;

	@MockBean
	private GenderTypeRepository genderTypeRepository;

	private List<Gender> genderTypes;

	private List<Gender> genderTypesNull;

	private GenderID genderId;

	@MockBean
	private HolidayRepository holidayRepository;

	private List<Holiday> holidays;

	@MockBean
	IdTypeRepository idTypeRepository;

	@MockBean
	ReasonCategoryRepository reasonCategoryRepository;

	@MockBean
	ReasonListRepository reasonListRepository;

	@MockBean
	RegistrationCenterTypeRepository registrationCenterTypeRepository;

	private List<ReasonCategory> reasoncategories;

	private List<ReasonList> reasonList;

	private PostReasonCategoryDto postReasonCategoryDto;

	private ReasonListDto reasonListDto;

	private CodeLangCodeAndRsnCatCodeID reasonListId;

	private String reasonListRequest = null;

	private String reasonCategoryRequest = null;

	@MockBean
	RegistrationCenterHistoryRepository repositoryCenterHistoryRepository;

	RegistrationCenterHistory center;
	Device device;
	private DeviceDto deviceDto;

	Title title;
	List<RegistrationCenterHistory> centers = new ArrayList<>();

	@MockBean
	RegistrationCenterRepository registrationCenterRepository;

	RegistrationCenter registrationCenter;
	RegistrationCenter banglore;
	RegistrationCenter chennai;

	List<RegistrationCenter> registrationCenters = new ArrayList<>();

	private RegistrationCenterUserMachine registrationCenterUserMachine;
	@MockBean
	private RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;

	@MockBean
	private RegistrationCenterMachineUserRepository registrationCenterMachineUserRepository;

	RegistrationCenterUserMachineHistory registrationCenterUserMachineHistory;

	RegistrationCenterMachineUserHistoryID registrationCenterUserMachineHistoryId;

	List<RegistrationCenterUserMachineHistory> registrationCenterUserMachineHistories = new ArrayList<>();

	RegistrationCenterMachineDeviceHistoryID registrationCenterMachineDeviceHistoryID = null;

	RegistrationCenterMachineDeviceID rcmdIdH = null;
	@MockBean
	private TitleRepository titleRepository;

	private List<Title> titleList;

	private List<Title> titlesNull;

	private CodeAndLanguageCodeID titleId;

	@MockBean
	private LanguageRepository languageRepository;

	private LanguageDto languageDto;

	private Language language;

	private Gender genderType;

	private GenderTypeDto genderDto;

	private ValidDocument validDocument;
	private Holiday holiday;

	@MockBean
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;
	@MockBean
	private RegistrationCenterDeviceHistoryRepository registrationCenterDeviceHistoryRepository;
	private RegistrationCenterDeviceDto registrationCenterDeviceDto;
	private RegistrationCenterDevice registrationCenterDevice;
	private RegistrationCenterDeviceHistory registrationCenterDeviceHistory;
	@MockBean
	private RegistrationCenterMachineRepository registrationCenterMachineRepository;
	@MockBean
	private RegistrationCenterMachineHistoryRepository registrationCenterMachineHistoryRepository;
	private RegistrationCenterMachineDto registrationCenterMachineDto;
	private RegistrationCenterMachine registrationCenterMachine;
	private RegistrationCenterMachineHistory registrationCenterMachineHistory;
	@MockBean
	private RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;
	@MockBean
	private RegistrationCenterMachineDeviceHistoryRepository registrationCenterMachineDeviceHistoryRepository;
	private RegistrationCenterMachineDeviceDto registrationCenterMachineDeviceDto;
	private RegistrationCenterMachineDevice registrationCenterMachineDevice;
	private RegistrationCenterMachineDeviceHistory registrationCenterMachineDeviceHistory;

	private ObjectMapper mapper;

	@MockBean
	private MachineHistoryRepository machineHistoryRepository;

	@MockBean
	private DeviceHistoryRepository deviceHistoryRepository;

	
	public static LocalDateTime localDateTimeUTCFormat = LocalDateTime.now();

	public static final DateTimeFormatter UTC_DATE_TIME_FORMAT = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public static final String UTC_DATE_TIME_FORMAT_DATE_STRING = "2018-12-02T02:50:12.208Z";

	@MockBean
	private IndividualTypeRepository individualTypeRepository;
	private IndividualTypeResponseDto individualTypeResponseDto;
	private List<IndividualType> individualTypes = new ArrayList<>();

	@SuppressWarnings("static-access")
	@Before
	public void setUp() {

		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(JSON_STRING_RESPONSE);

		mapper = new ObjectMapper();

		localDateTimeUTCFormat = localDateTimeUTCFormat.parse(UTC_DATE_TIME_FORMAT_DATE_STRING, UTC_DATE_TIME_FORMAT);
		blacklistedSetup();

		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		individualTypeSetup();

		genderTypeSetup();

		holidaySetup();

		idTypeSetup();

		packetRejectionSetup();

		registrationCenterHistorySetup();

		registrationCenterSetup();

		registrationCenterUserMachineSetup();

		titleIntegrationSetup();

		documentCategorySetUp();

		documentTypeSetUp();

		registrationCenterTypeSetUp();

		languageTestSetup();

		addValidDocumentSetUp();

		deviceSetup();

		registrationCenterDeviceSetup();
		registrationCenterMachineSetup();
		registrationCenterMachineDeviceSetup();

		machineSetUp();
		machinetypeSetUp();
		machineSpecificationSetUp();

		DeviceSpecsetUp();
		DevicetypeSetUp();
		deviceHistorySetUp();

		machineHistorySetUp();
		biometricAttributeTestSetup();
		templateSetup();
		templateTypeTestSetup();
		templateFileFormatSetup();
		registrationCenterDeviceHistorySetup();
	}

	private void individualTypeSetup() {
		IndividualType fr = new IndividualType();
		fr.setIndividualTypeID(new CodeAndLanguageCodeID("FR", "eng"));
		fr.setIsActive(true);
		fr.setName("Foreigner");

		IndividualType nfr = new IndividualType();
		nfr.setIndividualTypeID(new CodeAndLanguageCodeID("NFR", "eng"));
		nfr.setIsActive(true);
		nfr.setName("Non-Foreigner");

		individualTypes.add(fr);
		individualTypes.add(nfr);

		individualTypeResponseDto = new IndividualTypeResponseDto();
		IndividualTypeDto frDto = new IndividualTypeDto();
		MapperUtils.map(fr, frDto);
		IndividualTypeDto nfrDto = new IndividualTypeDto();
		MapperUtils.map(nfr, nfrDto);

		individualTypeResponseDto.getIndividualTypes().add(frDto);
		individualTypeResponseDto.getIndividualTypes().add(nfrDto);

	}

	/* Individual type test */
	@Test
	public void getAllIndividualTypeTest() throws Exception {
		when(individualTypeRepository.findAll()).thenReturn(individualTypes);
		mockMvc.perform(get("/v1.0/individualtypes").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void getAllIndividualTypeNoTypeFoundTest() throws Exception {
		when(individualTypeRepository.findAll()).thenReturn(null);
		mockMvc.perform(get("/v1.0/individualtypes").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void getAllIndividualTypeMasterDataServiceExceptionTest() throws Exception {
		when(individualTypeRepository.findAll()).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/v1.0/individualtypes").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	/* Applicant type code */
	@MockBean
	private ApplicantValidDocumentRepository applicantValidRepository;

	@Test
	public void getValidApplicantTypeTest() throws Exception {

		List<Object[]> list = new ArrayList<>();

		Object[] arr = new Object[9];

		for (int i = 0; i < arr.length; i++) {
			arr[i] = "abc";
		}

		list.add(arr);
		when(applicantValidRepository.getDocumentCategoryAndTypesForApplicantCode(Mockito.anyString(),
				Mockito.anyList())).thenReturn(list);
		mockMvc.perform(get("/v1.0/applicanttype/001/languages?languages=eng&languages=fra&languages=ara"))
				.andExpect(status().isOk());
	}

	@Test
	public void getValidApplicantTypeMasterDataServiceExceptionTest() throws Exception {
		when(applicantValidRepository.getDocumentCategoryAndTypesForApplicantCode(Mockito.anyString(),
				Mockito.anyList())).thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		mockMvc.perform(get("/v1.0/applicanttype/001/languages?languages=eng&languages=fra&languages=ara"))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getValidApplicantTypeNoTypeFoundTest() throws Exception {
		when(applicantValidRepository.getDocumentCategoryAndTypesForApplicantCode(Mockito.anyString(),
				Mockito.anyList())).thenReturn(null);
		mockMvc.perform(get("/v1.0/applicanttype/001/languages?languages=eng&languages=fra&languages=ara"))
				.andExpect(status().isOk());
	}

	private DeviceType deviceType;
	private DeviceTypeDto deviceTypeDto;

	private void DevicetypeSetUp() {

		deviceType = new DeviceType();
		deviceType.setCode("1000");
		deviceType.setLangCode("eng");
		deviceType.setName("Laptop");
		deviceType.setIsActive(true);
		deviceType.setDescription("Laptop Description");

		deviceTypeDto = new DeviceTypeDto();
		MapperUtils.map(deviceType, deviceTypeDto);

	}

	private MachineSpecification machineSpecification;
	private MachineSpecificationDto machineSpecificationDto;
	private List<MachineSpecification> machineSpecList;

	private void machineSpecificationSetUp() {

		machineSpecification = new MachineSpecification();
		machineSpecification.setId("1000");
		machineSpecification.setLangCode("eng");
		machineSpecification.setName("laptop");
		machineSpecification.setIsActive(true);
		machineSpecification.setDescription("HP Description");
		machineSpecification.setBrand("HP");
		machineSpecification.setMachineTypeCode("1231");
		machineSpecification.setLangCode("eng");
		machineSpecification.setMinDriverversion("version 0.1");
		machineSpecification.setModel("3168ngw");

		machineSpecList = new ArrayList<>();
		machineSpecList.add(machineSpecification);

		machineSpecificationDto = new MachineSpecificationDto();
		MapperUtils.map(machineSpecification, machineSpecificationDto);

	}

	private MachineType machineType;
	private MachineTypeDto machineTypeDto;

	private void machinetypeSetUp() {
		mapper = new ObjectMapper();

		machineType = new MachineType();
		machineType.setCode("1000");
		machineType.setLangCode("eng");
		machineType.setName("HP");
		machineType.setIsActive(true);
		machineType.setDescription("HP Description");

		machineTypeDto = new MachineTypeDto();
		MapperUtils.map(machineType, machineTypeDto);

	}

	private void biometricAttributeTestSetup() {
		// creating data coming from user
		biometricAttributeDto = new BiometricAttributeDto();
		biometricAttributeDto.setCode("BA222");
		biometricAttributeDto.setLangCode("eng");
		biometricAttributeDto.setName("black_iric");
		biometricAttributeDto.setBiometricTypeCode("iric");
		biometricAttributeDto.setIsActive(Boolean.TRUE);

		biometricAttribute = new BiometricAttribute();
		biometricAttribute.setCode("BA222");
		biometricAttribute.setLangCode("eng");
		biometricAttribute.setName("black_iric");
		biometricAttribute.setBiometricTypeCode("iric");
		biometricAttribute.setIsActive(Boolean.TRUE);

	}

	private void templateSetup() {
		templateDto = new TemplateDto();
		templateDto.setId("T222");
		templateDto.setLangCode("eng");
		templateDto.setName("Email template");
		templateDto.setTemplateTypeCode("EMAIL");
		templateDto.setFileFormatCode("XML");
		templateDto.setModuleId("preregistation");
		templateDto.setIsActive(Boolean.TRUE);

		template = new Template();
		template.setId("T222");
		template.setLangCode("eng");
		template.setName("Email template");
		template.setTemplateTypeCode("EMAIL");
		template.setFileFormatCode("XML");
		template.setModuleId("preregistation");
		template.setIsActive(Boolean.TRUE);

	}

	private void templateTypeTestSetup() {

		templateTypeDto = new TemplateTypeDto();
		templateTypeDto.setCode("TTC222");
		templateTypeDto.setLangCode("eng");
		templateTypeDto.setDescription("Template type desc");
		templateTypeDto.setIsActive(Boolean.TRUE);

		templateType = new TemplateType();
		templateType.setCode("TTC222");
		templateType.setLangCode("eng");
		templateType.setDescription("Template type desc");
		templateType.setIsActive(Boolean.TRUE);

	}

	List<MachineHistory> machineHistoryList;

	private void machineHistorySetUp() {
		LocalDateTime eDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		LocalDateTime vDate = LocalDateTime.of(2022, Month.JANUARY, 1, 10, 10, 30);
		machineHistoryList = new ArrayList<>();
		MachineHistory machineHistory = new MachineHistory();
		machineHistory.setId("1000");
		machineHistory.setName("Laptop");
		machineHistory.setIpAddress("129.0.0.0");
		machineHistory.setMacAddress("129.0.0.0");
		machineHistory.setEffectDateTime(eDate);
		machineHistory.setValidityDateTime(vDate);
		machineHistory.setIsActive(true);
		machineHistory.setLangCode("eng");
		machineHistoryList.add(machineHistory);

	}

	List<DeviceHistory> deviceHistoryList;

	private void deviceHistorySetUp() {
		LocalDateTime eDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		LocalDateTime vDate = LocalDateTime.of(2022, Month.JANUARY, 1, 10, 10, 30);
		deviceHistoryList = new ArrayList<>();
		DeviceHistory deviceHistory = new DeviceHistory();
		deviceHistory.setId("1000");
		deviceHistory.setName("Laptop");
		deviceHistory.setIpAddress("129.0.0.0");
		deviceHistory.setMacAddress("129.0.0.0");
		deviceHistory.setEffectDateTime(eDate);
		deviceHistory.setValidityDateTime(vDate);
		deviceHistory.setIsActive(true);
		deviceHistory.setLangCode("eng");
		deviceHistoryList.add(deviceHistory);

	}

	List<DeviceSpecification> deviceSpecList;
	DeviceSpecification deviceSpecification;
	DeviceSpecificationDto deviceSpecificationDto;

	@Before
	public void DeviceSpecsetUp() {

		deviceSpecList = new ArrayList<>();

		deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("1000");
		deviceSpecification.setName("Laptop");
		deviceSpecification.setBrand("HP");
		deviceSpecification.setModel("G-Series");
		deviceSpecification.setMinDriverversion("version 7");
		deviceSpecification.setDescription("HP Laptop");
		deviceSpecification.setIsActive(true);

		deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("1000");
		deviceSpecification.setLangCode("ENG");
		deviceSpecification.setName("laptop");
		deviceSpecification.setIsActive(true);
		deviceSpecification.setDescription("HP Description");
		deviceSpecification.setBrand("HP");
		deviceSpecification.setDeviceTypeCode("1231");
		deviceSpecification.setLangCode("eng");
		deviceSpecification.setMinDriverversion("version 0.1");
		deviceSpecification.setModel("3168ngw");
		deviceSpecList.add(deviceSpecification);

		deviceSpecificationDto = new DeviceSpecificationDto();
		MapperUtils.map(deviceSpecification, deviceSpecificationDto);
	}

	private List<Machine> machineList;
	private Machine machine;
	private MachineHistory machineHistory;
	private MachineDto machineDto;

	LocalDateTime specificDate;
	String machineJson;

	private void machineSetUp() {

		specificDate = LocalDateTime.now(ZoneId.of("UTC"));
		machineList = new ArrayList<>();
		machine = new Machine();
		machine.setId("1000");
		machine.setLangCode("eng");
		machine.setName("HP");
		machine.setIpAddress("129.0.0.0");
		machine.setMacAddress("178.0.0.0");
		machine.setMachineSpecId("1010");
		machine.setSerialNum("123");
		machine.setIsActive(true);
		// machine.setValidityDateTime(specificDate);
		machineList.add(machine);

		machineHistory = new MachineHistory();

		MapperUtils.mapFieldValues(machine, machineHistory);
		machineDto = new MachineDto();
		MapperUtils.map(machine, machineDto);

	}

	private void registrationCenterDeviceSetup() {
		registrationCenterDeviceDto = new RegistrationCenterDeviceDto();
		registrationCenterDeviceDto.setDeviceId("101");
		registrationCenterDeviceDto.setRegCenterId("1");
		registrationCenterDeviceDto.setLangCode("eng");
		registrationCenterDeviceDto.setIsActive(true);

		registrationCenterDevice = new RegistrationCenterDevice();
		RegistrationCenterDeviceID rcId = new RegistrationCenterDeviceID();
		rcId.setDeviceId(registrationCenterDeviceDto.getDeviceId());
		rcId.setRegCenterId(registrationCenterDeviceDto.getRegCenterId());
		registrationCenterDevice.setRegistrationCenterDevicePk(rcId);
		registrationCenterDevice.setIsActive(true);
		registrationCenterDevice.setLangCode("eng");
		registrationCenterDevice.setCreatedBy("admin");
		registrationCenterDevice.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		registrationCenterDevice.setIsDeleted(false);
		registrationCenterDeviceHistory = new RegistrationCenterDeviceHistory();
		RegistrationCenterDeviceHistoryPk rcIdH = new RegistrationCenterDeviceHistoryPk();
		rcIdH.setDeviceId(rcId.getDeviceId());
		rcIdH.setRegCenterId(rcId.getRegCenterId());
		registrationCenterDeviceHistory.setRegistrationCenterDeviceHistoryPk(rcIdH);
		registrationCenterDeviceHistory.setCreatedDateTime(registrationCenterDevice.getCreatedDateTime());
		registrationCenterDeviceHistory.setCreatedBy("admin");
		registrationCenterDeviceHistory.setIsActive(true);
		registrationCenterDeviceHistory.setLangCode("eng");
	}

	private void registrationCenterMachineSetup() {
		registrationCenterMachineDto = new RegistrationCenterMachineDto();
		registrationCenterMachineDto.setMachineId("1789");
		registrationCenterMachineDto.setRegCenterId("1");
		registrationCenterMachineDto.setLangCode("eng");
		registrationCenterMachineDto.setIsActive(true);
		registrationCenterMachine = new RegistrationCenterMachine();
		RegistrationCenterMachineID rmId = new RegistrationCenterMachineID();
		rmId.setMachineId(registrationCenterMachineDto.getMachineId());
		rmId.setRegCenterId(registrationCenterMachineDto.getRegCenterId());
		registrationCenterMachine.setRegistrationCenterMachinePk(rmId);
		registrationCenterMachine.setIsActive(true);
		registrationCenterMachine.setLangCode("eng");
		registrationCenterMachine.setCreatedBy("admin");
		registrationCenterMachine.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		registrationCenterMachine.setIsDeleted(false);

		registrationCenterMachineHistory = new RegistrationCenterMachineHistory();
		RegistrationCenterMachineHistoryID rmIdH = new RegistrationCenterMachineHistoryID();
		rmIdH.setMachineId(rmId.getMachineId());
		rmIdH.setRegCenterId(rmId.getRegCenterId());
		registrationCenterMachineHistory.setRegistrationCenterMachineHistoryPk(rmIdH);
		registrationCenterMachineHistory.setCreatedDateTime(registrationCenterMachine.getCreatedDateTime());
		registrationCenterMachineHistory.setCreatedBy("admin");
		registrationCenterMachineHistory.setIsActive(true);
		registrationCenterMachineHistory.setLangCode("eng");
	}

	private void registrationCenterMachineDeviceSetup() {
		registrationCenterMachineDeviceDto = new RegistrationCenterMachineDeviceDto();
		registrationCenterMachineDeviceDto.setMachineId("1789");
		registrationCenterMachineDeviceDto.setDeviceId("101");
		registrationCenterMachineDeviceDto.setRegCenterId("1");
		registrationCenterMachineDeviceDto.setLangCode("eng");
		registrationCenterMachineDeviceDto.setIsActive(true);

		registrationCenterMachineDevice = new RegistrationCenterMachineDevice();
		RegistrationCenterMachineDeviceID rcmdId = new RegistrationCenterMachineDeviceID();
		rcmdId.setDeviceId("101");
		rcmdId.setMachineId("1789");
		rcmdId.setRegCenterId("1");
		registrationCenterMachineDevice.setRegistrationCenterMachineDevicePk(rcmdId);
		registrationCenterMachineDevice.setIsActive(true);
		registrationCenterMachineDevice.setLangCode("eng");
		registrationCenterMachineDevice.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		registrationCenterMachineDevice.setCreatedBy("admin");

		registrationCenterMachineDeviceHistory = new RegistrationCenterMachineDeviceHistory();
		rcmdIdH = new RegistrationCenterMachineDeviceID();
		rcmdIdH.setDeviceId("101");
		rcmdIdH.setMachineId("1789");
		rcmdIdH.setRegCenterId("1");
		registrationCenterMachineDeviceHistoryID = new RegistrationCenterMachineDeviceHistoryID();
		registrationCenterMachineDeviceHistoryID.setDeviceId("101");
		registrationCenterMachineDeviceHistoryID.setMachineId("1789");
		registrationCenterMachineDeviceHistoryID.setRegCenterId("1");
		registrationCenterMachineDeviceHistoryID
				.setEffectivetimes(registrationCenterMachineDevice.getCreatedDateTime());
		registrationCenterMachineDeviceHistory
				.setRegistrationCenterMachineDeviceHistoryPk(registrationCenterMachineDeviceHistoryID);
		registrationCenterMachineDeviceHistory.setCreatedDateTime(registrationCenterMachineDevice.getCreatedDateTime());
		registrationCenterMachineDeviceHistory.setIsActive(true);
		registrationCenterMachineDeviceHistory.setLangCode("eng");
		registrationCenterMachineDeviceHistory.setCreatedBy("admin");

	}

	List<Device> deviceList;
	List<Object[]> objectList;
	DeviceHistory deviceHistory;

	private void deviceSetup() {

		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		Timestamp validDateTime = Timestamp.valueOf(specificDate);
		deviceDto = new DeviceDto();
		deviceDto.setDeviceSpecId("123");
		deviceDto.setId("1");
		deviceDto.setIpAddress("asd");
		deviceDto.setIsActive(true);
		deviceDto.setLangCode("eng");
		deviceDto.setMacAddress("asd");
		deviceDto.setName("asd");
		deviceDto.setSerialNum("asd");

		deviceList = new ArrayList<>();
		device = new Device();
		device.setId("1000");
		device.setName("Printer");
		device.setLangCode("eng");
		device.setIsActive(true);
		device.setMacAddress("127.0.0.0");
		device.setIpAddress("127.0.0.10");
		device.setSerialNum("234");
		device.setDeviceSpecId("234");
		device.setValidityDateTime(specificDate);
		deviceList.add(device);

		objectList = new ArrayList<>();
		Object objects[] = { "1001", "Laptop", "129.0.0.0", "123", "129.0.0.0", "1212", "eng", true, validDateTime,
				"LaptopCode" };
		objectList.add(objects);

		deviceHistory = new DeviceHistory();

	}

	private void templateFileFormatSetup() {
		templateFileFormatDto = new TemplateFileFormatDto();
		templateFileFormatDto.setCode("xml");
		templateFileFormatDto.setLangCode("eng");
		templateFileFormatDto.setIsActive(true);
		templateFileFormat = new TemplateFileFormat();
		templateFileFormat.setCode("xml");
		templateFileFormat.setLangCode("eng");
		templateFileFormat.setIsActive(true);

		templateFileFormatRequestDto.setRequest(templateFileFormatDto);
	}

	private void addValidDocumentSetUp() {
		validDocument = new ValidDocument();
		validDocument.setDocTypeCode("ttt");
		validDocument.setDocCategoryCode("ddd");
	}

	private void languageTestSetup() {
		// creating data coming from user

		languageDto = new LanguageDto();
		languageDto.setCode("eng");
		languageDto.setName("terman");
		languageDto.setIsActive(Boolean.TRUE);

		language = new Language();
		language.setCode("eng");
		language.setName("terman");
		language.setIsActive(Boolean.TRUE);
	}

	private void documentTypeSetUp() {
		type = new DocumentType();
		type.setCode("DT001");
		documentTypes = new ArrayList<>();
		documentTypes.add(type);
	}

	private void registrationCenterTypeSetUp() {
		regCenterType = new RegistrationCenterType();
		regCenterType.setCode("T01");
		regCenterTypes = new ArrayList<>();
		regCenterTypes.add(regCenterType);

	}

	private void documentCategorySetUp() {
		category = new DocumentCategory();
		category.setCode("DC001");
		entities = new ArrayList<>();
		entities.add(category);
	}

	private void titleIntegrationSetup() {
		titleList = new ArrayList<>();
		title = new Title();
		titleId = new CodeAndLanguageCodeID();
		titleId.setLangCode("eng");
		titleId.setCode("ABC");
		title.setIsActive(true);
		title.setCreatedBy("Ajay");
		title.setCreatedDateTime(null);
		title.setId(titleId);
		title.setTitleDescription("AAAAAAAAAAAA");
		title.setTitleName("HELLO");
		title.setUpdatedBy("XYZ");
		title.setUpdatedDateTime(null);
		titleList.add(title);
	}

	private void registrationCenterUserMachineSetup() {
		registrationCenterUserMachine = new RegistrationCenterUserMachine();
		RegistrationCenterMachineUserID registrationCenterMachineUserID = new RegistrationCenterMachineUserID();
		registrationCenterMachineUserID.setCntrId("REG001");
		registrationCenterMachineUserID.setUsrId("QC001");
		registrationCenterMachineUserID.setMachineId("MAC001");
		registrationCenterUserMachine.setLangCode("eng");
		registrationCenterUserMachine.setRegistrationCenterMachineUserID(registrationCenterMachineUserID);
		registrationCenterUserMachineHistory = new RegistrationCenterUserMachineHistory("1", "1", "1",
				LocalDateTime.now().minusDays(1), "eng");
	}

	private void registrationCenterSetup() {
		registrationCenter = new RegistrationCenter();
		registrationCenter.setId("1");
		registrationCenter.setName("bangalore");
		registrationCenter.setLatitude("12.9180722");
		registrationCenter.setLongitude("77.5028792");
		registrationCenter.setLangCode("eng");
		registrationCenter.setHolidayLocationCode("KAR");
		registrationCenters.add(registrationCenter);

		Location location = new Location();
		location.setCode("BLR");

		banglore = new RegistrationCenter();
		banglore.setId("1");
		banglore.setName("bangalore");
		banglore.setLatitude("12.9180722");
		banglore.setLongitude("77.5028792");
		banglore.setLangCode("eng");
		banglore.setLocationCode("LOC");
		chennai = new RegistrationCenter();
		chennai.setId("2");
		chennai.setName("Bangalore Central");
		chennai.setLangCode("eng");
		chennai.setLocationCode("LOC");
		registrationCenters.add(banglore);
		registrationCenters.add(chennai);

	}

	private void registrationCenterHistorySetup() {
		center = new RegistrationCenterHistory();
		center.setId("1");
		center.setName("bangalore");
		center.setLatitude("12.9180722");
		center.setLongitude("77.5028792");
		center.setLangCode("eng");
		center.setLocationCode("BLR");
		centers.add(center);
	}

	private void packetRejectionSetup() {
		ReasonCategory reasonCategory = new ReasonCategory();
		ReasonList reasonListObj = new ReasonList();
		reasonListDto = new ReasonListDto();
		postReasonCategoryDto = new PostReasonCategoryDto();
		postReasonCategoryDto.setCode("RC1");
		postReasonCategoryDto.setDescription("Reason category");
		postReasonCategoryDto.setIsActive(true);
		postReasonCategoryDto.setLangCode("eng");
		postReasonCategoryDto.setName("Reason category");
		reasonListDto.setCode("RL1");
		reasonListDto.setDescription("REASONLIST");
		reasonListDto.setLangCode("eng");
		reasonListDto.setIsActive(true);
		reasonListDto.setName("Reason List 1");
		reasonListDto.setRsnCatCode("RC1");
		reasonList = new ArrayList<>();
		reasonListObj.setCode("RL1");
		reasonListObj.setLangCode("eng");
		reasonListObj.setRsnCatCode("RC1");
		reasonListObj.setDescription("reasonList");
		reasonList.add(reasonListObj);
		reasonCategory.setReasonList(reasonList);
		reasonCategory.setCode("RC1");
		reasonCategory.setLangCode("eng");
		reasoncategories = new ArrayList<>();
		reasoncategories.add(reasonCategory);
		titleId = new CodeAndLanguageCodeID();
		titleId.setCode("RC1");
		titleId.setLangCode("eng");
		reasonListId = new CodeLangCodeAndRsnCatCodeID();
		reasonListId.setCode("RL1");
		reasonListId.setLangCode("eng");
		reasonListId.setRsnCatCode("RC1");
		RequestDto<ReasonListDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.create.packetrejection.reason");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(reasonListDto);
		RequestDto<PostReasonCategoryDto> requestDto1 = new RequestDto<>();
		requestDto1.setId("mosip.create.packetrejection.reason");
		requestDto1.setVer("1.0.0");
		requestDto1.setRequest(postReasonCategoryDto);
		try {
			reasonListRequest = mapper.writeValueAsString(requestDto);
			reasonCategoryRequest = mapper.writeValueAsString(requestDto1);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
	}

	private void idTypeSetup() {
		idType = new IdType();
		idType.setIsActive(true);
		idType.setCreatedBy("testCreation");
		idType.setLangCode("eng");
		idType.setCode("POA");
		idType.setDescr("Proof Of Address");
		idTypes = new ArrayList<>();
		idTypes.add(idType);
	}

	private void holidaySetup() {
		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		LocalDate date = LocalDate.of(2018, Month.NOVEMBER, 7);
		holidays = new ArrayList<>();
		holiday = new Holiday();

		holiday = new Holiday();
		holiday.setHolidayId(new HolidayID("KAR", date, "eng", "Diwali"));
		holiday.setId(1);
		holiday.setCreatedBy("John");
		holiday.setCreatedDateTime(specificDate);
		holiday.setHolidayDesc("Diwali");
		holiday.setIsActive(true);

		Holiday holiday2 = new Holiday();
		holiday2.setHolidayId(new HolidayID("KAH", date, "eng", "Durga Puja"));
		holiday2.setId(1);
		holiday2.setCreatedBy("John");
		holiday2.setCreatedDateTime(specificDate);
		holiday2.setHolidayDesc("Diwali");
		holiday2.setIsActive(true);

		holidays.add(holiday);
		holidays.add(holiday2);
	}

	private void genderTypeSetup() {

		genderDto = new GenderTypeDto();
		genderDto.setCode("GEN01");
		genderDto.setGenderName("Male");
		genderDto.setIsActive(true);
		genderDto.setLangCode("eng");

		genderTypes = new ArrayList<>();
		genderTypesNull = new ArrayList<>();
		genderType = new Gender();
		genderId = new GenderID();
		genderId.setGenderCode("GEN01");
		genderId.setGenderName("Male");
		genderType.setIsActive(true);
		genderType.setCreatedBy("MosipAdmin");
		genderType.setCreatedDateTime(null);
		genderType.setIsDeleted(false);
		genderType.setDeletedDateTime(null);
		genderType.setLangCode("eng");
		genderType.setUpdatedBy("Dom");
		genderType.setUpdatedDateTime(null);
		genderTypes.add(genderType);
	}

	private void blacklistedSetup() {
		words = new ArrayList<>();

		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setWord("abc");
		blacklistedWords.setLangCode("e");
		blacklistedWords.setDescription("no description available");

		words.add(blacklistedWords);
		blacklistedWords.setLangCode("TST");
		blacklistedWords.setIsActive(true);
		blacklistedWords.setWord("testword");
	}

	@Before
	public void LocationSetup() {
		locationHierarchies = new ArrayList<>();
		Location locationHierarchy = new Location();
		locationHierarchy.setCode("PAT");
		locationHierarchy.setName("PATANA");
		locationHierarchy.setHierarchyLevel((short)2);
		locationHierarchy.setHierarchyName("Distic");
		locationHierarchy.setParentLocCode("BHR");
		locationHierarchy.setLangCode("ENG");
		locationHierarchy.setCreatedBy("admin");
		locationHierarchy.setUpdatedBy("admin");
		locationHierarchy.setIsActive(true);
		locationHierarchies.add(locationHierarchy);
		Location locationHierarchy1 = new Location();
		locationHierarchy1.setCode("BX");
		locationHierarchy1.setName("BAXOR");
		locationHierarchy1.setHierarchyLevel((short)2);
		locationHierarchy1.setHierarchyName("Distic");
		locationHierarchy1.setParentLocCode("BHR");
		locationHierarchy1.setLangCode("ENG");
		locationHierarchy1.setCreatedBy("admin");
		locationHierarchy1.setUpdatedBy("admin");
		locationHierarchy1.setIsActive(true);
		locationHierarchies.add(locationHierarchy1);

	}

	private RegistrationCenterDeviceHistoryDto registrationCenterDeviceHistoryDto;

	private void registrationCenterDeviceHistorySetup() {
		registrationCenterDeviceHistoryDto = new RegistrationCenterDeviceHistoryDto();
		registrationCenterDeviceHistoryDto.setDeviceId("101");
		registrationCenterDeviceHistoryDto.setRegCenterId("1");
		registrationCenterDeviceHistoryDto.setEffectivetimes(localDateTimeUTCFormat);

		registrationCenterDeviceHistory = new RegistrationCenterDeviceHistory();
		RegistrationCenterDeviceHistoryPk rc = new RegistrationCenterDeviceHistoryPk();
		rc.setDeviceId(registrationCenterDeviceHistoryDto.getDeviceId());
		rc.setRegCenterId(registrationCenterDeviceHistoryDto.getRegCenterId());
		rc.setEffectivetimes(LocalDateTime.now(ZoneId.of("UTC")));
		registrationCenterDeviceHistory.setRegistrationCenterDeviceHistoryPk(rc);
		registrationCenterDeviceHistory.setIsActive(true);

	}

	// -------RegistrationCenter mapping-------------------------

	@Test
	public void mapRegistrationCenterAndDeviceTest() throws Exception {
		RequestDto<RegistrationCenterDeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.deviceid");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(registrationCenterDeviceDto);
		String content = mapper.writeValueAsString(requestDto);
		when(registrationCenterDeviceRepository.create(Mockito.any())).thenReturn(registrationCenterDevice);
		when(registrationCenterDeviceHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterDeviceHistory);

		mockMvc.perform(post("/v1.0/registrationcenterdevice").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void mapRegistrationCenterAndDeviceDataAccessLayerExceptionTest() throws Exception {
		RequestDto<RegistrationCenterDeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.deviceid");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(registrationCenterDeviceDto);
		String content = mapper.writeValueAsString(requestDto);
		when(registrationCenterDeviceRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		when(registrationCenterDeviceHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterDeviceHistory);

		mockMvc.perform(post("/v1.0/registrationcenterdevice").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void mapRegistrationCenterAndDeviceBadRequestTest() throws Exception {
		RequestDto<RegistrationCenterDeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.deviceid");
		requestDto.setVer("1.0.0");
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/registrationcenterdevice").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteRegistrationCenterAndDeviceMappingTest() throws Exception {
		when(registrationCenterDeviceRepository.findAllNondeletedMappings(Mockito.any()))
				.thenReturn(Optional.of(registrationCenterDevice));
		when(registrationCenterDeviceHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterDeviceHistory);
		when(registrationCenterDeviceRepository.update(Mockito.any())).thenReturn(registrationCenterDevice);
		mockMvc.perform(delete("/v1.0/registrationcenterdevice/RC001/DC001")).andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterAndDeviceMappingDataNotFoundExceptionTest() throws Exception {
		when(registrationCenterDeviceRepository.findAllNondeletedMappings(Mockito.any())).thenReturn(Optional.empty());
		mockMvc.perform(delete("/v1.0/registrationcenterdevice/RC001/DC001")).andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterAndDeviceMappingDataAccessLayerExceptionTest() throws Exception {
		when(registrationCenterDeviceRepository.findAllNondeletedMappings(Mockito.any()))
				.thenReturn(Optional.of(registrationCenterDevice));
		when(registrationCenterDeviceHistoryRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		mockMvc.perform(delete("/v1.0/registrationcenterdevice/RC001/DC001"))
				.andExpect(status().isInternalServerError());
	}

	// -------RegistrationCenterMachine mapping-------------------------

	@Test
	public void mapRegistrationCenterAndMachineTest() throws Exception {
		RequestDto<RegistrationCenterMachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(registrationCenterMachineDto);
		String content = mapper.writeValueAsString(requestDto);
		when(registrationCenterMachineRepository.create(Mockito.any())).thenReturn(registrationCenterMachine);
		when(registrationCenterMachineHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterMachineHistory);
		mockMvc.perform(
				post("/v1.0/registrationcentermachine").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void mapRegistrationCenterAndMachineDataAccessLayerExceptionTest() throws Exception {
		RequestDto<RegistrationCenterMachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(registrationCenterMachineDto);
		String content = mapper.writeValueAsString(requestDto);
		when(registrationCenterMachineRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		when(registrationCenterMachineHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterMachineHistory);
		mockMvc.perform(
				post("/v1.0/registrationcentermachine").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void mapRegistrationCenterAndMachineBadRequestTest() throws Exception {
		RequestDto<RegistrationCenterMachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid");
		requestDto.setVer("1.0.0");
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(
				post("/v1.0/registrationcentermachine").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterAndMachineMappingTest() throws Exception {
		when(registrationCenterMachineRepository.findAllNondeletedMappings(Mockito.any()))
				.thenReturn(Optional.of(registrationCenterMachine));
		when(registrationCenterMachineHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterMachineHistory);
		when(registrationCenterMachineRepository.update(Mockito.any())).thenReturn(registrationCenterMachine);
		mockMvc.perform(delete("/v1.0/registrationcentermachine/RC001/MC001")).andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterAndMachineMappingDataNotFoundExceptionTest() throws Exception {
		when(registrationCenterMachineRepository.findAllNondeletedMappings(Mockito.any())).thenReturn(Optional.empty());
		mockMvc.perform(delete("/v1.0/registrationcentermachine/RC001/MC001")).andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterAndMachineMappingDataAccessLayerExceptionTest() throws Exception {
		when(registrationCenterMachineRepository.findAllNondeletedMappings(Mockito.any()))
				.thenReturn(Optional.of(registrationCenterMachine));
		when(registrationCenterMachineHistoryRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		mockMvc.perform(delete("/v1.0/registrationcentermachine/RC001/MC001"))
				.andExpect(status().isInternalServerError());
	}

	// -------RegistrationCentermachineDevice mapping-------------------------

	@Test
	public void mapRegistrationCenterMachineAndDeviceTest() throws Exception {
		RequestDto<RegistrationCenterMachineDeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid.deviceid");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(registrationCenterMachineDeviceDto);
		String content = mapper.writeValueAsString(requestDto);
		when(registrationCenterMachineDeviceRepository.create(Mockito.any()))
				.thenReturn(registrationCenterMachineDevice);
		when(registrationCenterMachineDeviceHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterMachineDeviceHistory);
		mockMvc.perform(
				post("/v1.0/registrationcentermachinedevice").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void mapRegistrationCenterMachineAndDeviceDataAccessLayerExceptionTest() throws Exception {
		RequestDto<RegistrationCenterMachineDeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid.deviceid");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(registrationCenterMachineDeviceDto);
		String content = mapper.writeValueAsString(requestDto);
		when(registrationCenterMachineDeviceRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		when(registrationCenterMachineDeviceHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterMachineDeviceHistory);
		mockMvc.perform(
				post("/v1.0/registrationcentermachinedevice").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void mapRegistrationCenterMachineAndDeviceBadRequestTest() throws Exception {
		RequestDto<RegistrationCenterMachineDeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid.deviceid");
		requestDto.setVer("1.0.0");
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(
				post("/v1.0/registrationcentermachinedevice").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteRegCenterMachineDeviceTest() throws Exception {
		when(registrationCenterMachineDeviceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(registrationCenterMachineDevice);
		when(registrationCenterMachineDeviceRepository.update(Mockito.any()))
				.thenReturn(registrationCenterMachineDevice);
		when(registrationCenterMachineDeviceHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterMachineDeviceHistory);
		mockMvc.perform(delete("/v1.0/registrationcentermachinedevice/1/1000/1000")).andExpect(status().isOk());
	}

	@Test
	public void deleteRegCenterMachineDeviceDataNotFoundTest() throws Exception {
		when(registrationCenterMachineDeviceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		when(registrationCenterMachineDeviceRepository.update(Mockito.any()))
				.thenReturn(registrationCenterMachineDevice);
		when(registrationCenterMachineDeviceHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterMachineDeviceHistory);
		mockMvc.perform(delete("/v1.0/registrationcentermachinedevice/1/1000/1000")).andExpect(status().isOk());
	}

	@Test
	public void deleteRegCenterMachineDeviceDataAccessExcpetionTest() throws Exception {
		when(registrationCenterMachineDeviceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		when(registrationCenterMachineDeviceRepository.update(Mockito.any()))
				.thenReturn(registrationCenterMachineDevice);
		when(registrationCenterMachineDeviceHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterMachineDeviceHistory);
		mockMvc.perform(delete("/v1.0/registrationcentermachinedevice/1/1000/1000"))
				.andExpect(status().isInternalServerError());
	}

	// -----------------------------LanguageImplementationTest----------------------------------

	@Test
	public void updateLanguagesTest() throws Exception {
		RequestDto<LanguageDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.language.update");
		requestDto.setVer("1.0.0");

		LanguageDto frenchDto = new LanguageDto();
		frenchDto.setCode("fra");
		frenchDto.setFamily("fra");
		frenchDto.setName("FRENCH");
		frenchDto.setIsActive(true);
		requestDto.setRequest(frenchDto);

		Language french = new Language();
		french.setCode("fra");
		french.setFamily("fra");
		french.setName("french");
		french.setIsActive(true);
		french.setNativeName("french_naiv");
		String content = mapper.writeValueAsString(requestDto);
		when(languageRepository.findLanguageByCode(frenchDto.getCode())).thenReturn(french);
		when(languageRepository.update(Mockito.any())).thenReturn(french);
		mockMvc.perform(put("/v1.0/languages").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());

	}

	@Test
	public void updateLanguagesDataAccessLayerTest() throws Exception {
		RequestDto<LanguageDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.language.update");
		requestDto.setVer("1.0.0");

		LanguageDto frenchDto = new LanguageDto();
		frenchDto.setCode("fra");
		frenchDto.setFamily("french");
		frenchDto.setName("FRENCH");
		frenchDto.setIsActive(true);
		requestDto.setRequest(frenchDto);

		Language french = new Language();
		french.setCode("fra");
		french.setFamily("fra");
		french.setName("french");
		french.setIsActive(true);
		french.setNativeName("french_naiv");
		String content = mapper.writeValueAsString(requestDto);
		when(languageRepository.findLanguageByCode(frenchDto.getCode())).thenReturn(french);
		when(languageRepository.update(Mockito.any())).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(put("/v1.0/languages").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void updateLanguagesNotFoundTest() throws Exception {
		RequestDto<LanguageDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.language.update");
		requestDto.setVer("1.0.0");

		LanguageDto frenchDto = new LanguageDto();
		frenchDto.setCode("FRN");
		frenchDto.setFamily("french");
		frenchDto.setName("FRENCH");
		frenchDto.setIsActive(true);
		requestDto.setRequest(frenchDto);

		Language french = new Language();
		french.setCode("FRN");
		french.setFamily("frn");
		french.setName("french");
		french.setIsActive(true);
		french.setNativeName("french_naiv");
		String content = mapper.writeValueAsString(requestDto);
		when(languageRepository.findLanguageByCode(frenchDto.getCode())).thenReturn(null);
		mockMvc.perform(put("/v1.0/languages").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteLanguagesTest() throws Exception {
		when(languageRepository.findLanguageByCode(languageDto.getCode())).thenReturn(language);
		when(languageRepository.update(Mockito.any())).thenReturn(language);
		mockMvc.perform(delete("/v1.0/languages/{code}", languageDto.getCode())).andExpect(status().isOk());
	}

	@Test
	public void deleteDataAccessLayerLanguagesTest() throws Exception {
		when(languageRepository.findLanguageByCode(languageDto.getCode())).thenReturn(language);
		when(languageRepository.update(Mockito.any())).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(delete("/v1.0/languages/{code}", languageDto.getCode()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteNotFoundLanguagesTest() throws Exception {
		when(languageRepository.findLanguageByCode(languageDto.getCode())).thenReturn(null);
		mockMvc.perform(delete("/v1.0/languages/{code}", languageDto.getCode())).andExpect(status().isOk());
	}

	@Test
	public void saveLanguagesTest() throws Exception {
		RequestDto<LanguageDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(languageDto);
		String content = mapper.writeValueAsString(requestDto);
		when(languageRepository.create(Mockito.any())).thenReturn(language);
		mockMvc.perform(post("/v1.0/languages").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());

	}

	@Test
	public void saveLanguagesDataAccessLayerExceptionTest() throws Exception {
		RequestDto<LanguageDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(languageDto);
		String content = mapper.writeValueAsString(requestDto);
		when(languageRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(post("/v1.0/languages").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void saveLanguagesExceptionTest() throws Exception {
		RequestDto<LanguageDto> requestDto = new RequestDto<>();
		requestDto.setId("");
		requestDto.setVer("1.0.0");
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/languages").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());

	}

	// -----------------------------BlacklistedWordsTest----------------------------------
	@Test
	public void getAllWordsBylangCodeSuccessTest() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenReturn(words);
		mockMvc.perform(get("/v1.0/blacklistedwords/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getAllWordsBylangCodeNullResponseTest() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenReturn(null);
		mockMvc.perform(get("/v1.0/blacklistedwords/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getAllWordsBylangCodeEmptyArrayResponseTest() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenReturn(new ArrayList<>());
		mockMvc.perform(get("/v1.0/blacklistedwords/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getAllWordsBylangCodeFetchExceptionTest() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/blacklistedwords/{langcode}", "ENG")).andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllWordsBylangCodeNullArgExceptionTest() throws Exception {
		mockMvc.perform(get("/v1.0/blacklistedwords/{langcode}", " ")).andExpect(status().isOk());
	}

	// -----------------------------GenderTypeTest----------------------------------
	@Test
	public void getGenderByLanguageCodeFetchExceptionTest() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull("ENG"))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/gendertypes/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getGenderByLanguageCodeNotFoundExceptionTest() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull("ENG"))
				.thenReturn(genderTypesNull);

		mockMvc.perform(get("/v1.0/gendertypes/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void getAllGenderFetchExceptionTest() throws Exception {

		Mockito.when(genderTypeRepository.findAllByIsActiveAndIsDeleted()).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/gendertypes").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getAllGenderNotFoundExceptionTest() throws Exception {

		Mockito.when(genderTypeRepository.findAllByIsActiveAndIsDeleted()).thenReturn(genderTypesNull);

		mockMvc.perform(get("/v1.0/gendertypes").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	public void getGenderByLanguageCodeTest() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(genderTypes);
		mockMvc.perform(get("/v1.0/gendertypes/{languageCode}", "ENG")).andExpect(status().isOk());

	}

	@Test
	public void getAllGendersTest() throws Exception {
		Mockito.when(genderTypeRepository.findAllByIsActiveAndIsDeleted()).thenReturn(genderTypes);
		mockMvc.perform(get("/v1.0/gendertypes")).andExpect(status().isOk());

	}

	// -----------------------------HolidayTest----------------------------------

	@Test
	public void getHolidayAllHolidaysSuccessTest() throws Exception {
		when(holidayRepository.findAllNonDeletedHoliday()).thenReturn(holidays);
		mockMvc.perform(get("/v1.0/holidays")).andExpect(status().isOk());
	}

	@Test
	public void getAllHolidaNoHolidayFoundTest() throws Exception {
		mockMvc.perform(get("/v1.0/holidays")).andExpect(status().isOk());
	}

	@Test
	public void getAllHolidaysHolidayFetchExceptionTest() throws Exception {
		when(holidayRepository.findAllNonDeletedHoliday()).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/holidays")).andExpect(status().isInternalServerError());
	}

	@Test
	public void getHolidayByIdSuccessTest() throws Exception {
		when(holidayRepository.findAllById(any(Integer.class))).thenReturn(holidays);
		mockMvc.perform(get("/v1.0/holidays/{holidayId}", 1)).andExpect(status().isOk());
	}

	@Test
	public void getHolidayByIdHolidayFetchExceptionTest() throws Exception {
		when(holidayRepository.findAllById(any(Integer.class))).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/holidays/{holidayId}", 1)).andExpect(status().isInternalServerError());
	}

	@Test
	public void getHolidayByIdNoHolidayFoundTest() throws Exception {
		mockMvc.perform(get("/v1.0/holidays/{holidayId}", 1)).andExpect(status().isOk());
	}

	@Test
	public void getHolidayByIdAndLangCodeSuccessTest() throws Exception {
		when(holidayRepository.findHolidayByIdAndHolidayIdLangCode(any(Integer.class), anyString()))
				.thenReturn(holidays);
		mockMvc.perform(get("/v1.0/holidays/{holidayId}/{languagecode}", 1, "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getHolidayByIdAndLangCodeHolidayFetchExceptionTest() throws Exception {
		when(holidayRepository.findHolidayByIdAndHolidayIdLangCode(any(Integer.class), anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/holidays/{holidayId}/{languagecode}", 1, "ENG"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getHolidayByIdAndLangCodeHolidayNoDataFoundTest() throws Exception {
		mockMvc.perform(get("/v1.0/holidays/{holidayId}/{languagecode}", 1, "ENG")).andExpect(status().isOk());
	}

	@Test
	public void addHolidayTypeTest() throws Exception {
		String json = "{ \"id\": \"string\", \"request\": { \"holidayDate\": \"2019-01-01\", \"holidayDay\": \"Sunday\", \"holidayDesc\": \"New Year\", \"holidayMonth\": \"January\", \"holidayName\": \"New Year\", \"holidayYear\": \"2019\", \"id\": 1, \"isActive\": true, \"langCode\": \"eng\", \"locationCode\": \"BLR\" }, \"timestamp\": \"2018-12-06T08:49:32.190Z\", \"ver\": \"string\"}";
		when(holidayRepository.create(Mockito.any())).thenReturn(holiday);
		mockMvc.perform(post("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	public void addHolidayTypeLanguageValidationTest() throws Exception {
		String json = "{ \"id\": \"string\", \"request\": { \"holidayDate\": \"2019-01-01\", \"holidayDay\": \"Sunday\", \"holidayDesc\": \"New Year\", \"holidayMonth\": \"January\", \"holidayName\": \"New Year\", \"holidayYear\": \"2019\", \"id\": 1, \"isActive\": true, \"langCode\": \"asd\", \"locationCode\": \"BLR\" }, \"timestamp\": \"2018-12-06T08:49:32.190Z\", \"ver\": \"string\"}";
		when(holidayRepository.create(Mockito.any())).thenReturn(holiday);
		mockMvc.perform(post("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	public void addHolidayTypeExceptionTest() throws Exception {

		String json = "{ \"id\": \"string\", \"request\": { \"holidayDate\": \"2019-01-01\", \"holidayDay\": \"Sunday\", \"holidayDesc\": \"New Year\", \"holidayMonth\": \"January\", \"holidayName\": \"New Year\", \"holidayYear\": \"2019\", \"id\": 1, \"isActive\": true, \"langCode\": \"eng\", \"locationCode\": \"BLR\" }, \"timestamp\": \"2018-12-06T08:49:32.190Z\", \"ver\": \"string\"}";
		when(holidayRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute ", null));
		mockMvc.perform(post("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());

	}

	// -----------------------------IdTypeTest----------------------------------
	@Test
	public void getIdTypesByLanguageCodeFetchExceptionTest() throws Exception {
		when(idTypeRepository.findByLangCode("ENG")).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/v1.0/idtypes/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getIdTypesByLanguageCodeNotFoundExceptionTest() throws Exception {
		List<IdType> idTypeList = new ArrayList<>();
		idTypeList.add(idType);
		when(idTypeRepository.findByLangCode("ENG")).thenReturn(idTypeList);
		mockMvc.perform(get("/v1.0/idtypes/HIN").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void getIdTypesByLanguageCodeTest() throws Exception {
		List<IdType> idTypeList = new ArrayList<>();
		idTypeList.add(idType);
		when(idTypeRepository.findByLangCode("ENG")).thenReturn(idTypeList);
		MvcResult result = mockMvc.perform(get("/v1.0/idtypes/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		IdTypeResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				IdTypeResponseDto.class);
		assertThat(returnResponse.getIdtypes().get(0).getCode(), is("POA"));
	}

	// -----------------------------PacketRejectionTest----------------------------------
	@Test
	public void getAllRjectionReasonTest() throws Exception {
		Mockito.when(reasonCategoryRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull())
				.thenReturn(reasoncategories);
		mockMvc.perform(get("/v1.0/packetrejectionreasons")).andExpect(status().isOk());
	}

	@Test
	public void getAllRejectionReasonByCodeAndLangCodeTest() throws Exception {
		Mockito.when(reasonCategoryRepository.findReasonCategoryByCodeAndLangCode(ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(reasoncategories);
		mockMvc.perform(get("/v1.0/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isOk());
	}

	@Test
	public void getAllRjectionReasonFetchExceptionTest() throws Exception {
		Mockito.when(reasonCategoryRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull())
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/packetrejectionreasons")).andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllRejectionReasonByCodeAndLangCodeFetchExceptionTest() throws Exception {
		Mockito.when(reasonCategoryRepository.findReasonCategoryByCodeAndLangCode(ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllRjectionReasonRecordsNotFoundTest() throws Exception {
		Mockito.when(reasonCategoryRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(null);
		mockMvc.perform(get("/v1.0/packetrejectionreasons")).andExpect(status().isOk());
	}

	@Test
	public void getRjectionReasonByCodeAndLangCodeRecordsNotFoundExceptionTest() throws Exception {
		Mockito.when(reasonCategoryRepository.findReasonCategoryByCodeAndLangCode(ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(null);
		mockMvc.perform(get("/v1.0/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isOk());
	}

	@Test
	public void getRjectionReasonByCodeAndLangCodeRecordsEmptyExceptionTest() throws Exception {
		Mockito.when(reasonCategoryRepository.findReasonCategoryByCodeAndLangCode(ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(new ArrayList<ReasonCategory>());
		mockMvc.perform(get("/v1.0/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isOk());
	}

	@Test
	public void getAllRjectionReasonRecordsEmptyExceptionTest() throws Exception {
		Mockito.when(reasonCategoryRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull())
				.thenReturn(new ArrayList<ReasonCategory>());
		mockMvc.perform(get("/v1.0/packetrejectionreasons")).andExpect(status().isOk());
	}

	@Test
	public void createReasonCateogryTest() throws Exception {
		Mockito.when(reasonCategoryRepository.create(Mockito.any())).thenReturn(reasoncategories.get(0));
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON)
				.content(reasonCategoryRequest.getBytes())).andExpect(status().isOk());
	}

	@Test
	public void createReasonCateogryLanguageCodeValidatorFailureTest() throws Exception {
		RequestDto<PostReasonCategoryDto> requestDto1 = new RequestDto<>();
		requestDto1.setId("mosip.create.packetrejection.reason");
		requestDto1.setVer("1.0.0");
		postReasonCategoryDto.setLangCode("xxx");
		requestDto1.setRequest(postReasonCategoryDto);
		String content = mapper.writeValueAsString(requestDto1);
		Mockito.when(reasonCategoryRepository.create(Mockito.any())).thenReturn(reasoncategories.get(0));
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON)
				.content(content.getBytes())).andExpect(status().isOk());
	}

	@Test
	public void createReasonCateogryLanguageCodeValidatorTest() throws Exception {
		Mockito.when(reasonCategoryRepository.create(Mockito.any())).thenReturn(reasoncategories.get(0));
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON)
				.content(reasonCategoryRequest.getBytes())).andExpect(status().isOk());
	}

	@Test
	public void createReasonListTest() throws Exception {
		Mockito.when(reasonListRepository.create(Mockito.any())).thenReturn(reasonList.get(0));
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasonlist").contentType(MediaType.APPLICATION_JSON)
				.content(reasonListRequest.getBytes())).andExpect(status().isOk());
	}

	@Test
	public void createReasonListLanguageCodeValidatorTest() throws Exception {
		RequestDto<ReasonListDto> requestDto1 = new RequestDto<>();
		requestDto1.setId("mosip.create.packetrejection.reason");
		requestDto1.setVer("1.0.0");
		reasonListDto.setLangCode("xxx");
		requestDto1.setRequest(reasonListDto);
		String content = mapper.writeValueAsString(requestDto1);
		Mockito.when(reasonListRepository.create(Mockito.any())).thenReturn(reasonList.get(0));
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasonlist").contentType(MediaType.APPLICATION_JSON)
				.content(content.getBytes())).andExpect(status().isOk());
	}

	@Test
	public void createReasonCateogryFetchExceptionTest() throws Exception {
		Mockito.when(reasonCategoryRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON)
				.content(reasonCategoryRequest.getBytes())).andExpect(status().isInternalServerError());
	}

	@Test
	public void createReasonListFetchExceptionTest() throws Exception {
		Mockito.when(reasonListRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasonlist").contentType(MediaType.APPLICATION_JSON)
				.content(reasonListRequest.getBytes())).andExpect(status().isInternalServerError());
	}

	// -----------------------------RegistrationCenterTest----------------------------------

	@Test
	public void getSpecificRegistrationCenterByIdTest() throws Exception {

		when(repositoryCenterHistoryRepository.findByIdAndLangCodeAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull("1", "ENG",
				localDateTimeUTCFormat)).thenReturn(centers);

		MvcResult result = mockMvc
				.perform(get("/v1.0/registrationcentershistory/1/ENG/".concat(UTC_DATE_TIME_FORMAT_DATE_STRING))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		RegistrationCenterHistoryResponseDto returnResponse = mapper
				.readValue(result.getResponse().getContentAsString(), RegistrationCenterHistoryResponseDto.class);

		assertThat(returnResponse.getRegistrationCentersHistory().get(0).getId(), is("1"));
	}

	@Test
	public void getRegistrationCentersHistoryNotFoundExceptionTest() throws Exception {
		when(repositoryCenterHistoryRepository.findByIdAndLangCodeAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull("1", "ENG",
				localDateTimeUTCFormat)).thenReturn(null);
		mockMvc.perform(get("/v1.0/registrationcentershistory/1/ENG/".concat(UTC_DATE_TIME_FORMAT_DATE_STRING))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@Test
	public void getRegistrationCentersHistoryEmptyExceptionTest() throws Exception {
		when(repositoryCenterHistoryRepository.findByIdAndLangCodeAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull("1", "ENG",
				localDateTimeUTCFormat)).thenReturn(new ArrayList<RegistrationCenterHistory>());
		mockMvc.perform(get("/v1.0/registrationcentershistory/1/ENG/".concat(UTC_DATE_TIME_FORMAT_DATE_STRING))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@Test
	public void getRegistrationCentersHistoryFetchExceptionTest() throws Exception {
		when(repositoryCenterHistoryRepository.findByIdAndLangCodeAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull("1", "ENG",
				localDateTimeUTCFormat)).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/v1.0/registrationcentershistory/1/ENG/".concat(UTC_DATE_TIME_FORMAT_DATE_STRING))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError()).andReturn();
	}

	@Test
	public void getRegistrationCenterByHierarchylevelAndTextAndLanguageCodeTest() throws Exception {
		centers.add(center);
		when(registrationCenterRepository.findRegistrationCenterByListOfLocationCode(Mockito.anySet(),
				Mockito.anyString())).thenReturn(registrationCenters);
		when(locationRepository.getAllLocationsByLangCodeAndLevel(Mockito.anyString(), Mockito.anyShort()))
				.thenReturn(locationHierarchies);
		MvcResult result = mockMvc
				.perform(get("/v1.0/registrationcenters/ENG/1/PATANA").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(2).getName(), is("Bangalore Central"));
	}

	@Test
	public void getSpecificRegistrationCenterHierarchyLevelFetchExceptionTest() throws Exception {
		Set<String> codes = new HashSet<String>();
		codes.add("TEST");
		when(registrationCenterRepository.findRegistrationCenterByListOfLocationCode(Mockito.anySet(),
				Mockito.anyString())).thenThrow(DataAccessLayerException.class);
		when(locationRepository.getAllLocationsByLangCodeAndLevel(Mockito.anyString(), Mockito.anyShort()))
				.thenReturn(locationHierarchies);
		mockMvc.perform(get("/v1.0/registrationcenters/ENG/1/PATANA").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getRegistrationCenterHierarchyLevelNotFoundExceptionTest() throws Exception {

		List<RegistrationCenter> emptyList = new ArrayList<>();
		when(locationRepository.getAllLocationsByLangCodeAndLevel(Mockito.anyString(), Mockito.anyShort()))
				.thenReturn(locationHierarchies);
		when(registrationCenterRepository.findRegistrationCenterByListOfLocationCode(Mockito.anySet(),
				Mockito.anyString())).thenReturn(emptyList);

		mockMvc.perform(get("/v1.0/registrationcenters/ENG/1/BANGALORE").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void getRegistrationCenterHierarchyLevelNotFoundExceptionTest2() throws Exception {

		List<RegistrationCenter> emptyList = new ArrayList<>();
		when(locationRepository.getAllLocationsByLangCodeAndLevel(Mockito.anyString(), Mockito.anyShort()))
				.thenReturn(locationHierarchies);
		when(registrationCenterRepository.findRegistrationCenterByListOfLocationCode(Mockito.anySet(),
				Mockito.anyString())).thenReturn(emptyList);

		mockMvc.perform(get("/v1.0/registrationcenters/ENG/1/PATANA").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void getRegistrationCenterByHierarchylevelAndListTextAndLanguageCodeTest() throws Exception {
		when(locationRepository.getAllLocationsByLangCodeAndLevel(Mockito.anyString(), Mockito.anyShort()))
				.thenReturn(locationHierarchies);
		when(registrationCenterRepository.findRegistrationCenterByListOfLocationCode(Mockito.anySet(),
				Mockito.anyString())).thenReturn(registrationCenters);
		mockMvc.perform(get("/v1.0/registrationcenters/ENG/2/names").param("name", "PATANA")
				.param("name", "Bangalore Central").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andReturn();

	}

	@Test
	public void getRegistrationCenterByHierarchylevelAndListTextAndLanguageCodeFetchExceptionTest() throws Exception {
		when(locationRepository.getAllLocationsByLangCodeAndLevel(Mockito.anyString(), Mockito.anyShort()))
				.thenReturn(locationHierarchies);
		when(registrationCenterRepository.findRegistrationCenterByListOfLocationCode(Mockito.anySet(),
				Mockito.anyString())).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/registrationcenters/ENG/5/names").param("name", "PATANA")
				.param("name", "Bangalore Central").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getRegistrationCenterByHierarchylevelAndListTextAndLanguageCodeNotFoundExceptionTest()
			throws Exception {

		List<RegistrationCenter> emptyList = new ArrayList<>();
		when(locationRepository.getAllLocationsByLangCodeAndLevel(Mockito.anyString(), Mockito.anyShort()))
				.thenReturn(locationHierarchies);
		when(registrationCenterRepository.findRegistrationCenterByListOfLocationCode(Mockito.anySet(),
				Mockito.anyString())).thenReturn(emptyList);
		mockMvc.perform(get("/v1.0/registrationcenters/ENG/5/names").param("name", "bangalore")
				.param("name", "Bangalore Central").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	public void getRegistrationCenterByHierarchylevelAndListTextAndLanguageCodeNotFoundExceptionTest2()
			throws Exception {

		List<RegistrationCenter> emptyList = new ArrayList<>();
		when(locationRepository.getAllLocationsByLangCodeAndLevel(Mockito.anyString(), Mockito.anyShort()))
				.thenReturn(locationHierarchies);
		when(registrationCenterRepository.findRegistrationCenterByListOfLocationCode(Mockito.anySet(),
				Mockito.anyString())).thenReturn(emptyList);
		mockMvc.perform(get("/v1.0/registrationcenters/ENG/5/names").param("name", "PATANA")
				.param("name", "Bangalore Central").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	// -----------------------------RegistrationCenterIntegrationTest----------------------------------

	@Test
	public void getSpecificRegistrationCenterByIdAndLangCodeNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findByIdAndLangCode("1", "ENG")).thenReturn(null);

		mockMvc.perform(get("/v1.0/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void getSpecificRegistrationCenterByIdAndLangCodeFetchExceptionTest() throws Exception {

		when(registrationCenterRepository.findByIdAndLangCode("1", "ENG")).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getCoordinateSpecificRegistrationCentersRegistrationCenterNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG"))
				.thenReturn(new ArrayList<>());
		mockMvc.perform(get("/v1.0/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersRegistrationCenterFetchExceptionTest() throws Exception {
		when(registrationCenterRepository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG"))
				.thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/v1.0/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError()).andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersNumberFormatExceptionTest() throws Exception {
		mockMvc.perform(get("/v1.0/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/ae")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError()).andReturn();
	}

	@Test
	public void getSpecificRegistrationCenterByLocationCodeAndLangCodeNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findByLocationCodeAndLangCode("ENG", "BLR")).thenReturn(null);

		mockMvc.perform(get("/v1.0/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void getSpecificRegistrationCenterByLocationCodeAndLangCodeFetchExceptionTest() throws Exception {

		when(registrationCenterRepository.findByLocationCodeAndLangCode("BLR", "ENG"))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getAllRegistrationCentersNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findAll(RegistrationCenter.class))
				.thenReturn(new ArrayList<RegistrationCenter>());

		mockMvc.perform(get("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void getAllRegistrationCentersFetchExceptionTest() throws Exception {
		when(registrationCenterRepository.findAllByIsDeletedFalseOrIsDeletedIsNull())
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getSpecificRegistrationCenterByIdTestSuccessTest() throws Exception {
		when(registrationCenterRepository.findByIdAndLangCode("1", "ENG")).thenReturn(banglore);

		MvcResult result = mockMvc
				.perform(get("/v1.0/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);

		assertThat(returnResponse.getRegistrationCenters().get(0).getId(), is("1"));
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersTest() throws Exception {
		when(registrationCenterRepository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG"))
				.thenReturn(registrationCenters);
		MvcResult result = mockMvc
				.perform(get("/v1.0/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getLatitude(), is("12.9180722"));
		assertThat(returnResponse.getRegistrationCenters().get(1).getLongitude(), is("77.5028792"));
	}

	@Test
	public void getLocationSpecificRegistrationCentersTest() throws Exception {
		when(registrationCenterRepository.findByLocationCodeAndLangCode("BLR", "ENG")).thenReturn(registrationCenters);
		MvcResult result = mockMvc
				.perform(get("/v1.0/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(1).getLongitude(), is("77.5028792"));
	}

	@Test
	public void getLocationSpecificMultipleRegistrationCentersTest() throws Exception {
		when(registrationCenterRepository.findByLocationCodeAndLangCode("BLR", "ENG")).thenReturn(registrationCenters);
		MvcResult result = mockMvc
				.perform(get("/v1.0/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(2).getName(), is("Bangalore Central"));
	}

	@Test
	public void getAllRegistrationCenterTest() throws Exception {
		when(registrationCenterRepository.findAllByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(registrationCenters);
		MvcResult result = mockMvc.perform(get("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(2).getName(), is("Bangalore Central"));
	}

	// -----------------------------RegistrationCenterIntegrationTest----------------------------------

	@Test
	public void getRegistrationCentersMachineUserMappingNotFoundExceptionTest() throws Exception {
		when(registrationCenterUserMachineHistoryRepository
				.findByCntrIdAndUsrIdAndMachineIdAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull("1",
						"1", "1", localDateTimeUTCFormat)).thenReturn(registrationCenterUserMachineHistories);
		mockMvc.perform(get("/v1.0/getregistrationmachineusermappinghistory/".concat(UTC_DATE_TIME_FORMAT_DATE_STRING)
				.concat("/1/1/1")).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@Test
	public void getRegistrationCentersMachineUserMappingFetchExceptionTest() throws Exception {
		when(registrationCenterUserMachineHistoryRepository
				.findByCntrIdAndUsrIdAndMachineIdAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull("1",
						"1", "1", localDateTimeUTCFormat)).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/v1.0/getregistrationmachineusermappinghistory/".concat(UTC_DATE_TIME_FORMAT_DATE_STRING)
				.concat("/1/1/1")).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError())
				.andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersDateTimeParseExceptionTest() throws Exception {
		mockMvc.perform(get("/v1.0/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45+5:30/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	// @Test
	public void getRegistrationCentersMachineUserMappingTest() throws Exception {
		registrationCenterUserMachineHistories.add(registrationCenterUserMachineHistory);
		when(registrationCenterUserMachineHistoryRepository
				.findByCntrIdAndUsrIdAndMachineIdAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull("1",
						"1", "1", MapperUtils.parseToLocalDateTime("2018-10-30T19:20:30.45")))
								.thenReturn(registrationCenterUserMachineHistories);
		MvcResult result = mockMvc
				.perform(get("/v1.0/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		RegistrationCenterUserMachineMappingHistoryResponseDto returnResponse = mapper.readValue(
				result.getResponse().getContentAsString(),
				RegistrationCenterUserMachineMappingHistoryResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(0).getCntrId(), is("1"));
		assertThat(returnResponse.getRegistrationCenters().get(0).getUsrId(), is("1"));
		assertThat(returnResponse.getRegistrationCenters().get(0).getMachineId(), is("1"));
	}

	@Test
	public void deleteRegistrationCenterUserMachineMappingTest() throws Exception {
		when(registrationCenterMachineUserRepository.findAllNondeletedMappings(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.of(registrationCenterUserMachine));
		when(registrationCenterUserMachineHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterUserMachineHistory);
		when(registrationCenterMachineUserRepository.update(Mockito.any())).thenReturn(registrationCenterUserMachine);
		mockMvc.perform(delete("/v1.0/registrationmachineusermappings/REG001/MAC001/QC001")).andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterUserMachineMappingDataNotFoundExceptionTest() throws Exception {
		when(registrationCenterMachineUserRepository.findAllNondeletedMappings(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.empty());
		mockMvc.perform(delete("/v1.0/registrationmachineusermappings/REG001/MAC001/QC001")).andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterUserMachineMappingDataAccessLayerExceptionTest() throws Exception {
		when(registrationCenterMachineUserRepository.findAllNondeletedMappings(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		when(registrationCenterMachineUserRepository.create(Mockito.any())).thenReturn(registrationCenterUserMachine);
		when(registrationCenterUserMachineHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterUserMachineHistory);
		mockMvc.perform(delete("/v1.0/registrationmachineusermappings/REG001/MAC001/QC001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void createRegistrationCentersMachineUserMappingTest() throws Exception {
		RequestDto<RegistrationCenterUserMachineMappingDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterUserMachineMappingDto centerUserMachineMappingDto = new RegistrationCenterUserMachineMappingDto();
		centerUserMachineMappingDto.setCntrId("REG001");
		centerUserMachineMappingDto.setUsrId("QC001");
		centerUserMachineMappingDto.setIsActive(true);
		centerUserMachineMappingDto.setMachineId("MAC001");
		requestDto.setRequest(centerUserMachineMappingDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterMachineUserRepository.create(Mockito.any())).thenReturn(registrationCenterUserMachine);
		when(registrationCenterUserMachineHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterUserMachineHistory);
		mockMvc.perform(post("/v1.0/registrationmachineusermappings").contentType(MediaType.APPLICATION_JSON)
				.content(contentJson)).andExpect(status().isOk());
	}

	@Test
	public void createRegistrationCentersMachineUserMappingDataAccessLayerExceptionTest() throws Exception {
		RequestDto<RegistrationCenterUserMachineMappingDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterUserMachineMappingDto centerUserMachineMappingDto = new RegistrationCenterUserMachineMappingDto();
		centerUserMachineMappingDto.setCntrId("REG001");
		centerUserMachineMappingDto.setUsrId("QC001");
		centerUserMachineMappingDto.setIsActive(true);
		centerUserMachineMappingDto.setMachineId("MAC001");
		centerUserMachineMappingDto.setLangCode("eng");
		requestDto.setRequest(centerUserMachineMappingDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterMachineUserRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		mockMvc.perform(post("/v1.0/registrationmachineusermappings").contentType(MediaType.APPLICATION_JSON)
				.content(contentJson)).andExpect(status().isInternalServerError());
	}

	// TODO:
	@Test
	public void createOrUpdateRegistrationCentersMachineUserMappingCreateTest() throws Exception {
		RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto> requestDto = new RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		List<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDtos = new ArrayList<>();
		RegistrationCenterUserMachineMappingDto centerUserMachineMappingDto1 = new RegistrationCenterUserMachineMappingDto();
		centerUserMachineMappingDto1.setCntrId("REG001");
		centerUserMachineMappingDto1.setUsrId("QC001");
		centerUserMachineMappingDto1.setIsActive(true);
		centerUserMachineMappingDto1.setLangCode("eng");
		centerUserMachineMappingDto1.setMachineId("MAC001");
		registrationCenterUserMachineMappingDtos.add(centerUserMachineMappingDto1);
		RegistrationCenterUserMachineMappingDto centerUserMachineMappingDto2 = new RegistrationCenterUserMachineMappingDto();
		centerUserMachineMappingDto2.setCntrId("REG001");
		centerUserMachineMappingDto2.setUsrId("QC001");
		centerUserMachineMappingDto2.setLangCode("eng");
		centerUserMachineMappingDto2.setIsActive(true);
		centerUserMachineMappingDto2.setMachineId("MAC001");
		registrationCenterUserMachineMappingDtos.add(centerUserMachineMappingDto2);
		requestDto.setRequest(registrationCenterUserMachineMappingDtos);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterMachineUserRepository.findAllNondeletedMappings(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.empty());
		when(registrationCenterMachineUserRepository.create(Mockito.any())).thenReturn(registrationCenterUserMachine);
		when(registrationCenterUserMachineHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterUserMachineHistory);
		mockMvc.perform(put("/v1.0/registrationmachineusermappings").contentType(MediaType.APPLICATION_JSON)
				.content(contentJson)).andExpect(status().isOk());
	}

	// TODO:
	@Test
	public void createOrUpdateRegistrationCentersMachineUserMappingUpdateTest() throws Exception {
		RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto> requestDto = new RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		List<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDtos = new ArrayList<>();
		RegistrationCenterUserMachineMappingDto centerUserMachineMappingDto1 = new RegistrationCenterUserMachineMappingDto();
		centerUserMachineMappingDto1.setCntrId("REG001");
		centerUserMachineMappingDto1.setUsrId("QC001");
		centerUserMachineMappingDto1.setIsActive(true);
		centerUserMachineMappingDto1.setMachineId("MAC001");
		centerUserMachineMappingDto1.setLangCode("eng");
		registrationCenterUserMachineMappingDtos.add(centerUserMachineMappingDto1);
		RegistrationCenterUserMachineMappingDto centerUserMachineMappingDto2 = new RegistrationCenterUserMachineMappingDto();
		centerUserMachineMappingDto2.setCntrId("REG001");
		centerUserMachineMappingDto2.setUsrId("QC001");
		centerUserMachineMappingDto2.setIsActive(true);
		centerUserMachineMappingDto2.setMachineId("MAC001");
		centerUserMachineMappingDto2.setLangCode("eng");
		registrationCenterUserMachineMappingDtos.add(centerUserMachineMappingDto2);
		requestDto.setRequest(registrationCenterUserMachineMappingDtos);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterMachineUserRepository.findAllNondeletedMappings(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.of(registrationCenterUserMachine));
		when(registrationCenterMachineUserRepository.update(Mockito.any())).thenReturn(registrationCenterUserMachine);
		when(registrationCenterUserMachineHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterUserMachineHistory);
		mockMvc.perform(put("/v1.0/registrationmachineusermappings").contentType(MediaType.APPLICATION_JSON)
				.content(contentJson)).andExpect(status().isOk());
	}

	@Test
	public void createOrUpdateRegistrationCentersMachineUserMappingNotMappedTest() throws Exception {
		RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto> requestDto = new RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		List<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDtos = new ArrayList<>();
		RegistrationCenterUserMachineMappingDto centerUserMachineMappingDto1 = new RegistrationCenterUserMachineMappingDto();
		centerUserMachineMappingDto1.setCntrId("REG001");
		centerUserMachineMappingDto1.setUsrId("QC001");
		centerUserMachineMappingDto1.setIsActive(true);
		centerUserMachineMappingDto1.setLangCode("eng");
		centerUserMachineMappingDto1.setMachineId("MAC001");
		registrationCenterUserMachineMappingDtos.add(centerUserMachineMappingDto1);
		RegistrationCenterUserMachineMappingDto centerUserMachineMappingDto2 = new RegistrationCenterUserMachineMappingDto();
		centerUserMachineMappingDto2.setCntrId("REG001");
		centerUserMachineMappingDto2.setUsrId("QC001");
		centerUserMachineMappingDto2.setIsActive(true);
		centerUserMachineMappingDto2.setLangCode("eng");
		centerUserMachineMappingDto2.setMachineId("MAC001");
		registrationCenterUserMachineMappingDtos.add(centerUserMachineMappingDto2);
		requestDto.setRequest(registrationCenterUserMachineMappingDtos);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterMachineUserRepository.findAllNondeletedMappings(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.empty());
		when(registrationCenterMachineUserRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute ", null));
		when(registrationCenterUserMachineHistoryRepository.create(Mockito.any()))
				.thenReturn(registrationCenterUserMachineHistory);
		mockMvc.perform(put("/v1.0/registrationmachineusermappings").contentType(MediaType.APPLICATION_JSON)
				.content(contentJson)).andExpect(status().isOk());
	}

	// -----------------------------TitleIntegrationTest----------------------------------
	@Test
	public void getTitleByLanguageCodeNotFoundExceptionTest() throws Exception {

		titlesNull = new ArrayList<>();

		Mockito.when(titleRepository.getThroughLanguageCode("ENG")).thenReturn(titlesNull);

		mockMvc.perform(get("/v1.0/title/ENG").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	public void getAllTitleFetchExceptionTest() throws Exception {

		Mockito.when(titleRepository.findAll(Title.class)).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/title").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getAllTitleNotFoundExceptionTest() throws Exception {

		titlesNull = new ArrayList<>();

		Mockito.when(titleRepository.findAll(Title.class)).thenReturn(titlesNull);

		mockMvc.perform(get("/v1.0/title").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	public void getAllTitlesTest() throws Exception {
		Mockito.when(titleRepository.findAll(Title.class)).thenReturn(titleList);
		mockMvc.perform(get("/v1.0/title")).andExpect(status().isOk());

	}

	@Test
	public void getTitleByLanguageCodeTest() throws Exception {

		Mockito.when(titleRepository.getThroughLanguageCode(Mockito.anyString())).thenReturn(titleList);
		mockMvc.perform(get("/v1.0/title/{langcode}", "ENG")).andExpect(status().isOk());

	}

	@Test
	public void saveTitleTest() throws Exception {
		String content = "{ \"id\": \"string\", \"request\": { \"code\": \"43\", \"isActive\": true, \"langCode\": \"eng\", \"titleDescription\": \"string\", \"titleName\": \"string\" }, \"timestamp\": \"2018-12-17T09:10:25.829Z\", \"ver\": \"string\"}";
		when(titleRepository.create(Mockito.any())).thenReturn(title);
		mockMvc.perform(post("/v1.0/title").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void createTitleLangCodeValidationTest() throws Exception {
		String content = "{ \"id\": \"string\", \"request\": { \"code\": \"43\", \"isActive\": true, \"langCode\": \"dfg\", \"titleDescription\": \"string\", \"titleName\": \"string\" }, \"timestamp\": \"2018-12-17T09:10:25.829Z\", \"ver\": \"string\"}";
		mockMvc.perform(post("/v1.0/title").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void saveTitleExceptionTest() throws Exception {

		String content = "{ \"id\": \"string\", \"request\": { \"code\": \"43\", \"isActive\": true, \"langCode\": \"eng\", \"titleDescription\": \"string\", \"titleName\": \"string\" }, \"timestamp\": \"2018-12-17T09:10:25.829Z\", \"ver\": \"string\"}";
		when(titleRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute ", null));
		mockMvc.perform(post("/v1.0/title").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void updateTitleTest() throws Exception {
		RequestDto<TitleDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.title.update");
		requestDto.setVer("1.0");
		TitleDto titleDto = new TitleDto();
		titleDto.setCode("001");
		titleDto.setTitleDescription("mosip");
		titleDto.setIsActive(true);
		titleDto.setLangCode("eng");
		titleDto.setTitleName("mosip");
		requestDto.setRequest(titleDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(titleRepository.findById(Mockito.any(), Mockito.any())).thenReturn(title);
		mockMvc.perform(put("/v1.0/title").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateTitleLangCodeValidationTest() throws Exception {
		String content = "{ \"id\": \"string\", \"request\": { \"code\": \"43\", \"isActive\": true, \"langCode\": \"dfg\", \"titleDescription\": \"string\", \"titleName\": \"string\" }, \"timestamp\": \"2018-12-17T09:10:25.829Z\", \"ver\": \"string\"}";
		mockMvc.perform(put("/v1.0/title").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void updateTitleBadRequestTest() throws Exception {
		RequestDto<TitleDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.title.update");
		requestDto.setVer("1.0");
		TitleDto titleDto = new TitleDto();
		titleDto.setCode("001");
		titleDto.setTitleDescription("mosip");
		titleDto.setIsActive(true);
		titleDto.setLangCode("ENG");
		titleDto.setTitleName("mosip");
		requestDto.setRequest(titleDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(titleRepository.findById(Mockito.any(), Mockito.any())).thenReturn(null);
		mockMvc.perform(put("/v1.0/title").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateTitleDatabaseConnectionExceptionTest() throws Exception {
		RequestDto<TitleDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.title.update");
		requestDto.setVer("1.0");
		TitleDto titleDto = new TitleDto();
		titleDto.setCode("001");
		titleDto.setTitleDescription("mosip");
		titleDto.setIsActive(true);
		titleDto.setLangCode("eng");
		titleDto.setTitleName("mosip");
		requestDto.setRequest(titleDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(titleRepository.findById(Mockito.any(), Mockito.any())).thenReturn(title);
		when(titleRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(put("/v1.0/title").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteTitleTest() throws Exception {
		when(titleRepository.findByCode(Mockito.any())).thenReturn(titleList);
		when(titleRepository.update(Mockito.any())).thenReturn(title);
		mockMvc.perform(delete("/v1.0/title/ABC").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void deleteTitleBadRequestTest() throws Exception {
		when(titleRepository.getThroughLanguageCode(Mockito.any())).thenReturn(null);
		mockMvc.perform(delete("/v1.0/title/ABC").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	public void deleteTitleDatabaseConnectionExceptionTest() throws Exception {
		when(titleRepository.findByCode(Mockito.any())).thenReturn(titleList);
		when(titleRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/title/ABC").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}
	// -----------------------------------gender-type----------------------------------------

	@Test
	public void addGenderTypeTest() throws Exception {
		RequestDto<GenderTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(genderDto);
		String content = mapper.writeValueAsString(requestDto);
		when(genderTypeRepository.create(Mockito.any())).thenReturn(genderType);
		mockMvc.perform(post("/v1.0/gendertypes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void addGenderTypeLandCodeValidationTest() throws Exception {
		RequestDto<GenderTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		genderDto.setLangCode("akk");
		requestDto.setRequest(genderDto);
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/gendertypes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void addGenderTypeExceptionTest() throws Exception {

		RequestDto<GenderTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(genderDto);
		String content = mapper.writeValueAsString(requestDto);
		when(genderTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute ", null));
		mockMvc.perform(post("/v1.0/gendertypes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void updateGenderTypeTest() throws Exception {
		RequestDto<GenderTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		GenderTypeDto genderTypeDto = new GenderTypeDto("GEN01", "Male", "eng", true);
		requestDto.setRequest(genderTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(genderTypeRepository.updateGenderType(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(1);
		mockMvc.perform(put("/v1.0/gendertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateGenderTypeNotFoundExceptionTest() throws Exception {
		RequestDto<GenderTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		GenderTypeDto genderTypeDto = new GenderTypeDto("GEN01", "Male", "ENG", true);
		requestDto.setRequest(genderTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(genderTypeRepository.updateGenderType(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(0);
		mockMvc.perform(put("/v1.0/gendertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateGenderTypeDatabaseConnectionExceptionTest() throws Exception {
		RequestDto<GenderTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		GenderTypeDto genderTypeDto = new GenderTypeDto("GEN01", "Male", "eng", true);
		requestDto.setRequest(genderTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(genderTypeRepository.updateGenderType(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any()))
						.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(put("/v1.0/gendertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteGenderTypeTest() throws Exception {
		when(genderTypeRepository.deleteGenderType(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		mockMvc.perform(delete("/v1.0/gendertypes/GEN01").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteGenderTypeNotFoundExceptionTest() throws Exception {
		when(genderTypeRepository.deleteGenderType(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0);
		mockMvc.perform(delete("/v1.0/gendertypes/GEN01").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteGenderTypeDatabaseConnectionExceptionTest() throws Exception {

		when(genderTypeRepository.deleteGenderType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/gendertypes/GEN01").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	// ----------------------------------BiometricAttributeCreateApiTest--------------------------------------------------
	@Test
	public void createBiometricAttributeTest() throws Exception {

		RequestDto<BiometricAttributeDto> requestDto = new RequestDto<BiometricAttributeDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(biometricAttributeDto);
		String content = mapper.writeValueAsString(requestDto);
		Mockito.when(biometricAttributeRepository.create(Mockito.any())).thenReturn(biometricAttribute);
		mockMvc.perform(post("/v1.0/biometricattributes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void createBiometricAttributeExceptionTest() throws Exception {
		RequestDto<BiometricAttributeDto> requestDto = new RequestDto<BiometricAttributeDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(biometricAttributeDto);
		String content = mapper.writeValueAsString(requestDto);
		when(biometricAttributeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(post("/v1.0/biometricattributes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void createBiometricAttributeLangCodeValidationTest() throws Exception {
		RequestDto<BiometricAttributeDto> requestDto = new RequestDto<BiometricAttributeDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		biometricAttributeDto.setLangCode("akk");
		requestDto.setRequest(biometricAttributeDto);
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/biometricattributes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	// ----------------------------------TemplateCreateApiTest--------------------------------------------------
	@Test
	public void createTemplateTest() throws Exception {
		RequestDto<TemplateDto> requestDto = new RequestDto<TemplateDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(templateDto);
		String content = mapper.writeValueAsString(requestDto);
		Mockito.when(templateRepository.create(Mockito.any())).thenReturn(template);
		mockMvc.perform(post("/v1.0/templates").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void createTemplateLangCodeValidationTest() throws Exception {
		RequestDto<TemplateDto> requestDto = new RequestDto<TemplateDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		templateDto.setLangCode("akk");
		requestDto.setRequest(templateDto);
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/templates").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void createTemplateExceptionTest() throws Exception {
		RequestDto<TemplateDto> requestDto = new RequestDto<TemplateDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(templateDto);
		String content = mapper.writeValueAsString(requestDto);
		when(templateRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(post("/v1.0/templates").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());
	}

	// ----------------------------------TemplateTypeCreateApiTest--------------------------------------------------
	@Test
	public void createTemplateTypeTest() throws Exception {
		RequestDto<TemplateTypeDto> requestDto = new RequestDto<TemplateTypeDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(templateTypeDto);
		String content = mapper.writeValueAsString(requestDto);
		Mockito.when(templateTypeRepository.create(Mockito.any())).thenReturn(templateType);
		mockMvc.perform(post("/v1.0/templatetypes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void createTemplatetypeExceptionTest() throws Exception {
		RequestDto<TemplateTypeDto> requestDto = new RequestDto<TemplateTypeDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(templateTypeDto);
		String content = mapper.writeValueAsString(requestDto);
		when(templateTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(post("/v1.0/templatetypes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void createTemplatetypeLangValidationExceptionTest() throws Exception {
		RequestDto<TemplateTypeDto> requestDto = new RequestDto<TemplateTypeDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		templateTypeDto.setLangCode("akk");
		requestDto.setRequest(templateTypeDto);
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/templatetypes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	// -----------------------------------DeviceSpecificationTest---------------------------------
	@Test
	public void findDeviceSpecLangcodeSuccessTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(deviceSpecList);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void findDeviceSpecLangcodeNullResponseTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(null);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void findDeviceSpecLangcodeFetchExceptionTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}", "ENG"))
				.andExpect(status().isInternalServerError());
	}

	// --------------------------------------------
	@Test
	public void findDeviceSpecByLangCodeAndDevTypeCodeSuccessTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(
				Mockito.anyString(), Mockito.anyString())).thenReturn(deviceSpecList);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}/{devicetypecode}", "ENG", "laptop"))
				.andExpect(status().isOk());
	}

	@Test
	public void findDeviceSpecByLangCodeAndDevTypeCodeNullResponseTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(
				Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}/{devicetypecode}", "ENG", "laptop"))
				.andExpect(status().isOk());
	}

	@Test
	public void findDeviceSpecByLangCodeAndDevTypeCodeFetchExceptionTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(
				Mockito.anyString(), Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}/{devicetypecode}", "ENG", "laptop"))
				.andExpect(status().isInternalServerError());
	}
	// ----------------------------------------------------------------

	@Test
	public void createDeviceSpecificationTest() throws Exception {
		RequestDto<DeviceSpecificationDto> requestDto;
		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.DeviceSpecificationcode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceSpecificationDto);

		String deviceSpecificationJson = mapper.writeValueAsString(requestDto);

		when(deviceSpecificationRepository.create(Mockito.any())).thenReturn(deviceSpecification);
		mockMvc.perform(post("/v1.0/devicespecifications").contentType(MediaType.APPLICATION_JSON)
				.content(deviceSpecificationJson)).andExpect(status().isOk());
	}

	@Test
	public void createDeviceSpecificationLangCodeValidationTest() throws Exception {
		RequestDto<DeviceSpecificationDto> requestDto;
		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.DeviceSpecificationcode");
		requestDto.setVer("1.0.0");
		deviceSpecificationDto.setLangCode("akk");
		requestDto.setRequest(deviceSpecificationDto);
		String deviceSpecificationJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/devicespecifications").contentType(MediaType.APPLICATION_JSON)
				.content(deviceSpecificationJson)).andExpect(status().isOk());
	}

	@Test
	public void createDeviceSpecificationExceptionTest() throws Exception {
		RequestDto<DeviceSpecificationDto> requestDto;
		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.DeviceSpecificationcode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceSpecificationDto);

		String DeviceSpecificationJson = mapper.writeValueAsString(requestDto);

		Mockito.when(deviceSpecificationRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(DeviceSpecificationJson))
				.andExpect(status().isInternalServerError());
	}

	// ---------------------------------DeviceTypeTest------------------------------------------------

	@Test
	public void createDeviceTypeTest() throws Exception {

		RequestDto<DeviceTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.Devicetypecode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceTypeDto);

		String DeviceTypeJson = mapper.writeValueAsString(requestDto);

		when(deviceTypeRepository.create(Mockito.any())).thenReturn(deviceType);
		mockMvc.perform(post("/v1.0/devicetypes").contentType(MediaType.APPLICATION_JSON).content(DeviceTypeJson))
				.andExpect(status().isOk());
	}

	@Test
	public void createDeviceTypeExceptionTest() throws Exception {

		RequestDto<DeviceTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.Devicetypecode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceTypeDto);

		String DeviceTypeJson = mapper.writeValueAsString(requestDto);

		Mockito.when(deviceTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicetypes").contentType(MediaType.APPLICATION_JSON)
				.content(DeviceTypeJson)).andExpect(status().isInternalServerError());
	}

	// -------------------------------MachineSpecificationTest-------------------------------
	@Test
	public void createMachineSpecificationTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto;
		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineSpecificationcode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineSpecificationDto);

		String machineSpecificationJson = mapper.writeValueAsString(requestDto);

		when(machineSpecificationRepository.create(Mockito.any())).thenReturn(machineSpecification);
		mockMvc.perform(post("/v1.0/machinespecifications").contentType(MediaType.APPLICATION_JSON)
				.content(machineSpecificationJson)).andExpect(status().isOk());

	}

	@Test
	public void createMachineSpecificationLanguageCodeValidatorTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto;
		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineSpecificationcode");
		requestDto.setVer("1.0.0");
		machineSpecificationDto.setLangCode("xxx");
		requestDto.setRequest(machineSpecificationDto);

		String machineSpecificationJson = mapper.writeValueAsString(requestDto);

		when(machineSpecificationRepository.create(Mockito.any())).thenReturn(machineSpecification);
		mockMvc.perform(post("/v1.0/machinespecifications").contentType(MediaType.APPLICATION_JSON)
				.content(machineSpecificationJson)).andExpect(status().isOk());

	}

	@Test
	public void createMachineSpecificationExceptionTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto;
		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineSpecificationcode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineSpecificationDto);

		String machineSpecificationJson = mapper.writeValueAsString(requestDto);

		Mockito.when(machineSpecificationRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(machineSpecificationJson))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void createMachineSpecificationLangCodeValidationTest() throws Exception {
		RequestDto<MachineSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineSpecificationcode");
		requestDto.setVer("1.0.0");
		machineSpecificationDto.setLangCode("akk");
		requestDto.setRequest(machineSpecificationDto);
		String machineSpecificationJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/machinespecifications").contentType(MediaType.APPLICATION_JSON)
				.content(machineSpecificationJson)).andExpect(status().isOk());
	}

	// -------------------------------------------------------------------------
	@Test
	public void updateMachineSpecificationTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machineSpecification.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineSpecificationDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineSpecificationRepository.findByIdAndLangCodeIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(machineSpecification);
		Mockito.when(machineSpecificationRepository.update(Mockito.any())).thenReturn(machineSpecification);

		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk());

	}

	@Test
	public void updateMachineSpecificationLanguageCodeValidatorTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machineSpecification.update");
		requestDto.setVer("1.0.0");
		machineSpecificationDto.setLangCode("xxx");
		requestDto.setRequest(machineSpecificationDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineSpecificationRepository.findByIdAndLangCodeIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(machineSpecification);
		Mockito.when(machineSpecificationRepository.update(Mockito.any())).thenReturn(machineSpecification);

		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk());

	}

	@Test
	public void updateMachineSpecificationNotFoundExceptionTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machineSpecification.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineSpecificationDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineSpecificationRepository.findByIdAndLangCodeIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk());

	}

	@Test
	public void updateMachineSpecificationDatabaseConnectionExceptionTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machineSpecification.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineSpecificationDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineSpecificationRepository.findByIdAndLangCodeIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isInternalServerError());

	}
	// -----------------------------------------------------------------------------------------------

	@Test
	public void deleteMachineSpecificationTest() throws Exception {
		List<Machine> emptyList = new ArrayList<>();
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineSpecList);
		when(machineRepository.findMachineBymachineSpecIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(emptyList);
		when(machineSpecificationRepository.update(Mockito.any())).thenReturn(machineSpecification);
		mockMvc.perform(delete("/v1.0/machinespecifications/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteMachineSpecificationDataNotFoundExceptionTest() throws Exception {
		List<MachineSpecification> emptyList = new ArrayList<>();
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(emptyList);
		mockMvc.perform(delete("/v1.0/machinespecifications/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteMachineSpecificationDatabaseConnectionExceptionTest() throws Exception {
		List<Machine> emptyList = new ArrayList<>();
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineSpecList);
		when(machineRepository.findMachineBymachineSpecIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(emptyList);
		when(machineSpecificationRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/machinespecifications/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void deleteMachineSpecificationDependencyExceptionTest() throws Exception {
		List<Machine> machineList = new ArrayList<Machine>();
		Machine machine = new Machine();
		machineList.add(machine);
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineSpecList);
		when(machineRepository
				.findMachineBymachineSpecIdAndIsDeletedFalseorIsDeletedIsNull(machineSpecification.getId()))
						.thenReturn(machineList);
		mockMvc.perform(delete("/v1.0/machinespecifications/MS001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	// -------------------------MachineTest-----------------------------------------

	@Test
	public void getMachineAllSuccessTest() throws Exception {
		when(machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(machineList);
		mockMvc.perform(get("/v1.0/machines")).andExpect(status().isOk());
	}

	@Test
	public void getMachineAllNullResponseTest() throws Exception {
		when(machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(null);
		mockMvc.perform(get("/v1.0/machines")).andExpect(status().isOk());
	}

	@Test
	public void getMachineAllFetchExceptionTest() throws Exception {
		when(machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull())
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/machines")).andExpect(status().isInternalServerError());
	}

	// --------------------------------------------
	@Test
	public void getMachineIdLangcodeSuccessTest() throws Exception {
		List<Machine> machines = new ArrayList<Machine>();
		machines.add(machine);
		when(machineRepository.findAllByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenReturn(machines);
		mockMvc.perform(get("/v1.0/machines/{id}/{langcode}", "1000", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getMachineIdLangcodeNullResponseTest() throws Exception {
		when(machineRepository.findAllByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		mockMvc.perform(get("/v1.0/machines/{id}/{langcode}", "1000", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getMachineIdLangcodeFetchExceptionTest() throws Exception {
		when(machineRepository.findAllByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/machines/{id}/{langcode}", "1000", "ENG"))
				.andExpect(status().isInternalServerError());
	}

	// -----------------------------------
	@Test
	public void getMachineLangcodeSuccessTest() throws Exception {
		when(machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(machineList);
		mockMvc.perform(get("/v1.0/machines/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getMachineLangcodeNullResponseTest() throws Exception {
		when(machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(null);
		mockMvc.perform(get("/v1.0/machines/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getMachineLangcodeFetchExceptionTest() throws Exception {
		when(machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/machines/{langcode}", "ENG")).andExpect(status().isInternalServerError());
	}

	@Test
	public void createMachineTest() throws Exception {
		RequestDto<MachineDto> requestDto;
		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineDto);

		machineJson = mapper.writeValueAsString(requestDto);

		when(machineRepository.create(Mockito.any())).thenReturn(machine);
		when(machineHistoryRepository.create(Mockito.any())).thenReturn(machineHistory);
		mockMvc.perform(post("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(machineJson))
				.andExpect(status().isOk());
	}

	@Test
	public void createMachineLanguageCodeValidatorTest() throws Exception {
		RequestDto<MachineDto> requestDto;
		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid");
		requestDto.setVer("1.0.0");
		machineDto.setLangCode("xxx");
		requestDto.setRequest(machineDto);

		machineJson = mapper.writeValueAsString(requestDto);

		when(machineRepository.create(Mockito.any())).thenReturn(machine);
		when(machineHistoryRepository.create(Mockito.any())).thenReturn(machineHistory);
		mockMvc.perform(post("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(machineJson))
				.andExpect(status().isOk());
	}

	@Test
	public void createMachineTestInvalid() throws Exception {
		RequestDto<MachineDto> requestDto;
		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid");
		requestDto.setVer("1.0.0");
		MachineDto mDto = new MachineDto();
		mDto.setId("1000ddfagsdgfadsfdgdsagdsagdsagdagagagdsgagadgagdf");
		mDto.setLangCode("eng");
		mDto.setName("HP");
		mDto.setIpAddress("129.0.0.0");
		mDto.setMacAddress("178.0.0.0");
		mDto.setMachineSpecId("1010");
		mDto.setSerialNum("123");
		mDto.setIsActive(true);
		requestDto.setRequest(mDto);
		machineJson = mapper.writeValueAsString(requestDto);

		mockMvc.perform(post("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(machineJson))
				.andExpect(status().isOk());
	}

	@Test
	public void createMachineExceptionTest() throws Exception {
		RequestDto<MachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.Machine.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineDto);
		String content = mapper.writeValueAsString(requestDto);

		Mockito.when(machineRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(
				MockMvcRequestBuilders.post("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void updateMachineTest() throws Exception {

		RequestDto<MachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machine.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineDto);
		String content = mapper.writeValueAsString(requestDto);

		when(machineRepository.findMachineByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.anyString())).thenReturn(machine);
		Mockito.when(machineRepository.update(Mockito.any())).thenReturn(machine);
		when(machineHistoryRepository.create(Mockito.any())).thenReturn(machineHistory);
		mockMvc.perform(
				MockMvcRequestBuilders.put("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void updateMachineLanguageCodeValidatorTest() throws Exception {

		RequestDto<MachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machine.update");
		requestDto.setVer("1.0.0");
		machineDto.setLangCode("xxx");
		requestDto.setRequest(machineDto);
		String content = mapper.writeValueAsString(requestDto);

		when(machineRepository.findMachineByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(machine);
		Mockito.when(machineRepository.update(Mockito.any())).thenReturn(machine);
		when(machineHistoryRepository.create(Mockito.any())).thenReturn(machineHistory);
		mockMvc.perform(
				MockMvcRequestBuilders.put("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void updateMachineNotFoundExceptionTest() throws Exception {

		RequestDto<MachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machine.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineRepository.findMachineByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(null);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void updateMachineDatabaseConnectionExceptionTest() throws Exception {

		RequestDto<MachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machine.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineRepository.findMachineByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());

	}

	// ---------------------------------------------------------------------------------------
	@Test
	public void deleteMachineTest() throws Exception {
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineList);
		when(registrationCenterMachineDeviceRepository.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(new ArrayList<RegistrationCenterMachineDevice>());
		when(registrationCenterMachineRepository.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(new ArrayList<RegistrationCenterMachine>());
		when(registrationCenterMachineUserRepository.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(new ArrayList<RegistrationCenterUserMachine>());
		when(machineRepository.update(Mockito.any())).thenReturn(machine);
		when(machineHistoryRepository.create(Mockito.any())).thenReturn(machineHistory);
		mockMvc.perform(delete("/v1.0/machines/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteMachineDependencyTest() throws Exception {
		List<RegistrationCenterMachineDevice> regCenterMachineDevices = new ArrayList<RegistrationCenterMachineDevice>();
		regCenterMachineDevices.add(registrationCenterMachineDevice);
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineList);
		when(registrationCenterMachineDeviceRepository.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(regCenterMachineDevices);
		when(registrationCenterMachineRepository.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(new ArrayList<RegistrationCenterMachine>());
		when(registrationCenterMachineUserRepository.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(new ArrayList<RegistrationCenterUserMachine>());
		when(machineRepository.update(Mockito.any())).thenReturn(machine);
		when(machineHistoryRepository.create(Mockito.any())).thenReturn(machineHistory);
		mockMvc.perform(delete("/v1.0/machines/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteMachineNotFoundExceptionTest() throws Exception {
		List<Machine> emptList = new ArrayList<>();
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any())).thenReturn(emptList);

		mockMvc.perform(delete("/v1.0/machines/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteMachineDatabaseConnectionExceptionTest() throws Exception {
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineList);
		when(machineRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/machines/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}
	// -----------------------------MachineTypeTest-------------------------------------------

	@Test
	public void createMachineTypeTest() throws Exception {
		RequestDto<MachineTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machinetypecode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineTypeDto);

		String machineTypeJson = mapper.writeValueAsString(requestDto);

		when(machineTypeRepository.create(Mockito.any())).thenReturn(machineType);
		mockMvc.perform(post("/v1.0/machinetypes").contentType(MediaType.APPLICATION_JSON).content(machineTypeJson))
				.andExpect(status().isOk());
	}

	@Test
	public void createMachineTypeLangCodeValidationTest() throws Exception {
		RequestDto<MachineTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machinetypecode");
		requestDto.setVer("1.0.0");
		machineTypeDto.setLangCode("akk");
		requestDto.setRequest(machineTypeDto);

		String machineTypeJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/machinetypes").contentType(MediaType.APPLICATION_JSON).content(machineTypeJson))
				.andExpect(status().isOk());
	}

	@Test
	public void createMachineTypeExceptionTest() throws Exception {
		RequestDto<MachineTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machinetypecode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineTypeDto);

		String machineTypeJson = mapper.writeValueAsString(requestDto);

		Mockito.when(machineTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machinetypes").contentType(MediaType.APPLICATION_JSON)
				.content(machineTypeJson)).andExpect(status().isInternalServerError());
	}

	// --------------------------------DeviceTest-------------------------------------------------
	@Test
	public void getDeviceLangcodeSuccessTest() throws Exception {
		when(deviceRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(deviceList);
		mockMvc.perform(get("/v1.0/devices/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getDeviceLangcodeNullResponseTest() throws Exception {
		when(deviceRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString())).thenReturn(null);
		mockMvc.perform(get("/v1.0/devices/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getDeviceLangcodeFetchExceptionTest() throws Exception {
		when(deviceRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/devices/{langcode}", "ENG")).andExpect(status().isInternalServerError());
	}

	// ----------------------------
	@Test
	public void getDeviceLangCodeAndDeviceTypeSuccessTest() throws Exception {
		when(deviceRepository.findByLangCodeAndDtypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(objectList);
		mockMvc.perform(get("/v1.0/devices/{languagecode}/{deviceType}", "ENG", "LaptopCode"))
				.andExpect(status().isOk());
	}

	@Test

	public void getDeviceLangCodeAndDeviceTypeNullResponseTest() throws Exception {
		when(deviceRepository.findByLangCodeAndDtypeCode(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(get("/v1.0/devices/{languagecode}/{deviceType}", "ENG", "LaptopCode"))
				.andExpect(status().isOk());
	}

	@Test
	public void getDeviceLangCodeAndDeviceTypeFetchExceptionTest() throws Exception {
		when(deviceRepository.findByLangCodeAndDtypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/devices/{languagecode}/{deviceType}", "ENG", "LaptopCode"))
				.andExpect(status().isInternalServerError());
	}

	// ---------------------------------------------

	@Test
	public void createDeviceTest() throws Exception {
		RequestDto<DeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.device.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceDto);
		String content = mapper.writeValueAsString(requestDto);

		Mockito.when(deviceRepository.create(Mockito.any())).thenReturn(device);
		Mockito.when(deviceHistoryRepository.create(Mockito.any())).thenReturn(deviceHistory);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/v1.0/devices").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void createDeviceExceptionTest() throws Exception {
		RequestDto<DeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.device.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceDto);
		String content = mapper.writeValueAsString(requestDto);

		Mockito.when(deviceRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(
				MockMvcRequestBuilders.post("/v1.0/devices").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void updateDeviceSuccessTest() throws Exception {
		RequestDto<DeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.device.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceDto);
		String content = mapper.writeValueAsString(requestDto);
		Mockito.when(deviceRepository.findByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenReturn(device);
		when(deviceHistoryRepository.create(Mockito.any())).thenReturn(deviceHistory);
		Mockito.when(deviceRepository.update(Mockito.any())).thenReturn(device);
		mockMvc.perform(
				MockMvcRequestBuilders.put("/v1.0/devices").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void updateDeviceDatabaseConnectionExceptionTest() throws Exception {
		when(deviceRepository.findByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenReturn(device);
		when(deviceRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(put("/v1.0/devices/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void updateDeviceNotFoundExceptionTest() throws Exception {
		RequestDto<DeviceDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.device.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceDto);
		String content = mapper.writeValueAsString(requestDto);
		Mockito.when(deviceRepository.findByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		when(deviceHistoryRepository.create(Mockito.any())).thenReturn(deviceHistory);
		Mockito.when(deviceRepository.update(Mockito.any())).thenThrow(new DataNotFoundException("", ""));
		mockMvc.perform(
				MockMvcRequestBuilders.put("/v1.0/devices").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());

	}

	// TODO:
	@Test
	public void deleteDeviceSuccessTest() throws Exception {
		Mockito.when(deviceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(deviceList);
		Mockito.when(deviceRepository.update(Mockito.any())).thenReturn(device);
		Mockito.when(registrationCenterMachineDeviceRepository
				.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(new ArrayList<RegistrationCenterMachineDevice>());
		Mockito.when(registrationCenterDeviceRepository.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(new ArrayList<RegistrationCenterDevice>());
		when(deviceHistoryRepository.create(Mockito.any())).thenReturn(deviceHistory);
		mockMvc.perform(MockMvcRequestBuilders.delete("/v1.0/devices/123").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteDeviceDependecyTest() throws Exception {
		List<RegistrationCenterMachineDevice> regCenterMachineDevices = new ArrayList<RegistrationCenterMachineDevice>();
		regCenterMachineDevices.add(registrationCenterMachineDevice);
		Mockito.when(deviceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(deviceList);
		Mockito.when(deviceRepository.update(Mockito.any())).thenReturn(device);
		Mockito.when(registrationCenterMachineDeviceRepository
				.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any())).thenReturn(regCenterMachineDevices);
		Mockito.when(registrationCenterDeviceRepository.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(new ArrayList<RegistrationCenterDevice>());
		when(deviceHistoryRepository.create(Mockito.any())).thenReturn(deviceHistory);
		mockMvc.perform(MockMvcRequestBuilders.delete("/v1.0/devices/123").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteDeviceExceptionTest() throws Exception {
		List<Device> emptList = new ArrayList<>();
		Mockito.when(deviceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(emptList);
		mockMvc.perform(MockMvcRequestBuilders.delete("/v1.0/devices/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteDeviceDatabaseConnectionExceptionTest() throws Exception {
		when(deviceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any())).thenReturn(deviceList);
		when(deviceRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/devices/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	// -----------------------------------------MachineHistory---------------------------------------------
	@Test
	public void getMachineHistroyIdLangEffDTimeSuccessTest() throws Exception {
		when(machineHistoryRepository
				.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(machineHistoryList);
		mockMvc.perform(get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG",
				"2018-01-01T10:10:30.956Z")).andExpect(status().isOk());
	}

	@Test
	public void getMachineHistroyIdLangEffDTimeNullResponseTest() throws Exception {
		when(machineHistoryRepository
				.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(null);
		mockMvc.perform(get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG",
				"2018-01-01T10:10:30.956Z")).andExpect(status().isOk());
	}

	@Test
	public void getMachineHistroyIdLangEffDTimeFetchExceptionTest() throws Exception {
		when(machineHistoryRepository
				.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any()))
								.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG",
				"2018-01-01T10:10:30.956Z")).andExpect(status().isInternalServerError());
	}

	@Test
	public void addBlackListedWordTest() throws Exception {
		RequestDto<BlacklistedWordsDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("test  word");
		blacklistedWordsDto.setLangCode("eng");
		blacklistedWordsDto.setDescription("test description");
		blacklistedWordsDto.setIsActive(true);
		requestDto.setRequest(blacklistedWordsDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setLangCode("TST");
		Mockito.when(wordsRepository.create(Mockito.any())).thenReturn(blacklistedWords);
		mockMvc.perform(post("/v1.0/blacklistedwords").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void createBlacklistedWordsLangValidationExceptionTest() throws Exception {
		RequestDto<BlacklistedWordsDto> requestDto = new RequestDto<BlacklistedWordsDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("test  word");
		blacklistedWordsDto.setLangCode("akk");
		blacklistedWordsDto.setDescription("test description");
		blacklistedWordsDto.setIsActive(true);
		requestDto.setRequest(blacklistedWordsDto);
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/blacklistedwords").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void addBlackListedWordExceptionTest() throws Exception {
		RequestDto<BlacklistedWordsDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("test  word");
		blacklistedWordsDto.setLangCode("eng");
		blacklistedWordsDto.setDescription("test description");
		blacklistedWordsDto.setIsActive(true);
		requestDto.setRequest(blacklistedWordsDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(wordsRepository.create(Mockito.any())).thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(post("/v1.0/blacklistedwords").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void createRegistrationCenterTypeListTest() throws Exception {
		RequestDto<RegistrationCenterTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterTypeDto registrationCenterTypeDto = new RegistrationCenterTypeDto();
		registrationCenterTypeDto.setCode("testcode");
		registrationCenterTypeDto.setDescr("testdescription");
		registrationCenterTypeDto.setIsActive(true);
		registrationCenterTypeDto.setLangCode("eng");
		registrationCenterTypeDto.setName("testname");
		requestDto.setRequest(registrationCenterTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterTypeRepository.create(Mockito.any())).thenReturn(regCenterType);
		mockMvc.perform(
				post("/v1.0/registrationcentertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void createRegistrationCenterTypeListLanguageCodeValidationTest() throws Exception {
		RequestDto<RegistrationCenterTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterTypeDto registrationCenterTypeDto = new RegistrationCenterTypeDto();
		registrationCenterTypeDto.setCode("testcode");
		registrationCenterTypeDto.setDescr("testdescription");
		registrationCenterTypeDto.setIsActive(true);
		registrationCenterTypeDto.setLangCode("xyz");
		registrationCenterTypeDto.setName("testname");
		requestDto.setRequest(registrationCenterTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterTypeRepository.create(Mockito.any())).thenReturn(regCenterType);
		mockMvc.perform(
				post("/v1.0/registrationcentertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void createRegistrationCenterTypeListTestExceptionTest() throws Exception {
		RequestDto<RegistrationCenterTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterTypeDto registrationCenterTypeDto = new RegistrationCenterTypeDto();
		registrationCenterTypeDto.setCode("testcode");
		registrationCenterTypeDto.setDescr("testdescription");
		registrationCenterTypeDto.setIsActive(true);
		registrationCenterTypeDto.setLangCode("eng");
		registrationCenterTypeDto.setName("testname");
		requestDto.setRequest(registrationCenterTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(
				post("/v1.0/registrationcentertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void createIdTypeTest() throws Exception {
		RequestDto<IdTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		IdTypeDto idTypeDto = new IdTypeDto();
		idTypeDto.setCode("testcode");
		idTypeDto.setDescr("testdescription");
		idTypeDto.setIsActive(true);
		idTypeDto.setLangCode("eng");
		idTypeDto.setName("testname");
		requestDto.setRequest(idTypeDto);
		String content = mapper.writeValueAsString(requestDto);
		IdType idType = new IdType();
		idType.setCode("IDT001");
		when(idTypeRepository.create(Mockito.any())).thenReturn(idType);
		mockMvc.perform(post("/v1.0/idtypes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void createIdTypeLanguageCodeValidatorTest() throws Exception {
		RequestDto<IdTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		IdTypeDto idTypeDto = new IdTypeDto();
		idTypeDto.setCode("testcode");
		idTypeDto.setDescr("testdescription");
		idTypeDto.setIsActive(true);
		idTypeDto.setLangCode("xxx");
		idTypeDto.setName("testname");
		requestDto.setRequest(idTypeDto);
		String content = mapper.writeValueAsString(requestDto);
		IdType idType = new IdType();
		idType.setCode("IDT001");
		when(idTypeRepository.create(Mockito.any())).thenReturn(idType);
		mockMvc.perform(post("/v1.0/idtypes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void createIdTypeExceptionTest() throws Exception {
		RequestDto<IdTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		IdTypeDto idTypeDto = new IdTypeDto();
		idTypeDto.setCode("testcode");
		idTypeDto.setDescr("testdescription");
		idTypeDto.setIsActive(true);
		idTypeDto.setLangCode("eng");
		idTypeDto.setName("testname");
		requestDto.setRequest(idTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(idTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(post("/v1.0/idtypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void addDocumentTypeListTest() throws Exception {
		RequestDto<DocumentTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentTypeDto documentTypeDto = new DocumentTypeDto();
		documentTypeDto.setCode("D001");
		documentTypeDto.setDescription("Proof Of Identity");
		documentTypeDto.setIsActive(true);
		documentTypeDto.setLangCode("eng");
		documentTypeDto.setName("POI");
		requestDto.setRequest(documentTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);

		when(documentTypeRepository.create(Mockito.any())).thenReturn(type);
		mockMvc.perform(post("/v1.0/documenttypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void addDocumentTypesDatabaseConnectionExceptionTest() throws Exception {
		RequestDto<DocumentTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentTypeDto documentTypeDto = new DocumentTypeDto();
		documentTypeDto.setCode("D001");
		documentTypeDto.setDescription("Proof Of Identity");
		documentTypeDto.setIsActive(true);
		documentTypeDto.setLangCode("eng");
		documentTypeDto.setName("POI");
		requestDto.setRequest(documentTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(documentTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(post("/v1.0/documenttypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void addDocumentTypesLangCodeValidationTest() throws Exception {
		RequestDto<DocumentTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentTypeDto documentTypeDto = new DocumentTypeDto();
		documentTypeDto.setCode("D001");
		documentTypeDto.setDescription("Proof Of Identity");
		documentTypeDto.setIsActive(true);
		documentTypeDto.setLangCode("akk");
		documentTypeDto.setName("POI");
		requestDto.setRequest(documentTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/documenttypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateDocumentTypeTest() throws Exception {
		RequestDto<DocumentTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentTypeDto documentTypeDto = new DocumentTypeDto();
		documentTypeDto.setCode("D001");
		documentTypeDto.setDescription("Proof Of Identity");
		documentTypeDto.setIsActive(true);
		documentTypeDto.setLangCode("eng");
		documentTypeDto.setName("POI");
		requestDto.setRequest(documentTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(documentTypeRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(type);
		when(documentTypeRepository.update(Mockito.any())).thenReturn(type);
		mockMvc.perform(put("/v1.0/documenttypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateDocumentTypeLangValidationTest() throws Exception {
		RequestDto<DocumentTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentTypeDto documentTypeDto = new DocumentTypeDto();
		documentTypeDto.setCode("D001");
		documentTypeDto.setDescription("Proof Of Identity");
		documentTypeDto.setIsActive(true);
		documentTypeDto.setLangCode("akk");
		documentTypeDto.setName("POI");
		requestDto.setRequest(documentTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(documentTypeRepository.update(Mockito.any())).thenReturn(type);
		mockMvc.perform(put("/v1.0/documenttypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateDocumentTypeNotFoundExceptionTest() throws Exception {
		RequestDto<DocumentTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentTypeDto documentTypeDto = new DocumentTypeDto();
		documentTypeDto.setCode("D001");
		documentTypeDto.setDescription("Proof Of Identity");
		documentTypeDto.setIsActive(true);
		documentTypeDto.setLangCode("ENG");
		documentTypeDto.setName("POI");
		requestDto.setRequest(documentTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(documentTypeRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(null);
		mockMvc.perform(put("/v1.0/documenttypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateDocumentTypeDatabaseConnectionExceptionTest() throws Exception {
		RequestDto<DocumentTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentTypeDto documentTypeDto = new DocumentTypeDto();
		documentTypeDto.setCode("D001");
		documentTypeDto.setDescription("Proof Of Identity");
		documentTypeDto.setIsActive(true);
		documentTypeDto.setLangCode("eng");
		documentTypeDto.setName("POI");
		requestDto.setRequest(documentTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(documentTypeRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(type);
		when(documentTypeRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(put("/v1.0/documenttypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void deleteDocumentTypeTest() throws Exception {

		when(validDocumentRepository.findByDocTypeCode(Mockito.anyString())).thenReturn(new ArrayList<ValidDocument>());
		when(documentTypeRepository.deleteDocumentType(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(2);
		mockMvc.perform(delete("/v1.0/documenttypes/DT001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteDocumentTypeNotFoundExceptionTest() throws Exception {
		when(validDocumentRepository.findByDocTypeCode(Mockito.anyString())).thenReturn(new ArrayList<ValidDocument>());
		when(documentTypeRepository.deleteDocumentType(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0);

		mockMvc.perform(delete("/v1.0/documenttypes/DT001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteDocumentTypeDatabaseConnectionExceptionTest() throws Exception {
		when(validDocumentRepository.findByDocCategoryCode(Mockito.anyString()))
				.thenReturn(new ArrayList<ValidDocument>());
		when(documentTypeRepository.deleteDocumentType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/documenttypes/DT001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void deleteDocumentTypeDependencyExceptionTest() throws Exception {
		ValidDocument document = new ValidDocument();
		List<ValidDocument> validDocumentList = new ArrayList<>();
		validDocumentList.add(document);
		when(validDocumentRepository.findByDocTypeCode(Mockito.anyString())).thenReturn(validDocumentList);
		mockMvc.perform(delete("/v1.0/documenttypes/DT001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void insertValidDocumentExceptionTest() throws Exception {
		RequestDto<ValidDocumentDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		ValidDocumentDto validDocumentDto = new ValidDocumentDto();
		validDocumentDto.setIsActive(true);
		validDocumentDto.setLangCode("eng");
		validDocumentDto.setDocTypeCode("TEST");
		validDocumentDto.setDocCategoryCode("TSC");
		requestDto.setRequest(validDocumentDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(validDocumentRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(post("/v1.0/validdocuments").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void addDocumentCategoryTest() throws Exception {
		RequestDto<DocumentCategoryDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
		documentCategoryDto.setCode("D001");
		documentCategoryDto.setDescription("Proof Of Identity");
		documentCategoryDto.setIsActive(true);
		documentCategoryDto.setLangCode("eng");
		documentCategoryDto.setName("POI");
		requestDto.setRequest(documentCategoryDto);
		String contentJson = mapper.writeValueAsString(requestDto);

		when(documentCategoryRepository.create(Mockito.any())).thenReturn(category);
		mockMvc.perform(post("/v1.0/documentcategories").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void addDocumentCategoryLanguageCodeValidatorTest() throws Exception {
		RequestDto<DocumentCategoryDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
		documentCategoryDto.setCode("D001");
		documentCategoryDto.setDescription("Proof Of Identity");
		documentCategoryDto.setIsActive(true);
		documentCategoryDto.setLangCode("xxx");
		documentCategoryDto.setName("POI");
		requestDto.setRequest(documentCategoryDto);
		String contentJson = mapper.writeValueAsString(requestDto);

		when(documentCategoryRepository.create(Mockito.any())).thenReturn(category);
		mockMvc.perform(post("/v1.0/documentcategories").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void addDocumentCategoryDatabaseConnectionExceptionTest() throws Exception {
		RequestDto<DocumentCategoryDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
		documentCategoryDto.setCode("D001");
		documentCategoryDto.setDescription("Proof Of Identity");
		documentCategoryDto.setIsActive(true);
		documentCategoryDto.setLangCode("eng");
		documentCategoryDto.setName("POI");
		requestDto.setRequest(documentCategoryDto);
		String contentJson = mapper.writeValueAsString(requestDto);

		when(documentCategoryRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(post("/v1.0/documentcategories").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void updateDocumentCategoryTest() throws Exception {
		RequestDto<DocumentCategoryDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
		documentCategoryDto.setCode("D001");
		documentCategoryDto.setDescription("Proof Of Identity");
		documentCategoryDto.setIsActive(true);
		documentCategoryDto.setLangCode("eng");
		documentCategoryDto.setName("POI");
		requestDto.setRequest(documentCategoryDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(documentCategoryRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(category);
		mockMvc.perform(put("/v1.0/documentcategories").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateDocumentCategoryNotFoundExceptionTest() throws Exception {
		RequestDto<DocumentCategoryDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
		documentCategoryDto.setCode("D001");
		documentCategoryDto.setDescription("Proof Of Identity");
		documentCategoryDto.setIsActive(true);
		documentCategoryDto.setLangCode("eng");
		documentCategoryDto.setName("POI");
		requestDto.setRequest(documentCategoryDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(documentCategoryRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(null);
		mockMvc.perform(put("/v1.0/documentcategories").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateDocumentCategoryDatabaseConnectionExceptionTest() throws Exception {
		RequestDto<DocumentCategoryDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
		documentCategoryDto.setCode("D001");
		documentCategoryDto.setDescription("Proof Of Identity");
		documentCategoryDto.setIsActive(true);
		documentCategoryDto.setLangCode("eng");
		documentCategoryDto.setName("POI");
		requestDto.setRequest(documentCategoryDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(documentCategoryRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(category);
		when(documentCategoryRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(put("/v1.0/documentcategories").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteDocumentCategoryTest() throws Exception {
		when(validDocumentRepository.findByDocCategoryCode(Mockito.anyString()))
				.thenReturn(new ArrayList<ValidDocument>());
		when(documentCategoryRepository.deleteDocumentCategory(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(2);
		mockMvc.perform(delete("/v1.0/documentcategories/DC001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteDocumentCategoryNotFoundExceptionTest() throws Exception {
		when(validDocumentRepository.findByDocCategoryCode(Mockito.anyString()))
				.thenReturn(new ArrayList<ValidDocument>());
		when(documentCategoryRepository.deleteDocumentCategory(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(0);

		mockMvc.perform(delete("/v1.0/documentcategories/DC001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteDocumentCategoryDatabaseConnectionExceptionTest() throws Exception {
		when(validDocumentRepository.findByDocCategoryCode(Mockito.anyString()))
				.thenReturn(new ArrayList<ValidDocument>());
		when(documentCategoryRepository.deleteDocumentCategory(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/documentcategories/DC001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void deleteDocumentCategoryDependencyExceptionTest() throws Exception {
		ValidDocument document = new ValidDocument();
		List<ValidDocument> validDocumentList = new ArrayList<>();
		validDocumentList.add(document);
		when(validDocumentRepository.findByDocCategoryCode(Mockito.anyString())).thenReturn(validDocumentList);
		mockMvc.perform(delete("/v1.0/documentcategories/DC001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void insertValidDocumentTest() throws Exception {
		RequestDto<ValidDocumentDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		ValidDocumentDto validDocumentDto = new ValidDocumentDto();
		validDocumentDto.setIsActive(true);
		validDocumentDto.setLangCode("eng");
		validDocumentDto.setDocCategoryCode("TSC");
		validDocumentDto.setDocTypeCode("TEST");
		requestDto.setRequest(validDocumentDto);
		String contentJson = mapper.writeValueAsString(requestDto);

		when(validDocumentRepository.create(Mockito.any())).thenReturn(validDocument);
		mockMvc.perform(post("/v1.0/validdocuments").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteValidDocumentTest() throws Exception {
		when(validDocumentRepository.deleteValidDocument(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(1);
		mockMvc.perform(delete("/v1.0/validdocuments/DC001/DT001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteValidDocumentNotFoundExceptionTest() throws Exception {
		when(validDocumentRepository.deleteValidDocument(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(0);
		mockMvc.perform(delete("/v1.0/validdocuments/DC001/DT001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteValidDocumentDatabaseConnectionExceptionTest() throws Exception {
		when(validDocumentRepository.deleteValidDocument(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/validdocuments/DC001/DT001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void createRegistrationCenterExceptionTest() throws Exception {
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto registrationCenterDto = getRegCenterDto();

		requestDto.setRequest(registrationCenterDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(post("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void createRegistrationCenterLangExceptionTest() throws Exception {
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto registrationCenterDto = getRegCenterDto();
		registrationCenterDto.setLangCode(null);

		requestDto.setRequest(registrationCenterDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

		registrationCenterDto.setLangCode("fsadgdsagdsaf");
		contentJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(null);
		registrationCenterDto.setLangCode("eng");
		contentJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().is5xxServerError());

	}

	@Test
	public void registrationCenterInvalidTest() throws Exception {
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto invalidRegCntrDto = getRegCenterDto();
		invalidRegCntrDto.setId("ID3456789102787");// more than 10 to check for invalid
		requestDto.setRequest(invalidRegCntrDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	private RegistrationCenterDto getRegCenterDto() {

		RegistrationCenterDto dto = new RegistrationCenterDto();
		dto.setIsActive(true);
		dto.setId("ID");
		dto.setName("testname");
		dto.setAddressLine1("test");
		dto.setAddressLine2("test");
		dto.setAddressLine3("test");
		dto.setLangCode("eng");
		dto.setLocationCode("LOC01");
		dto.setLatitude("12.9135636");
		dto.setLongitude("77.5950804");
		return dto;
	}

	/*------------------------- deviceSecification update and delete ----------------------------*/
	@Test
	public void updateDeviceSpecificationTest() throws Exception {
		RequestDto<DeviceSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		requestDto.setRequest(deviceSpecificationDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(deviceSpecificationRepository.findByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(deviceSpecification);
		when(deviceSpecificationRepository.update(Mockito.any())).thenReturn(deviceSpecification);
		mockMvc.perform(put("/v1.0/devicespecifications").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateDeviceSpecificationLangCodeValidationTest() throws Exception {
		RequestDto<DeviceSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		deviceSpecificationDto.setLangCode("akk");
		requestDto.setRequest(deviceSpecificationDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(put("/v1.0/devicespecifications").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateDeviceSpecificationRequestExceptionTest() throws Exception {
		RequestDto<DeviceSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");

		requestDto.setRequest(deviceSpecificationDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(deviceSpecificationRepository.findByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(null);
		mockMvc.perform(put("/v1.0/devicespecifications").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateDeviceSpecificationDatabaseConnectionExceptionTest() throws Exception {
		RequestDto<DeviceSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		requestDto.setRequest(deviceSpecificationDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(deviceSpecificationRepository.findByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(deviceSpecification);
		when(deviceSpecificationRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(put("/v1.0/devicespecifications").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteDeviceSpecificationTest() throws Exception {

		when(deviceSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(deviceSpecList);
		when(deviceSpecificationRepository.findByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(deviceSpecification);
		when(deviceRepository.findDeviceByDeviceSpecIdAndIsDeletedFalseorIsDeletedIsNull(deviceSpecification.getId()))
				.thenReturn(new ArrayList<Device>());
		when(deviceSpecificationRepository.update(Mockito.any())).thenReturn(deviceSpecification);
		mockMvc.perform(delete("/v1.0/devicespecifications/DS001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteDeviceSpecificationNotFoundExceptionTest() throws Exception {
		List<DeviceSpecification> emptList = new ArrayList<>();
		when(deviceSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(emptList);
		mockMvc.perform(delete("/v1.0/devicespecifications/DS001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteDeviceSpecificationDatabaseConnectionExceptionTest() throws Exception {
		when(deviceSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(deviceSpecList);
		when(deviceSpecificationRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/devicespecifications/DS001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteDeviceSpecificationdependencyExceptionTest() throws Exception {
		List<Device> deviceList = new ArrayList<Device>();
		Device device = new Device();
		deviceList.add(device);
		when(deviceSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(deviceSpecList);
		when(deviceRepository.findDeviceByDeviceSpecIdAndIsDeletedFalseorIsDeletedIsNull(deviceSpecification.getId()))
				.thenReturn(deviceList);
		mockMvc.perform(delete("/v1.0/devicespecifications/DS001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	/*------------------------------ template update and delete test-----------------------------*/
	@Test
	public void updateTemplateTest() throws Exception {
		RequestDto<TemplateDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		requestDto.setRequest(templateDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(templateRepository.findTemplateByIDAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(template);
		when(templateRepository.update(Mockito.any())).thenReturn(template);
		mockMvc.perform(put("/v1.0/templates").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateTemplateLangCodeValidationTest() throws Exception {
		RequestDto<TemplateDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		templateDto.setLangCode("akk");
		requestDto.setRequest(templateDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		mockMvc.perform(put("/v1.0/templates").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateTemplateNotRequestExceptionTest() throws Exception {
		RequestDto<TemplateDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		requestDto.setRequest(templateDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(templateRepository.findTemplateByIDAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(null);
		mockMvc.perform(put("/v1.0/templates").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateTemplateDatabaseConnectionExceptionTest() throws Exception {
		RequestDto<TemplateDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		requestDto.setRequest(templateDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(templateRepository.findTemplateByIDAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(template);
		when(templateRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(put("/v1.0/templates").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteTemplateTest() throws Exception {
		when(templateRepository.deleteTemplate(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		mockMvc.perform(delete("/v1.0/templates/T001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteTemplateRequestExceptionTest() throws Exception {
		when(templateRepository.deleteTemplate(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0);
		mockMvc.perform(delete("/v1.0/templates/T001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteTemplateDatabaseConnectionExceptionTest() throws Exception {
		when(templateRepository.deleteTemplate(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/templates/T001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	// ------------------------------- TemplateFileFormat Test
	@Test
	public void updateTemplateFileFormatSuccessTest() throws Exception {
		RequestDto<TemplateFileFormatDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.device.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(templateFileFormatDto);
		String content = mapper.writeValueAsString(requestDto);
		Mockito.when(templateFileFormatRepository
				.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(templateFileFormat);
		Mockito.when(templateFileFormatRepository.update(Mockito.any())).thenReturn(templateFileFormat);
		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/templatefileformats").contentType(MediaType.APPLICATION_JSON)
				.content(content)).andExpect(status().isOk());
	}

	@Test
	public void updateTemplateFileFormatLanguageCodeValidationTest() throws Exception {
		RequestDto<TemplateFileFormatDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.device.update");
		requestDto.setVer("1.0.0");
		templateFileFormatDto.setLangCode("xxx");
		requestDto.setRequest(templateFileFormatDto);
		String content = mapper.writeValueAsString(requestDto);
		Mockito.when(templateFileFormatRepository
				.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(templateFileFormat);
		Mockito.when(templateFileFormatRepository.update(Mockito.any())).thenReturn(templateFileFormat);
		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/templatefileformats").contentType(MediaType.APPLICATION_JSON)
				.content(content)).andExpect(status().isOk());
	}

	@Test
	public void updateTemplateFileFormatExceptionTest() throws Exception {
		RequestDto<TemplateFileFormatDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.device.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(templateFileFormatDto);
		String content = mapper.writeValueAsString(requestDto);

		Mockito.when(templateFileFormatRepository
				.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);
		Mockito.when(templateFileFormatRepository.update(Mockito.any())).thenThrow(new RequestException("", ""));
		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/templatefileformats").contentType(MediaType.APPLICATION_JSON)
				.content(content)).andExpect(status().isOk());
	}

	@Test
	public void deleteTemplateFileFormatSuccessTest() throws Exception {
		List<Template> templates = new ArrayList<>();
		Mockito.when(templateRepository.findAllByFileFormatCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(templates);
		Mockito.when(templateFileFormatRepository.deleteTemplateFileFormat(Mockito.anyString(), Mockito.any(),
				Mockito.anyString())).thenReturn(1);
		Mockito.when(templateFileFormatRepository.update(Mockito.any())).thenReturn(templateFileFormat);
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/v1.0/templatefileformats/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteTemplateFileFormatExceptionTest() throws Exception {
		List<Template> templates = new ArrayList<>();
		Mockito.when(templateRepository.findAllByFileFormatCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(templates);
		Mockito.when(templateFileFormatRepository.deleteTemplateFileFormat(Mockito.anyString(), Mockito.any(),
				Mockito.anyString())).thenThrow(new RequestException("", ""));
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/v1.0/templatefileformats/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	/*------------------------------------Holiday Update/delete -------------------------------------*/

	@Test
	public void deleteHolidaySuccess() throws Exception {
		String input = "{\"id\": \"string\",\"request\":{\"locationCode\":\"LOC01\",\"holidayDate\":\"2019-01-01\",\"holidayName\":\"New Year\"},\"timestamp\": \"2018-12-24T06:15:12.494Z\",\"ver\": \"string\"}";
		when(holidayRepository.deleteHolidays(any(), anyString(), any(), anyString())).thenReturn(1);
		mockMvc.perform(delete("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteHolidayNoHolidayFound() throws Exception {
		String input = "{\"id\": \"string\",\"request\":{\"locationCode\":\"LOC01\",\"holidayDate\":\"2019-01-01\",\"holidayName\":\"New Year\"},\"timestamp\": \"2018-12-24T06:15:12.494Z\",\"ver\": \"string\"}";
		when(holidayRepository.deleteHolidays(any(), anyString(), any(), anyString())).thenReturn(0);
		mockMvc.perform(delete("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteHolidayFailure() throws Exception {
		String input = "{\"id\": \"string\",\"request\":{\"locationCode\":\"LOC01\",\"holidayDate\":\"2019-01-01\",\"holidayName\":\"New Year\"},\"timestamp\": \"2018-12-24T06:15:12.494Z\",\"ver\": \"string\"}";
		when(holidayRepository.deleteHolidays(any(), anyString(), any(), anyString()))
				.thenThrow(DataRetrievalFailureException.class, DataAccessLayerException.class);
		mockMvc.perform(delete("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void updateHolidaySuccessTest() throws Exception {
		String input = "{\"id\": \"string\",\"request\":{\"holidayDate\": \"2018-01-01\", \"holidayDesc\": \"New Year\",\"holidayName\": \"New Year\",\"id\": 1,\"isActive\": false,\"langCode\": \"eng\",\"locationCode\": \"LOC01\"},\"timestamp\": \"2018-12-24T06:26:18.807Z\",\"ver\": \"string\"}";
		when(holidayRepository.createQueryUpdateOrDelete(any(), any())).thenReturn(1);
		mockMvc.perform(put("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isOk());
	}

	@Test
	public void updateHolidayLanguageValidationTest() throws Exception {
		String input = "{\"id\": \"string\",\"request\":{\"holidayDate\": \"2018-01-01\", \"holidayDesc\": \"New Year\",\"holidayName\": \"New Year\",\"id\": 1,\"isActive\": false,\"langCode\": \"asd\",\"locationCode\": \"LOC01\"},\"timestamp\": \"2018-12-24T06:26:18.807Z\",\"ver\": \"string\"}";
		when(holidayRepository.createQueryUpdateOrDelete(any(), any())).thenReturn(1);
		mockMvc.perform(put("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isOk());
	}

	@Test
	public void updateHolidaySuccessNewNameAndDateTest() throws Exception {
		String input = "{\"id\": \"string\",\"request\":{\"holidayDate\": \"2018-01-01\", \"holidayDesc\": \"New Year\",\"holidayName\": \"New Year\",\"id\": 1,\"isActive\": false,\"langCode\": \"eng\",\"locationCode\": \"LOC01\",\"newHolidayDate\": \"2019-01-01\",\"newHolidayDesc\": \"New Year Desc\",\"newHolidayName\": \"New Year\"},\"timestamp\": \"2018-12-24T06:26:18.807Z\",\"ver\": \"string\"}";
		when(holidayRepository.createQueryUpdateOrDelete(any(), any())).thenReturn(1);
		mockMvc.perform(put("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isOk());
	}

	@Test
	public void updateHolidaySuccessNewData() throws Exception {
		String input = "{\"id\": \"string\",\"request\":{\"holidayDate\": \"2018-01-01\", \"holidayDesc\": \"New Year\",\"holidayName\": \"New Year\",\"id\": 1,\"isActive\": false,\"langCode\": \"eng\",\"locationCode\": \"LOC01\",\"newHolidayDate\": null,\"newHolidayDesc\": \" \",\"newHolidayName\": \" \"},\"timestamp\": \"2018-12-24T06:26:18.807Z\",\"ver\": \"string\"}";
		when(holidayRepository.createQueryUpdateOrDelete(any(), any())).thenReturn(1);
		mockMvc.perform(put("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isOk());
	}

	@Test
	public void updateHolidayNoHolidayUpdated() throws Exception {
		String input = "{\"id\": \"string\",\"request\":{\"holidayDate\": \"2018-01-01\", \"holidayDesc\": \"New Year\",\"holidayName\": \"New Year\",\"id\": 1,\"isActive\": false,\"langCode\": \"ENG\",\"locationCode\": \"LOC01\"},\"timestamp\": \"2018-12-24T06:26:18.807Z\",\"ver\": \"string\"}";
		when(holidayRepository.createQueryUpdateOrDelete(any(), any())).thenReturn(0);
		mockMvc.perform(put("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateHolidayNoHolidayFailure() throws Exception {
		String input = "{\"id\": \"string\",\"request\":{\"holidayDate\": \"2018-01-01\", \"holidayDesc\": \"New Year\",\"holidayName\": \"New Year\",\"id\": 1,\"isActive\": false,\"langCode\": \"eng\",\"locationCode\": \"LOC01\"},\"timestamp\": \"2018-12-24T06:26:18.807Z\",\"ver\": \"string\"}";
		when(holidayRepository.createQueryUpdateOrDelete(any(), any())).thenThrow(DataRetrievalFailureException.class,
				DataAccessLayerException.class);
		mockMvc.perform(put("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isInternalServerError());
	}

	/*------------------------------------Blacklisted Word Update/delete -------------------------------------*/

	@Test
	public void deleteBlacklistedWordSuccess() throws Exception {
		when(wordsRepository.deleteBlackListedWord(anyString(), any())).thenReturn(1);
		mockMvc.perform(delete("/v1.0/blacklistedwords/{word}", "abc")).andExpect(status().isOk());
	}

	@Test
	public void deleteBlacklistedWordNoWordDeleted() throws Exception {
		when(wordsRepository.deleteBlackListedWord(anyString(), any())).thenReturn(0);
		mockMvc.perform(delete("/v1.0/blacklistedwords/{word}", "abc")).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteBlacklistedWordFailure() throws Exception {
		when(wordsRepository.deleteBlackListedWord(anyString(), any())).thenThrow(DataRetrievalFailureException.class,
				DataAccessLayerException.class);
		mockMvc.perform(delete("/v1.0/blacklistedwords/{word}", "abc")).andExpect(status().isInternalServerError());
	}

	@Test
	public void updateBadWordSuccess() throws Exception {
		String input = "{\"id\": \"string\",\"request\": {\"description\": \"bad word description\",\"isActive\": false,\"langCode\": \"eng\",\"word\": \"badword\"},\"timestamp\": \"2018-12-24T07:21:42.232Z\",\"ver\": \"string\"}";
		when(wordsRepository.findByWordAndLangCode(anyString(), anyString())).thenReturn(words.get(0));
		when(wordsRepository.update(any())).thenReturn(words.get(0));
		mockMvc.perform(put("/v1.0/blacklistedwords").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isOk());
	}

	@Test
	public void updateBlacklistedWordsLangValidationExceptionTest() throws Exception {
		RequestDto<BlacklistedWordsDto> requestDto = new RequestDto<BlacklistedWordsDto>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("test  word");
		blacklistedWordsDto.setLangCode("akk");
		blacklistedWordsDto.setDescription("test description");
		blacklistedWordsDto.setIsActive(true);
		requestDto.setRequest(blacklistedWordsDto);
		String content = mapper.writeValueAsString(requestDto);
		mockMvc.perform(put("/v1.0/blacklistedwords").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void updateBadWordNoWordFound() throws Exception {
		String input = "{\"id\": \"string\",\"request\": {\"description\": \"bad word description\",\"isActive\": false,\"langCode\": \"ENG\",\"word\": \"badword\"},\"timestamp\": \"2018-12-24T07:21:42.232Z\",\"ver\": \"string\"}";
		mockMvc.perform(put("/v1.0/blacklistedwords").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateBadWordFailure() throws Exception {
		String input = "{\"id\": \"string\",\"request\": {\"description\": \"bad word description\",\"isActive\": false,\"langCode\": \"eng\",\"word\": \"badword\"},\"timestamp\": \"2018-12-24T07:21:42.232Z\",\"ver\": \"string\"}";
		when(wordsRepository.findByWordAndLangCode(anyString(), anyString())).thenReturn(words.get(0));
		when(wordsRepository.update(any())).thenThrow(DataRetrievalFailureException.class,
				DataAccessLayerException.class);
		mockMvc.perform(put("/v1.0/blacklistedwords").contentType(MediaType.APPLICATION_JSON).content(input))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void updateRegistrationCenterTypeTest() throws Exception {
		RequestDto<RegistrationCenterTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterTypeDto registrationCenterTypeDto = new RegistrationCenterTypeDto();
		registrationCenterTypeDto.setCode("D001");
		registrationCenterTypeDto.setIsActive(true);
		registrationCenterTypeDto.setLangCode("eng");
		registrationCenterTypeDto.setName("POI");
		registrationCenterTypeDto.setDescr("TEST DESCR");
		requestDto.setRequest(registrationCenterTypeDto);
		RegistrationCenterType registrationCenterType = new RegistrationCenterType();
		registrationCenterType.setCode("D001");
		registrationCenterType.setDescr("TEST DESCR");
		registrationCenterType.setName("POI");
		String contentJson = mapper.writeValueAsString(requestDto);

		when(registrationCenterTypeRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(registrationCenterType);
		when(registrationCenterTypeRepository.update(Mockito.any())).thenReturn(registrationCenterType);
		mockMvc.perform(
				put("/v1.0/registrationcentertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateRegistrationCenterTypeLanguageCodeValidatorTest() throws Exception {
		RequestDto<RegistrationCenterTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterTypeDto registrationCenterTypeDto = new RegistrationCenterTypeDto();
		registrationCenterTypeDto.setCode("D001");
		registrationCenterTypeDto.setIsActive(true);
		registrationCenterTypeDto.setLangCode("ENG");
		registrationCenterTypeDto.setName("POI");
		registrationCenterTypeDto.setDescr("TEST DESCR");
		requestDto.setRequest(registrationCenterTypeDto);
		RegistrationCenterType registrationCenterType = new RegistrationCenterType();
		registrationCenterType.setCode("D001");
		registrationCenterType.setDescr("TEST DESCR");
		registrationCenterType.setName("POI");
		String contentJson = mapper.writeValueAsString(requestDto);

		when(registrationCenterTypeRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(registrationCenterType);
		when(registrationCenterTypeRepository.update(Mockito.any())).thenReturn(registrationCenterType);
		mockMvc.perform(
				put("/v1.0/registrationcentertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateRegistrationCenterTypeNotFoundExceptionTest() throws Exception {
		RequestDto<RegistrationCenterTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterTypeDto registrationCenterTypeDto = new RegistrationCenterTypeDto();
		registrationCenterTypeDto.setCode("D001");
		registrationCenterTypeDto.setIsActive(true);
		registrationCenterTypeDto.setLangCode("eng");
		registrationCenterTypeDto.setName("POI");
		registrationCenterTypeDto.setDescr("TEST DESCR");
		requestDto.setRequest(registrationCenterTypeDto);
		RegistrationCenterType registrationCenterType = new RegistrationCenterType();
		registrationCenterType.setCode("D001");
		registrationCenterType.setDescr("TEST DESCR");
		registrationCenterType.setName("POI");
		String contentJson = mapper.writeValueAsString(requestDto);

		when(registrationCenterTypeRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(null);
		when(registrationCenterTypeRepository.update(Mockito.any())).thenReturn(registrationCenterType);
		mockMvc.perform(
				put("/v1.0/registrationcentertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());

	}

	@Test
	public void updateRegistrationCenterTypeDataAccessExceptionTest() throws Exception {
		RequestDto<RegistrationCenterTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterTypeDto registrationCenterTypeDto = new RegistrationCenterTypeDto();
		registrationCenterTypeDto.setCode("D001");
		registrationCenterTypeDto.setIsActive(true);
		registrationCenterTypeDto.setLangCode("eng");
		registrationCenterTypeDto.setName("POI");
		registrationCenterTypeDto.setDescr("TEST DESCR");
		requestDto.setRequest(registrationCenterTypeDto);
		RegistrationCenterType registrationCenterType = new RegistrationCenterType();
		registrationCenterType.setCode("D001");
		registrationCenterType.setDescr("TEST DESCR");
		registrationCenterType.setName("POI");
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterTypeRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(registrationCenterType);
		when(registrationCenterTypeRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(
				put("/v1.0/registrationcentertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteRegistrationCenterTypeTest() throws Exception {
		RegistrationCenterType registrationCenterType = new RegistrationCenterType();
		registrationCenterType.setCode("RC001");
		registrationCenterType.setName("RGC");
		ArrayList<RegistrationCenterType> list = new ArrayList<>();
		list.add(registrationCenterType);
		when(registrationCenterTypeRepository.findByCode(Mockito.any())).thenReturn(list);
		when(registrationCenterTypeRepository.deleteRegistrationCenterType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(1);
		mockMvc.perform(delete("/v1.0/registrationcentertypes/RC001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterTypeNotFoundExceptionTest() throws Exception {
		when(registrationCenterTypeRepository.findByCode(Mockito.any())).thenReturn(new ArrayList<>());
		when(registrationCenterTypeRepository.deleteRegistrationCenterType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(0);
		mockMvc.perform(delete("/v1.0/registrationcentertypes/RC001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void deleteRegistrationCenterTypeDataAccessExceptionTest() throws Exception {
		when(registrationCenterTypeRepository.findByCode(Mockito.any())).thenReturn(new ArrayList<>());
		when(registrationCenterTypeRepository.deleteRegistrationCenterType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));

		mockMvc.perform(delete("/v1.0/registrationcentertypes/RC001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void deleteRegistrationCenterTypeDependencyExceptionTest() throws Exception {
		RegistrationCenter registrationCenter = new RegistrationCenter();
		List<RegistrationCenter> registrationCenterList = new ArrayList<>();
		registrationCenterList.add(registrationCenter);
		when(registrationCenterRepository.findByCenterTypeCode(Mockito.any())).thenReturn(registrationCenterList);
		mockMvc.perform(delete("/v1.0/registrationcentertypes/RC001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	// -----------------------------------------DeviceHistory---------------------------------------------
	@Test
	public void getDeviceHistroyIdLangEffDTimeSuccessTest() throws Exception {
		when(deviceHistoryRepository
				.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(deviceHistoryList);
		mockMvc.perform(
				get("/v1.0/deviceshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG", "2018-01-01T10:10:30.956Z"))
				.andExpect(status().isOk());
	}

	@Test
	public void getDeviceHistroyIdLangEffDTimeNullResponseTest() throws Exception {
		when(deviceHistoryRepository
				.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(null);
		mockMvc.perform(
				get("/v1.0/deviceshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG", "2018-01-01T10:10:30.956Z"))
				.andExpect(status().isOk());
	}

	@Test
	public void getDeviceHistroyIdLangEffDTimeFetchExceptionTest() throws Exception {
		when(deviceHistoryRepository
				.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any()))
								.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(
				get("/v1.0/deviceshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG", "2018-01-01T10:10:30.956Z"))
				.andExpect(status().isInternalServerError());
	}

	// -------------------------------RegistrationCenterControllerTest--------------------------
	@Test
	public void testGetRegistraionCenterHolidaysSuccess() throws Exception {
		Mockito.when(registrationCenterRepository.findByIdAndLangCode(anyString(), anyString()))
				.thenReturn(registrationCenter);
		Mockito.when(holidayRepository.findAllByLocationCodeYearAndLangCode(anyString(), anyString(), anyInt()))
				.thenReturn(holidays);
		mockMvc.perform(get("/v1.0/getregistrationcenterholidays/{languagecode}/{registrationcenterid}/{year}", "ENG",
				"REG_CR_001", 2018)).andExpect(status().isOk());
	}

	@Test
	public void testGetRegistraionCenterHolidaysNoRegCenterFound() throws Exception {
		mockMvc.perform(get("/v1.0/getregistrationcenterholidays/{languagecode}/{registrationcenterid}/{year}", "ENG",
				"REG_CR_001", 2017)).andExpect(status().isOk());
	}

	@Test
	public void testGetRegistraionCenterHolidaysRegistrationCenterFetchException() throws Exception {
		Mockito.when(registrationCenterRepository.findByIdAndLangCode(anyString(), anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/getregistrationcenterholidays/{languagecode}/{registrationcenterid}/{year}", "ENG",
				"REG_CR_001", 2017)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetRegistraionCenterHolidaysHolidayFetchException() throws Exception {
		Mockito.when(registrationCenterRepository.findByIdAndLangCode(anyString(), anyString()))
				.thenReturn(registrationCenter);

		Mockito.when(holidayRepository.findAllByLocationCodeYearAndLangCode(anyString(), anyString(), anyInt()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/getregistrationcenterholidays/{languagecode}/{registrationcenterid}/{year}", "ENG",
				"REG_CR_001", 2018)).andExpect(status().isInternalServerError());
	}

	// -------------------Registration center device history-----------
	@Test
	public void getRegCentDevHistByregCentIdDevIdEffTimeTest() throws Exception {
		when(registrationCenterDeviceHistoryRepository
				.findByFirstByRegCenterIdAndDeviceIdAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any()))
								.thenReturn(registrationCenterDeviceHistory);
		mockMvc.perform(get("/v1.0/registrationcenterdevicehistory/{regcenterid}/{deviceid}/{effdatetimes}", "RCI1000",
				"DID10", "2018-01-01T10:10:30.956Z")).andExpect(status().isOk());
	}

	@Test
	public void getRegCentDevHistByregCentIdDevIdEffTimeNullResponseTest() throws Exception {
		when(registrationCenterDeviceHistoryRepository
				.findByFirstByRegCenterIdAndDeviceIdAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(null);
		mockMvc.perform(get("/v1.0/registrationcenterdevicehistory/{regcenterid}/{deviceid}/{effdatetimes}", "RCI1000",
				"DID10", "2018-01-01T10:10:30.956Z")).andExpect(status().isOk());
	}

	@Test
	public void getRegCentDevHistByregCentIdDevIdEffTimeFetchExceptionTest() throws Exception {
		when(registrationCenterDeviceHistoryRepository
				.findByFirstByRegCenterIdAndDeviceIdAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any()))
								.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/registrationcenterdevicehistory/{regcenterid}/{deviceid}/{effdatetimes}", "RCI1000",
				"DID10", "2018-01-01T10:10:30.956Z")).andExpect(status().isInternalServerError());
	}

	@Test
	public void createRegistrationCenterTest() throws Exception {
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		short numberOfKiosks = 1;
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto registrationCenterDto = new RegistrationCenterDto();
		registrationCenterDto.setName("TEST CENTER");
		registrationCenterDto.setAddressLine1("Address Line 1");
		registrationCenterDto.setAddressLine2("Address Line 2");
		registrationCenterDto.setAddressLine3("Address Line 3");
		registrationCenterDto.setCenterTypeCode("REG01");
		registrationCenterDto.setContactPerson("Test");
		registrationCenterDto.setContactPhone("9999999999");
		registrationCenterDto.setHolidayLocationCode("HLC01");
		registrationCenterDto.setId("676");
		registrationCenterDto.setIsActive(true);
		registrationCenterDto.setLangCode("eng");
		registrationCenterDto.setLatitude("12.9646818");
		registrationCenterDto.setLocationCode("LOC01");
		registrationCenterDto.setLongitude("77.70168");
		registrationCenterDto.setNumberOfKiosks((short) 1);
		registrationCenterDto.setTimeZone("UTC");
		registrationCenterDto.setWorkingHours("9");
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setName("TEST CENTER");
		registrationCenter.setAddressLine1("Address Line 1");
		registrationCenter.setAddressLine2("Address Line 2");
		registrationCenter.setAddressLine3("Address Line 3");
		registrationCenter.setCenterTypeCode("REG01");
		registrationCenter.setContactPerson("Test");
		registrationCenter.setContactPhone("9999999999");
		registrationCenter.setHolidayLocationCode("HLC01");
		registrationCenter.setId("676");
		registrationCenter.setIsActive(true);
		registrationCenter.setLangCode("eng");
		registrationCenter.setLatitude("12.9646818");
		registrationCenter.setLocationCode("LOC01");
		registrationCenter.setLongitude("77.70168");
		registrationCenter.setNumberOfKiosks(numberOfKiosks);
		registrationCenter.setTimeZone("UTC");
		registrationCenter.setWorkingHours("9");
		requestDto.setRequest(registrationCenterDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterRepository.create(Mockito.any())).thenReturn(registrationCenter);
		mockMvc.perform(post("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateRegistrationCenterTest() throws Exception {
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		short numberOfKiosks = 1;
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto registrationCenterDto = new RegistrationCenterDto();
		registrationCenterDto.setName("UPDATED TEST CENTER");
		registrationCenterDto.setAddressLine1("Address Line 1");
		registrationCenterDto.setAddressLine2("Address Line 2");
		registrationCenterDto.setAddressLine3("Address Line 3");
		registrationCenterDto.setCenterTypeCode("REG01");
		registrationCenterDto.setContactPerson("Test");
		registrationCenterDto.setContactPhone("9999999999");
		registrationCenterDto.setHolidayLocationCode("HLC01");
		registrationCenterDto.setId("676");
		registrationCenterDto.setIsActive(true);
		registrationCenterDto.setLangCode("eng");
		registrationCenterDto.setLatitude("12.9646818");
		registrationCenterDto.setLocationCode("LOC01");
		registrationCenterDto.setLongitude("77.70168");
		registrationCenterDto.setNumberOfKiosks((short) 1);
		registrationCenterDto.setTimeZone("UTC");
		registrationCenterDto.setWorkingHours("9");
		RegistrationCenter updatedRegistrationCenter = new RegistrationCenter();
		updatedRegistrationCenter.setName("TEST CENTER");
		updatedRegistrationCenter.setAddressLine1("Address Line 1");
		updatedRegistrationCenter.setAddressLine2("Address Line 2");
		updatedRegistrationCenter.setAddressLine3("Address Line 3");
		updatedRegistrationCenter.setCenterTypeCode("REG01");
		updatedRegistrationCenter.setContactPerson("Test");
		updatedRegistrationCenter.setContactPhone("9999999999");
		updatedRegistrationCenter.setHolidayLocationCode("HLC01");
		updatedRegistrationCenter.setId("676");
		updatedRegistrationCenter.setIsActive(true);
		updatedRegistrationCenter.setLangCode("eng");
		updatedRegistrationCenter.setLatitude("12.9646818");
		updatedRegistrationCenter.setLocationCode("LOC01");
		updatedRegistrationCenter.setLongitude("77.70168");
		updatedRegistrationCenter.setNumberOfKiosks(numberOfKiosks);
		updatedRegistrationCenter.setTimeZone("UTC");
		updatedRegistrationCenter.setWorkingHours("9");
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setName("TEST CENTER");
		registrationCenter.setAddressLine1("Address Line 1");
		registrationCenter.setAddressLine2("Address Line 2");
		registrationCenter.setAddressLine3("Address Line 3");
		registrationCenter.setCenterTypeCode("REG01");
		registrationCenter.setContactPerson("Test");
		registrationCenter.setContactPhone("9999999999");
		registrationCenter.setHolidayLocationCode("HLC01");
		registrationCenter.setId("676");
		registrationCenter.setIsActive(true);
		registrationCenter.setLangCode("eng");
		registrationCenter.setLatitude("12.9646818");
		registrationCenter.setLocationCode("LOC01");
		registrationCenter.setLongitude("77.70168");
		registrationCenter.setNumberOfKiosks(numberOfKiosks);
		registrationCenter.setTimeZone("UTC");
		registrationCenter.setWorkingHours("9");
		requestDto.setRequest(registrationCenterDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterRepository.findByIdAndIsDeletedFalseOrNull(Mockito.any()))
				.thenReturn(registrationCenter);
		when(registrationCenterRepository.update(Mockito.any())).thenReturn(updatedRegistrationCenter);
		mockMvc.perform(put("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterTest() throws Exception {
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		short numberOfKiosks = 1;
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto registrationCenterDto = new RegistrationCenterDto();
		registrationCenterDto.setName("TEST CENTER");
		registrationCenterDto.setAddressLine1("Address Line 1");
		registrationCenterDto.setAddressLine2("Address Line 2");
		registrationCenterDto.setAddressLine3("Address Line 3");
		registrationCenterDto.setCenterTypeCode("REG01");
		registrationCenterDto.setContactPerson("Test");
		registrationCenterDto.setContactPhone("9999999999");
		registrationCenterDto.setHolidayLocationCode("HLC01");
		registrationCenterDto.setId("676");
		registrationCenterDto.setIsActive(true);
		registrationCenterDto.setLangCode("eng");
		registrationCenterDto.setLatitude("12.9646818");
		registrationCenterDto.setLocationCode("LOC01");
		registrationCenterDto.setLongitude("77.70168");
		registrationCenterDto.setNumberOfKiosks((short) 1);
		registrationCenterDto.setTimeZone("UTC");
		registrationCenterDto.setWorkingHours("9");
		RegistrationCenter updatedRegistrationCenter = new RegistrationCenter();
		updatedRegistrationCenter.setName("TEST CENTER");
		updatedRegistrationCenter.setAddressLine1("Address Line 1");
		updatedRegistrationCenter.setAddressLine2("Address Line 2");
		updatedRegistrationCenter.setAddressLine3("Address Line 3");
		updatedRegistrationCenter.setCenterTypeCode("REG01");
		updatedRegistrationCenter.setContactPerson("Test");
		updatedRegistrationCenter.setContactPhone("9999999999");
		updatedRegistrationCenter.setHolidayLocationCode("HLC01");
		updatedRegistrationCenter.setId("676");
		updatedRegistrationCenter.setIsActive(true);
		updatedRegistrationCenter.setLangCode("eng");
		updatedRegistrationCenter.setLatitude("12.9646818");
		updatedRegistrationCenter.setLocationCode("LOC01");
		updatedRegistrationCenter.setLongitude("77.70168");
		updatedRegistrationCenter.setNumberOfKiosks(numberOfKiosks);
		updatedRegistrationCenter.setTimeZone("UTC");
		updatedRegistrationCenter.setWorkingHours("9");
		updatedRegistrationCenter.setIsDeleted(true);
		List<RegistrationCenter> registrationCenterlist = new ArrayList<>();
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setName("TEST CENTER");
		registrationCenter.setAddressLine1("Address Line 1");
		registrationCenter.setAddressLine2("Address Line 2");
		registrationCenter.setAddressLine3("Address Line 3");
		registrationCenter.setCenterTypeCode("REG01");
		registrationCenter.setContactPerson("Test");
		registrationCenter.setContactPhone("9999999999");
		registrationCenter.setHolidayLocationCode("HLC01");
		registrationCenter.setId("676");
		registrationCenter.setIsActive(true);
		registrationCenter.setLangCode("eng");
		registrationCenter.setLatitude("12.9646818");
		registrationCenter.setLocationCode("LOC01");
		registrationCenter.setLongitude("77.70168");
		registrationCenter.setNumberOfKiosks(numberOfKiosks);
		registrationCenter.setTimeZone("UTC");
		registrationCenter.setWorkingHours("9");
		requestDto.setRequest(registrationCenterDto);
		registrationCenterlist.add(registrationCenter);
		String contentJson = mapper.writeValueAsString(requestDto);
		RegistrationCenterHistory registrationCenterHistory = new RegistrationCenterHistory();
		when(registrationCenterRepository.findByRegIdAndIsDeletedFalseOrNull(Mockito.any()))
				.thenReturn(registrationCenterlist);
		when(registrationCenterMachineRepository.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(new ArrayList<RegistrationCenterMachine>());

		when(registrationCenterMachineDeviceRepository
				.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
						.thenReturn(new ArrayList<RegistrationCenterMachineDevice>());

		when(registrationCenterMachineUserRepository
				.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
						.thenReturn(new ArrayList<RegistrationCenterUserMachine>());

		when(registrationCenterDeviceRepository.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(new ArrayList<RegistrationCenterDevice>());
		
		when(registrationCenterRepository.update(Mockito.any())).thenReturn(registrationCenter);
		// TODO
		when(repositoryCenterHistoryRepository.create(Mockito.any())).thenReturn(registrationCenterHistory);
		mockMvc.perform(
				delete("/v1.0/registrationcenters/676").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterDependecyTest() throws Exception {
		List<RegistrationCenterMachine> registrationCenterMachines = new ArrayList<RegistrationCenterMachine>();
		registrationCenterMachines.add(registrationCenterMachine);
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		short numberOfKiosks = 1;
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto registrationCenterDto = new RegistrationCenterDto();
		registrationCenterDto.setName("TEST CENTER");
		registrationCenterDto.setAddressLine1("Address Line 1");
		registrationCenterDto.setAddressLine2("Address Line 2");
		registrationCenterDto.setAddressLine3("Address Line 3");
		registrationCenterDto.setCenterTypeCode("REG01");
		registrationCenterDto.setContactPerson("Test");
		registrationCenterDto.setContactPhone("9999999999");
		registrationCenterDto.setHolidayLocationCode("HLC01");
		registrationCenterDto.setId("676");
		registrationCenterDto.setIsActive(true);
		registrationCenterDto.setLangCode("eng");
		registrationCenterDto.setLatitude("12.9646818");
		registrationCenterDto.setLocationCode("LOC01");
		registrationCenterDto.setLongitude("77.70168");
		registrationCenterDto.setNumberOfKiosks((short) 1);
		registrationCenterDto.setTimeZone("UTC");
		registrationCenterDto.setWorkingHours("9");
		RegistrationCenter updatedRegistrationCenter = new RegistrationCenter();
		updatedRegistrationCenter.setName("TEST CENTER");
		updatedRegistrationCenter.setAddressLine1("Address Line 1");
		updatedRegistrationCenter.setAddressLine2("Address Line 2");
		updatedRegistrationCenter.setAddressLine3("Address Line 3");
		updatedRegistrationCenter.setCenterTypeCode("REG01");
		updatedRegistrationCenter.setContactPerson("Test");
		updatedRegistrationCenter.setContactPhone("9999999999");
		updatedRegistrationCenter.setHolidayLocationCode("HLC01");
		updatedRegistrationCenter.setId("676");
		updatedRegistrationCenter.setIsActive(true);
		updatedRegistrationCenter.setLangCode("eng");
		updatedRegistrationCenter.setLatitude("12.9646818");
		updatedRegistrationCenter.setLocationCode("LOC01");
		updatedRegistrationCenter.setLongitude("77.70168");
		updatedRegistrationCenter.setNumberOfKiosks(numberOfKiosks);
		updatedRegistrationCenter.setTimeZone("UTC");
		updatedRegistrationCenter.setWorkingHours("9");
		updatedRegistrationCenter.setIsDeleted(true);
		List<RegistrationCenter> registrationCenterlist = new ArrayList<>();
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setName("TEST CENTER");
		registrationCenter.setAddressLine1("Address Line 1");
		registrationCenter.setAddressLine2("Address Line 2");
		registrationCenter.setAddressLine3("Address Line 3");
		registrationCenter.setCenterTypeCode("REG01");
		registrationCenter.setContactPerson("Test");
		registrationCenter.setContactPhone("9999999999");
		registrationCenter.setHolidayLocationCode("HLC01");
		registrationCenter.setId("676");
		registrationCenter.setIsActive(true);
		registrationCenter.setLangCode("eng");
		registrationCenter.setLatitude("12.9646818");
		registrationCenter.setLocationCode("LOC01");
		registrationCenter.setLongitude("77.70168");
		registrationCenter.setNumberOfKiosks(numberOfKiosks);
		registrationCenter.setTimeZone("UTC");
		registrationCenter.setWorkingHours("9");
		registrationCenterlist.add(registrationCenter);
		requestDto.setRequest(registrationCenterDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterRepository.findByRegIdAndIsDeletedFalseOrNull(Mockito.any()))
				.thenReturn(registrationCenterlist);
		when(registrationCenterMachineRepository.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(registrationCenterMachines);

		when(registrationCenterMachineDeviceRepository
				.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
						.thenReturn(new ArrayList<RegistrationCenterMachineDevice>());

		when(registrationCenterMachineUserRepository
				.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
						.thenReturn(new ArrayList<RegistrationCenterUserMachine>());

		when(registrationCenterDeviceRepository.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(new ArrayList<RegistrationCenterDevice>());
		when(registrationCenterRepository.update(Mockito.any())).thenReturn(registrationCenter);
		mockMvc.perform(
				delete("/v1.0/registrationcenters/676").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteRegistrationCenterTestForRequestException() throws Exception {
		List<RegistrationCenter> registrationCenterlist  = new ArrayList<>();
		when(registrationCenterRepository.findByRegIdAndIsDeletedFalseOrNull(Mockito.any()))
		.thenReturn(registrationCenterlist);
	mockMvc.perform(delete("/v1.0/registrationcenters/12")).andExpect(status().isOk());
		
	}

	@Test
	public void deleteRegistrationCenterDataAccessExceptionTest() throws Exception {
		List<RegistrationCenter> registrationCenterlist = new ArrayList<>();
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setName("TEST CENTER");
		registrationCenter.setAddressLine1("Address Line 1");
		registrationCenter.setAddressLine2("Address Line 2");
		registrationCenter.setAddressLine3("Address Line 3");
		registrationCenter.setCenterTypeCode("REG01");
		registrationCenter.setContactPerson("Test");
		registrationCenter.setContactPhone("9999999999");
		registrationCenter.setHolidayLocationCode("HLC01");
		registrationCenter.setId("676");
		registrationCenter.setIsActive(true);
		registrationCenter.setLangCode("eng");
		registrationCenter.setLatitude("12.9646818");
		registrationCenter.setLocationCode("LOC01");
		registrationCenter.setLongitude("77.70168");
		registrationCenter.setTimeZone("UTC");
		registrationCenter.setWorkingHours("9");
		registrationCenterlist.add(registrationCenter);
		when(registrationCenterRepository.findByRegIdAndIsDeletedFalseOrNull(Mockito.any()))
		.thenReturn(registrationCenterlist);
when(registrationCenterMachineRepository.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
		.thenReturn(new ArrayList<RegistrationCenterMachine>());

when(registrationCenterMachineDeviceRepository
		.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(new ArrayList<RegistrationCenterMachineDevice>());

when(registrationCenterMachineUserRepository
		.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(new ArrayList<RegistrationCenterUserMachine>());

when(registrationCenterDeviceRepository.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
		.thenReturn(new ArrayList<RegistrationCenterDevice>());

		when(registrationCenterRepository.update(Mockito.any())).thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/registrationcenters/12").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void createRegistrationCenterTestInvalidLatLongFormatTest() throws Exception {
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		short numberOfKiosks = 1;
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto registrationCenterDto = new RegistrationCenterDto();
		registrationCenterDto.setName("TEST CENTER");
		registrationCenterDto.setAddressLine1("Address Line 1");
		registrationCenterDto.setAddressLine2("Address Line 2");
		registrationCenterDto.setAddressLine3("Address Line 3");
		registrationCenterDto.setCenterTypeCode("REG01");
		registrationCenterDto.setContactPerson("Test");
		registrationCenterDto.setContactPhone("9999999999");
		registrationCenterDto.setHolidayLocationCode("HLC01");
		registrationCenterDto.setId("676");
		registrationCenterDto.setIsActive(true);
		registrationCenterDto.setLangCode("eng");
		registrationCenterDto.setLatitude("INVALID");
		registrationCenterDto.setLocationCode("INVALID");
		registrationCenterDto.setLongitude("77.70168");
		registrationCenterDto.setNumberOfKiosks((short) 1);
		registrationCenterDto.setTimeZone("UTC");
		registrationCenterDto.setWorkingHours("9");
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setName("TEST CENTER");
		registrationCenter.setAddressLine1("Address Line 1");
		registrationCenter.setAddressLine2("Address Line 2");
		registrationCenter.setAddressLine3("Address Line 3");
		registrationCenter.setCenterTypeCode("REG01");
		registrationCenter.setContactPerson("Test");
		registrationCenter.setContactPhone("9999999999");
		registrationCenter.setHolidayLocationCode("HLC01");
		registrationCenter.setId("676");
		registrationCenter.setIsActive(true);
		registrationCenter.setLangCode("eng");
		registrationCenter.setLatitude("12.9646818");
		registrationCenter.setLocationCode("LOC01");
		registrationCenter.setLongitude("77.70168");
		registrationCenter.setNumberOfKiosks(numberOfKiosks);
		registrationCenter.setTimeZone("UTC");
		registrationCenter.setWorkingHours("9");
		requestDto.setRequest(registrationCenterDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterRepository.create(Mockito.any())).thenThrow(new RequestException("", ""));
		mockMvc.perform(post("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateRegistrationCenterRequestExceptionTest() throws Exception {
		RegistrationCenterHistory registrationCenterHistory = new RegistrationCenterHistory();
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		short numberOfKiosks = 1;
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto registrationCenterDto = new RegistrationCenterDto();
		registrationCenterDto.setName("TEST CENTER");
		registrationCenterDto.setAddressLine1("Address Line 1");
		registrationCenterDto.setAddressLine2("Address Line 2");
		registrationCenterDto.setAddressLine3("Address Line 3");
		registrationCenterDto.setCenterTypeCode("REG01");
		registrationCenterDto.setContactPerson("Test");
		registrationCenterDto.setContactPhone("9999999999");
		registrationCenterDto.setHolidayLocationCode("HLC01");
		registrationCenterDto.setId("676");
		registrationCenterDto.setIsActive(true);
		registrationCenterDto.setLangCode("eng");
		registrationCenterDto.setLatitude("INVALID");
		registrationCenterDto.setLocationCode("INVALID");
		registrationCenterDto.setLongitude("77.70168");
		registrationCenterDto.setNumberOfKiosks((short) 1);
		registrationCenterDto.setTimeZone("UTC");
		registrationCenterDto.setWorkingHours("9");
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setName("TEST CENTER");
		registrationCenter.setAddressLine1("Address Line 1");
		registrationCenter.setAddressLine2("Address Line 2");
		registrationCenter.setAddressLine3("Address Line 3");
		registrationCenter.setCenterTypeCode("REG01");
		registrationCenter.setContactPerson("Test");
		registrationCenter.setContactPhone("9999999999");
		registrationCenter.setHolidayLocationCode("HLC01");
		registrationCenter.setId("676");
		registrationCenter.setIsActive(true);
		registrationCenter.setLangCode("eng");
		registrationCenter.setLatitude("12.9646818");
		registrationCenter.setLocationCode("LOC01");
		registrationCenter.setLongitude("77.70168");
		registrationCenter.setNumberOfKiosks(numberOfKiosks);
		registrationCenter.setTimeZone("UTC");
		registrationCenter.setWorkingHours("9");
		requestDto.setRequest(registrationCenterDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterRepository.findByIdAndLangCode(Mockito.anyString(), Mockito.anyString())).thenReturn(registrationCenter);
		when(registrationCenterRepository.update(registrationCenter)).thenReturn(registrationCenter);
		when(repositoryCenterHistoryRepository.create(Mockito.any())).thenReturn(registrationCenterHistory);
		mockMvc.perform(put("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateRegistrationCenterDataAccessExceptionTest() throws Exception {
		RequestDto<RegistrationCenterDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterDto registrationCenterDto = new RegistrationCenterDto();
		registrationCenterDto.setName("TEST CENTER");
		registrationCenterDto.setAddressLine1("Address Line 1");
		registrationCenterDto.setAddressLine2("Address Line 2");
		registrationCenterDto.setAddressLine3("Address Line 3");
		registrationCenterDto.setCenterTypeCode("REG01");
		registrationCenterDto.setContactPerson("Test");
		registrationCenterDto.setContactPhone("9999999999");
		registrationCenterDto.setHolidayLocationCode("HLC01");
		registrationCenterDto.setId("676");
		registrationCenterDto.setIsActive(true);
		registrationCenterDto.setLangCode("eng");
		registrationCenterDto.setLatitude("12.9646818");
		registrationCenterDto.setLocationCode("LOC");
		registrationCenterDto.setLongitude("77.70168");
		registrationCenterDto.setNumberOfKiosks((short) 1);
		registrationCenterDto.setTimeZone("UTC");
		registrationCenterDto.setWorkingHours("9");
		requestDto.setRequest(registrationCenterDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterRepository.findByIdAndLangCode(Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(put("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}
	
	//-----------------------------------genderNameValidationTest-------------------//
	
	 @Test
		public void validateGenderNameInvalidTest() throws Exception {
			Mockito.when(genderTypeRepository.isGenderNamePresent(Mockito.anyString())).thenReturn(false);
			mockMvc.perform(get("/v1.0/gendertypes/validate/others")).andExpect(status().isOk()).andReturn();

		}

		@Test
		public void validateGenderNameValid() throws Exception {
			Mockito.when(genderTypeRepository.isGenderNamePresent(Mockito.anyString())).thenReturn(true);
			mockMvc.perform(get("/v1.0/gendertypes/validate/male")).andExpect(status().isOk()).andReturn();

		}

		@Test()
		public void validateGenderNameException() throws Exception {
			Mockito.when(genderTypeRepository.isGenderNamePresent(Mockito.anyString()))
					.thenThrow(DataRetrievalFailureException.class);
			mockMvc.perform(get("/v1.0/gendertypes/validate/male")).andExpect(status().is5xxServerError());

		}
		
		
		// ----------------------------------------------------location------------------------------//

		@Test
		public void validateLocationNameInvalidTest() throws Exception {
			Mockito.when(locationRepository.isLocationNamePresent(Mockito.anyString())).thenReturn(false);
			mockMvc.perform(get("/v1.0/locations/validate/Morocco")).andExpect(status().isOk()).andReturn();

		}

		@Test
		public void validateLocationNameValidTest() throws Exception {
			Mockito.when(locationRepository.isLocationNamePresent(Mockito.anyString())).thenReturn(true);
			mockMvc.perform(get("/v1.0/locations/validate/Morroco")).andExpect(status().isOk()).andReturn();

		}

		@Test()
		public void validateLocationNameExceptionTest() throws Exception {
			Mockito.when(locationRepository.isLocationNamePresent(Mockito.anyString()))
					.thenThrow(DataRetrievalFailureException.class);
			mockMvc.perform(get("/v1.0/locations/validate/Morroco")).andExpect(status().is5xxServerError());

		}

}