package io.mosip.kernel.syncdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.syncdata.entity.Application;
import io.mosip.kernel.syncdata.entity.BiometricAttribute;
import io.mosip.kernel.syncdata.entity.BiometricType;
import io.mosip.kernel.syncdata.entity.BlacklistedWords;
import io.mosip.kernel.syncdata.entity.Device;
import io.mosip.kernel.syncdata.entity.DeviceSpecification;
import io.mosip.kernel.syncdata.entity.DeviceType;
import io.mosip.kernel.syncdata.entity.DocumentCategory;
import io.mosip.kernel.syncdata.entity.DocumentType;
import io.mosip.kernel.syncdata.entity.Gender;
import io.mosip.kernel.syncdata.entity.Holiday;
import io.mosip.kernel.syncdata.entity.IdType;
import io.mosip.kernel.syncdata.entity.Language;
import io.mosip.kernel.syncdata.entity.Location;
import io.mosip.kernel.syncdata.entity.Machine;
import io.mosip.kernel.syncdata.entity.MachineSpecification;
import io.mosip.kernel.syncdata.entity.MachineType;
import io.mosip.kernel.syncdata.entity.ReasonCategory;
import io.mosip.kernel.syncdata.entity.ReasonList;
import io.mosip.kernel.syncdata.entity.RegistrationCenter;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.syncdata.entity.RegistrationCenterType;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUser;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.syncdata.entity.Template;
import io.mosip.kernel.syncdata.entity.TemplateFileFormat;
import io.mosip.kernel.syncdata.entity.TemplateType;
import io.mosip.kernel.syncdata.entity.Title;
import io.mosip.kernel.syncdata.entity.ValidDocument;
import io.mosip.kernel.syncdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.syncdata.entity.id.HolidayID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterDeviceID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineDeviceID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserID;
import io.mosip.kernel.syncdata.repository.ApplicationRepository;
import io.mosip.kernel.syncdata.repository.BiometricAttributeRepository;
import io.mosip.kernel.syncdata.repository.BiometricTypeRepository;
import io.mosip.kernel.syncdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.syncdata.repository.DeviceRepository;
import io.mosip.kernel.syncdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.syncdata.repository.DeviceTypeRepository;
import io.mosip.kernel.syncdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.syncdata.repository.DocumentTypeRepository;
import io.mosip.kernel.syncdata.repository.GenderRepository;
import io.mosip.kernel.syncdata.repository.HolidayRepository;
import io.mosip.kernel.syncdata.repository.IdTypeRepository;
import io.mosip.kernel.syncdata.repository.LanguageRepository;
import io.mosip.kernel.syncdata.repository.LocationRepository;
import io.mosip.kernel.syncdata.repository.MachineRepository;
import io.mosip.kernel.syncdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.syncdata.repository.MachineTypeRepository;
import io.mosip.kernel.syncdata.repository.ReasonCategoryRepository;
import io.mosip.kernel.syncdata.repository.ReasonListRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineDeviceRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserMachineRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.syncdata.repository.TemplateFileFormatRepository;
import io.mosip.kernel.syncdata.repository.TemplateRepository;
import io.mosip.kernel.syncdata.repository.TemplateTypeRepository;
import io.mosip.kernel.syncdata.repository.TitleRepository;
import io.mosip.kernel.syncdata.repository.ValidDocumentRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SyncDataIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private List<Application> applications;
	private List<Machine> machines;
	private List<MachineSpecification> machineSpecification;
	private List<MachineType> machineType;
	private List<RegistrationCenter> registrationCenters;
	private List<RegistrationCenterType> registrationCenterType;
	private List<Device> devices;
	private List<DeviceSpecification> deviceSpecification;
	private List<DeviceType> deviceType;
	private List<Holiday> holidays;
	private List<BlacklistedWords> blackListedWords;
	private List<Title> titles;
	private List<Gender> genders;
	private List<Language> languages;
	private List<Template> templates;
	private List<TemplateFileFormat> templateFileFormats;
	private List<TemplateType> templateTypes;
	private List<BiometricAttribute> biometricAttributes;
	private List<BiometricType> biometricTypes;
	private List<DocumentCategory> documentCategories;
	private List<DocumentType> documentTypes;
	private List<ValidDocument> validDocuments;
	private List<ReasonCategory> reasonCategories;
	private List<ReasonList> reasonLists;
	private List<IdType> idTypes;
	private List<Location> locations;
	private List<RegistrationCenterMachine> registrationCenterMachines;
	private List<RegistrationCenterDevice> registrationCenterDevices;
	private List<RegistrationCenterMachineDevice> registrationCenterMachineDevices;
	private List<RegistrationCenterUserMachine> registrationCenterUserMachines;
	private List<RegistrationCenterUser> registrationCenterUsers;

	@MockBean
	private ApplicationRepository applicationRepository;
	@MockBean
	private MachineRepository machineRepository;
	@MockBean
	private MachineTypeRepository machineTypeRepository;
	@MockBean
	private RegistrationCenterRepository registrationCenterRepository;
	@MockBean
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;
	@MockBean
	private TemplateRepository templateRepository;
	@MockBean
	private TemplateFileFormatRepository templateFileFormatRepository;
	@MockBean
	private ReasonCategoryRepository reasonCategoryRepository;
	@MockBean
	private HolidayRepository holidayRepository;
	@MockBean
	private BlacklistedWordsRepository blacklistedWordsRepository;
	@MockBean
	private BiometricTypeRepository biometricTypeRepository;
	@MockBean
	private BiometricAttributeRepository biometricAttributeRepository;
	@MockBean
	private TitleRepository titleRepository;
	@MockBean
	private LanguageRepository languageRepository;
	@MockBean
	private GenderRepository genderTypeRepository;
	@MockBean
	private DeviceRepository deviceRepository;
	@MockBean
	private DocumentCategoryRepository documentCategoryRepository;
	@MockBean
	private DocumentTypeRepository documentTypeRepository;
	@MockBean
	private IdTypeRepository idTypeRepository;
	@MockBean
	private DeviceSpecificationRepository deviceSpecificationRepository;
	@MockBean
	private LocationRepository locationRepository;
	@MockBean
	private TemplateTypeRepository templateTypeRepository;
	@MockBean
	private MachineSpecificationRepository machineSpecificationRepository;
	@MockBean
	private DeviceTypeRepository deviceTypeRepository;
	@MockBean
	private ValidDocumentRepository validDocumentRepository;
	@MockBean
	private ReasonListRepository reasonListRepository;
	@MockBean
	private RegistrationCenterMachineRepository registrationCenterMachineRepository;
	@MockBean
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;
	@MockBean
	private RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;
	@MockBean
	private RegistrationCenterUserMachineRepository registrationCenterUserMachineRepository;
	@MockBean
	private RegistrationCenterUserRepository registrationCenterUserRepository;

	// ###########################CONFIG START#########################
	@MockBean
	private RestTemplate restTemplate;
	private static final String JSON_CONFIG_RESPONSE = "{\r\n" + "\"registrationConfiguration\":\r\n"
			+ "							{\"keyValidityPeriodPreRegPack\":\"3\",\"smsNotificationTemplateRegCorrection\":\"OTP for your request is $otp\",\"defaultDOB\":\"1-Jan\",\"smsNotificationTemplateOtp\":\"OTP for your request is $otp\",\"supervisorVerificationRequiredForExceptions\":\"true\",\"keyValidityPeriodRegPack\":\"3\",\"irisRetryAttempts\":\"10\",\"fingerprintQualityThreshold\":\"120\",\"multifactorauthentication\":\"true\",\"smsNotificationTemplateUpdateUIN\":\"OTP for your request is $otp\",\"supervisorAuthType\":\"password\",\"maxDurationRegPermittedWithoutMasterdataSyncInDays\":\"10\",\"modeOfNotifyingIndividual\":\"mobile\",\"emailNotificationTemplateUpdateUIN\":\"Hello $user the OTP is $otp\",\"maxDocSizeInMB\":\"150\",\"emailNotificationTemplateOtp\":\"Hello $user the OTP is $otp\",\"emailNotificationTemplateRegCorrection\":\"Hello $user the OTP is $otp\",\"faceRetry\":\"12\",\"noOfFingerprintAuthToOnboardUser\":\"10\",\"smsNotificationTemplateLostUIN\":\"OTP for your request is $otp\",\"supervisorAuthMode\":\"IRIS\",\"operatorRegSubmissionMode\":\"fingerprint\",\"officerAuthType\":\"password\",\"faceQualityThreshold\":\"25\",\"gpsDistanceRadiusInMeters\":\"3\",\"automaticSyncFreqServerToClient\":\"25\",\"maxDurationWithoutMasterdataSyncInDays\":\"7\",\"loginMode\":\"bootable dongle\",\"irisQualityThreshold\":\"25\",\"retentionPeriodAudit\":\"3\",\"fingerprintRetryAttempts\":\"234\",\"emailNotificationTemplateNewReg\":\"Hello $user the OTP is $otp\",\"passwordExpiryDurationInDays\":\"3\",\"emailNotificationTemplateLostUIN\":\"Hello $user the OTP is $otp\",\"blockRegistrationIfNotSynced\":\"10\",\"noOfIrisAuthToOnboardUser\":\"10\",\"smsNotificationTemplateNewReg\":\"OTP for your request is $otp\"},\r\n"
			+ "\r\n" + "\"globalConfiguration\":\r\n"
			+ "						{\"mosip.kernel.crypto.symmetric-algorithm-name\":\"AES\",\"mosip.kernel.virus-scanner.port\":\"3310\",\"mosip.kernel.email.max-length\":\"50\",\"mosip.kernel.email.domain.ext-max-lenght\":\"7\",\"mosip.kernel.rid.sequence-length\":\"5\",\"mosip.kernel.uin.uin-generation-cron\":\"0 * * * * *\",\"mosip.kernel.rid.centerid-length\":\"5\",\"mosip.kernel.email.special-char\":\"!#$%&'*+-\\/=?^_`{|}~.\",\"mosip.kernel.rid.timestamp-length\":\"14\",\"mosip.kernel.vid.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.asymmetric-algorithm-length\":\"2048\",\"mosip.kernel.uin.min-unused-threshold\":\"100000\",\"mosip.kernel.prid.sequence-limit\":\"3\",\"auth.role.prefix\":\"ROLE_\",\"mosip.kernel.email.domain.ext-min-lenght\":\"2\",\"auth.server.validate.url\":\"http:\\/\\/localhost:8091\\/auth\\/validate_token\",\"mosip.kernel.machineid.length\":\"4\",\"mosip.supported-languages\":\"eng,ara,fra\",\"mosip.kernel.prid.length\":\"14\",\"auth.header.name\":\"Authorization\",\"mosip.kernel.crypto.asymmetric-algorithm-name\":\"RSA\",\"mosip.kernel.phone.min-length\":\"9\",\"mosip.kernel.uin.length\":\"10\",\"mosip.kernel.virus-scanner.host\":\"104.211.209.102\",\"mosip.kernel.email.min-length\":\"7\",\"mosip.kernel.rid.machineid-length\":\"5\",\"mosip.kernel.prid.repeating-block-limit\":\"3\",\"mosip.kernel.vid.length.repeating-block-limit\":\"2\",\"mosip.kernel.rid.length\":\"29\",\"mosip.kernel.phone.max-length\":\"15\",\"mosip.kernel.prid.repeating-limit\":\"2\",\"mosip.kernel.uin.restricted-numbers\":\"786,666\",\"mosip.kernel.email.domain.special-char\":\"-\",\"mosip.kernel.vid.length.repeating-limit\":\"2\",\"mosip.kernel.registrationcenterid.length\":\"4\",\"mosip.kernel.phone.special-char\":\"+ -\",\"mosip.kernel.uin.uins-to-generate\":\"200000\",\"mosip.kernel.vid.length\":\"16\",\"mosip.kernel.tokenid.length\":\"36\",\"mosip.kernel.uin.length.repeating-block-limit\":\"2\",\"mosip.kernel.tspid.length\":\"4\",\"mosip.kernel.tokenid.sequence-limit\":\"3\",\"mosip.kernel.uin.length.repeating-limit\":\"2\",\"mosip.kernel.uin.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.symmetric-algorithm-length\":\"256\",\"mosip.kernel.data-key-splitter\":\"#KEY_SPLITTER#\"}\r\n"
			+ "}";
	private static final String JSON_REGISTRATION_CONFIG_RESPONSE = "{\"keyValidityPeriodPreRegPack\":\"3\",\"smsNotificationTemplateRegCorrection\":\"OTP for your request is $otp\",\"defaultDOB\":\"1-Jan\",\"smsNotificationTemplateOtp\":\"OTP for your request is $otp\",\"supervisorVerificationRequiredForExceptions\":\"true\",\"keyValidityPeriodRegPack\":\"3\",\"irisRetryAttempts\":\"10\",\"fingerprintQualityThreshold\":\"120\",\"multifactorauthentication\":\"true\",\"smsNotificationTemplateUpdateUIN\":\"OTP for your request is $otp\",\"supervisorAuthType\":\"password\",\"maxDurationRegPermittedWithoutMasterdataSyncInDays\":\"10\",\"modeOfNotifyingIndividual\":\"mobile\",\"emailNotificationTemplateUpdateUIN\":\"Hello $user the OTP is $otp\",\"maxDocSizeInMB\":\"150\",\"emailNotificationTemplateOtp\":\"Hello $user the OTP is $otp\",\"emailNotificationTemplateRegCorrection\":\"Hello $user the OTP is $otp\",\"faceRetry\":\"12\",\"noOfFingerprintAuthToOnboardUser\":\"10\",\"smsNotificationTemplateLostUIN\":\"OTP for your request is $otp\",\"supervisorAuthMode\":\"IRIS\",\"operatorRegSubmissionMode\":\"fingerprint\",\"officerAuthType\":\"password\",\"faceQualityThreshold\":\"25\",\"gpsDistanceRadiusInMeters\":\"3\",\"automaticSyncFreqServerToClient\":\"25\",\"maxDurationWithoutMasterdataSyncInDays\":\"7\",\"loginMode\":\"bootable dongle\",\"irisQualityThreshold\":\"25\",\"retentionPeriodAudit\":\"3\",\"fingerprintRetryAttempts\":\"234\",\"emailNotificationTemplateNewReg\":\"Hello $user the OTP is $otp\",\"passwordExpiryDurationInDays\":\"3\",\"emailNotificationTemplateLostUIN\":\"Hello $user the OTP is $otp\",\"blockRegistrationIfNotSynced\":\"10\",\"noOfIrisAuthToOnboardUser\":\"10\",\"smsNotificationTemplateNewReg\":\"OTP for your request is $otp\"}";
	private static final String JSON_GLOBAL_CONFIG_RESPONSE = "{\"mosip.kernel.crypto.symmetric-algorithm-name\":\"AES\",\"mosip.kernel.virus-scanner.port\":\"3310\",\"mosip.kernel.email.max-length\":\"50\",\"mosip.kernel.email.domain.ext-max-lenght\":\"7\",\"mosip.kernel.rid.sequence-length\":\"5\",\"mosip.kernel.uin.uin-generation-cron\":\"0 * * * * *\",\"mosip.kernel.rid.centerid-length\":\"5\",\"mosip.kernel.email.special-char\":\"!#$%&'*+-\\/=?^_`{|}~.\",\"mosip.kernel.rid.timestamp-length\":\"14\",\"mosip.kernel.vid.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.asymmetric-algorithm-length\":\"2048\",\"mosip.kernel.uin.min-unused-threshold\":\"100000\",\"mosip.kernel.prid.sequence-limit\":\"3\",\"auth.role.prefix\":\"ROLE_\",\"mosip.kernel.email.domain.ext-min-lenght\":\"2\",\"auth.server.validate.url\":\"http:\\/\\/localhost:8091\\/auth\\/validate_token\",\"mosip.kernel.machineid.length\":\"4\",\"mosip.supported-languages\":\"eng,ara,fra\",\"mosip.kernel.prid.length\":\"14\",\"auth.header.name\":\"Authorization\",\"mosip.kernel.crypto.asymmetric-algorithm-name\":\"RSA\",\"mosip.kernel.phone.min-length\":\"9\",\"mosip.kernel.uin.length\":\"10\",\"mosip.kernel.virus-scanner.host\":\"104.211.209.102\",\"mosip.kernel.email.min-length\":\"7\",\"mosip.kernel.rid.machineid-length\":\"5\",\"mosip.kernel.prid.repeating-block-limit\":\"3\",\"mosip.kernel.vid.length.repeating-block-limit\":\"2\",\"mosip.kernel.rid.length\":\"29\",\"mosip.kernel.phone.max-length\":\"15\",\"mosip.kernel.prid.repeating-limit\":\"2\",\"mosip.kernel.uin.restricted-numbers\":\"786,666\",\"mosip.kernel.email.domain.special-char\":\"-\",\"mosip.kernel.vid.length.repeating-limit\":\"2\",\"mosip.kernel.registrationcenterid.length\":\"4\",\"mosip.kernel.phone.special-char\":\"+ -\",\"mosip.kernel.uin.uins-to-generate\":\"200000\",\"mosip.kernel.vid.length\":\"16\",\"mosip.kernel.tokenid.length\":\"36\",\"mosip.kernel.uin.length.repeating-block-limit\":\"2\",\"mosip.kernel.tspid.length\":\"4\",\"mosip.kernel.tokenid.sequence-limit\":\"3\",\"mosip.kernel.uin.length.repeating-limit\":\"2\",\"mosip.kernel.uin.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.symmetric-algorithm-length\":\"256\",\"mosip.kernel.data-key-splitter\":\"#KEY_SPLITTER#\"}";
	// ###########################CONFIG END#########################

	@Test
	public void testGetConfig() throws Exception {
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
				.thenReturn(JSON_REGISTRATION_CONFIG_RESPONSE);
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(JSON_GLOBAL_CONFIG_RESPONSE);
		mockMvc.perform(get("/v1.0/configs")).andExpect(status().isOk());
	}

	@Test
	public void testGlobalConfig() throws Exception {
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
				.thenReturn(JSON_REGISTRATION_CONFIG_RESPONSE);
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(JSON_GLOBAL_CONFIG_RESPONSE);
		mockMvc.perform(get("/v1.0/globalconfigs")).andExpect(status().isOk());
	}

	@Test
	public void testRegistrationConfig() throws Exception {
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
				.thenReturn(JSON_REGISTRATION_CONFIG_RESPONSE);
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
				.thenReturn(JSON_REGISTRATION_CONFIG_RESPONSE);
		mockMvc.perform(get("/v1.0/registrationcenterconfig/1")).andExpect(status().isOk());
	}

	@Before
	public void setup() {
		LocalDateTime localdateTime = LocalDateTime.parse("2018-11-01T01:01:01");
		LocalTime localTime = LocalTime.parse("09:00:00");
		applications = new ArrayList<>();
		applications.add(new Application("101", "ENG", "MOSIP", "MOSIP"));
		machines = new ArrayList<>();
		machines.add(new Machine("1001", "Laptop", "9876427", "172.12.01.128", "21:21:21:12", "1001", "ENG",
				localdateTime, null));
		machineSpecification = new ArrayList<>();
		machineSpecification.add(
				new MachineSpecification("1001", "Laptop", "Lenovo", "T480", "1001", "1.0", "Laptop", "ENG", null));
		machineType = new ArrayList<>();
		machineType.add(new MachineType("1001", "ENG", "System", "System"));
		devices = new ArrayList<>();
		devices.add(new Device("1011", "printer", "123", "127.0.0.122", "213:21:132:312", "1011", "ENG", localdateTime,
				true, "moisp", localdateTime, null, null, null, null));
		deviceSpecification = new ArrayList<>();
		deviceSpecification.add(new DeviceSpecification("1011", "SP-1011", "HP", "E1011", "T1011", "1.0", "HP-SP1011",
				"Hp Printer", null));
		deviceType = new ArrayList<>();
		deviceType.add(new DeviceType("T1011", "ENG", "device", "deviceDescriptiom"));
		registrationCenters = new ArrayList<>();
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setId("1011");
		registrationCenter.setAddressLine1("address-line1");
		registrationCenter.setAddressLine2("address-line2");
		registrationCenter.setAddressLine3("address-line3");
		registrationCenter.setCenterEndTime(localTime);
		registrationCenter.setCenterStartTime(localTime);
		registrationCenter.setCenterTypeCode("T1011");
		registrationCenter.setContactPerson("admin");
		registrationCenter.setContactPhone("9865123456");
		registrationCenter.setHolidayLocationCode("LOC01");
		registrationCenter.setIsActive(true);
		registrationCenter.setLanguageCode("ENG");
		registrationCenter.setWorkingHours("9");
		registrationCenter.setLunchEndTime(localTime);
		registrationCenter.setLunchStartTime(localTime);
		registrationCenters.add(registrationCenter);

		registrationCenterType = new ArrayList<>();
		registrationCenterType.add(new RegistrationCenterType("T1011", "ENG", "Main", "Main"));
		templates = new ArrayList<>();
		templates.add(new Template("T1", "ENG", "Email-Template", "Email-Template", "F101", "m", "text", "M101",
				"ModuleName", "T101"));
		templateFileFormats = new ArrayList<>();
		templateFileFormats.add(new TemplateFileFormat("T101", "ENG", "Email"));
		templateTypes = new ArrayList<>();
		templateTypes.add(new TemplateType("T101", "ENG", "Description"));
		holidays = new ArrayList<>();
		holidays.add(new Holiday(new HolidayID(1, "LOC01", LocalDate.parse("2019-01-01"), "ENG"), "New Year",
				"description"));
		blackListedWords = new ArrayList<>();
		blackListedWords.add(new BlacklistedWords("ABC", "ENG", "description"));
		titles = new ArrayList<>();
		titles.add(new Title(new CodeAndLanguageCodeID("1011", "ENG"), "title", "titleDescription"));
		genders = new ArrayList<>();
		genders.add(new Gender("G1011", "MALE", "description"));
		languages = new ArrayList<>();
		languages.add(new Language("ENG", "english", "family", "native name"));
		idTypes = new ArrayList<>();
		idTypes.add(new IdType("ID101", "ENG", "ID", "descr"));
		validDocuments = new ArrayList<>();
		validDocuments.add(new ValidDocument("D101", "DC101", null, null, "ENG"));
		biometricAttributes = new ArrayList<>();
		biometricAttributes.add(new BiometricAttribute("B101", "101", "Fingerprint", "description", "BT101", null));
		biometricTypes = new ArrayList<>();
		biometricTypes.add(new BiometricType("BT101", "ENG", "name", "description"));
		documentCategories = new ArrayList<>();
		documentCategories.add(new DocumentCategory("DC101", "ENG", "DC name", "description"));
		documentTypes = new ArrayList<>();
		documentTypes.add(new DocumentType("DT101", "ENG", "DT Type", "description"));
		reasonCategories = new ArrayList<>();
		reasonCategories.add(new ReasonCategory("RC101", "101", "R-1", "description", null));
		reasonLists = new ArrayList<>();
		reasonLists.add(new ReasonList("RL101", "RL1", "ENG", "RL", "description", null));
		locations = new ArrayList<>();
		locations.add(new Location("LOC01", "ENG", "Location", 1, "1", "1"));
		registrationCenterMachines = new ArrayList<>();
		registrationCenterMachines.add(new RegistrationCenterMachine(new RegistrationCenterMachineID("01010", "111")));
		registrationCenterDevices = new ArrayList<>();
		registrationCenterDevices.add(new RegistrationCenterDevice(new RegistrationCenterDeviceID("01010", "string")));
		registrationCenterMachineDevices = new ArrayList<>();
		registrationCenterMachineDevices.add(
				new RegistrationCenterMachineDevice(new RegistrationCenterMachineDeviceID("01010", "111", "1000")));
		registrationCenterUserMachines = new ArrayList<>();
		registrationCenterUserMachines
				.add(new RegistrationCenterUserMachine("01010", "qc001", "111", null, null, null));
		registrationCenterUsers = new ArrayList<>();
		registrationCenterUsers.add(new RegistrationCenterUser(new RegistrationCenterUserID("01010", "qc001")));
	}

	private void mockSuccess() {
		when(applicationRepository.findAll()).thenReturn(applications);
		when(applicationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(applications);
		when(machineRepository.findMachineById(Mockito.anyString())).thenReturn(machines);
		when(machineRepository.findAllLatestCreatedUpdateDeleted(Mockito.anyString(), Mockito.any()))
				.thenReturn(machines);
		when(machineSpecificationRepository.findByMachineId(Mockito.anyString())).thenReturn(machineSpecification);
		when(machineSpecificationRepository.findLatestByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(machineSpecification);
		when(machineTypeRepository.findAllByMachineId(Mockito.anyString())).thenReturn(machineType);
		when(machineTypeRepository.findLatestByMachineId(Mockito.anyString(), Mockito.any())).thenReturn(machineType);
		when(templateRepository.findAll()).thenReturn(templates);
		when(templateRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(templates);
		when(templateFileFormatRepository.findAllTemplateFormat()).thenReturn(templateFileFormats);
		when(templateFileFormatRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenReturn(templateFileFormats);
		when(templateTypeRepository.findAll()).thenReturn(templateTypes);
		when(templateTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(templateTypes);
		when(holidayRepository.findAllByMachineId(Mockito.anyString())).thenReturn(holidays);
		when(holidayRepository.findAllLatestCreatedUpdateDeletedByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(holidays);
		when(blacklistedWordsRepository.findAll()).thenReturn(blackListedWords);
		when(blacklistedWordsRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(blackListedWords);
		when(registrationCenterRepository.findRegistrationCenterByMachineId(Mockito.anyString()))
				.thenReturn(registrationCenters);
		when(registrationCenterRepository.findLatestRegistrationCenterByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(registrationCenters);
		when(registrationCenterTypeRepository.findRegistrationCenterTypeByMachineId(Mockito.anyString()))
				.thenReturn(registrationCenterType);
		when(registrationCenterTypeRepository.findLatestRegistrationCenterTypeByMachineId(Mockito.anyString(),
				Mockito.any())).thenReturn(registrationCenterType);
		when(genderTypeRepository.findAll()).thenReturn(genders);
		when(genderTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(genders);
		when(idTypeRepository.findAll()).thenReturn(idTypes);
		when(idTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(idTypes);
		when(deviceRepository.findDeviceByMachineId(Mockito.anyString())).thenReturn(devices);
		when(deviceRepository.findLatestDevicesByMachineId(Mockito.anyString(), Mockito.any())).thenReturn(devices);
		when(deviceSpecificationRepository.findDeviceTypeByMachineId(Mockito.anyString()))
				.thenReturn(deviceSpecification);
		when(deviceSpecificationRepository.findLatestDeviceTypeByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(deviceSpecification);
		when(deviceTypeRepository.findDeviceTypeByMachineId(Mockito.anyString())).thenReturn(deviceType);
		when(deviceTypeRepository.findLatestDeviceTypeByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(deviceType);
		when(languageRepository.findAll()).thenReturn(languages);
		when(languageRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(languages);
		when(reasonCategoryRepository.findAllReasons()).thenReturn(reasonCategories);
		when(reasonCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(reasonCategories);
		when(reasonListRepository.findAll()).thenReturn(reasonLists);
		when(reasonListRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(reasonLists);
		when(documentCategoryRepository.findAll()).thenReturn(documentCategories);
		when(documentCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenReturn(documentCategories);
		when(documentTypeRepository.findAll()).thenReturn(documentTypes).thenReturn(documentTypes);
		when(documentTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(documentTypes);
		when(validDocumentRepository.findAll()).thenReturn(validDocuments);
		when(validDocumentRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(validDocuments);
		when(biometricAttributeRepository.findAll()).thenReturn(biometricAttributes);
		when(biometricAttributeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenReturn(biometricAttributes);
		when(biometricTypeRepository.findAll()).thenReturn(biometricTypes);
		when(titleRepository.findAll()).thenReturn(titles);
		when(titleRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(titles);
		when(locationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(locations);
		when(locationRepository.findAll()).thenReturn(locations);
		when(registrationCenterMachineRepository.findAllByMachineId(Mockito.any()))
				.thenReturn(registrationCenterMachines);
		when(registrationCenterMachineRepository.findAllLatestByMachineIdCreatedUpdatedDeleted(Mockito.any(),
				Mockito.any())).thenReturn(registrationCenterMachines);
		when(registrationCenterDeviceRepository.findAllByRegistrationCenter(Mockito.any()))
				.thenReturn(registrationCenterDevices);
		when(registrationCenterDeviceRepository.findAllLatestByRegistrationCenterCreatedUpdatedDeleted(Mockito.any(),
				Mockito.any())).thenReturn(registrationCenterDevices);
		when(registrationCenterMachineDeviceRepository.findAllByRegistrationCenterId(Mockito.any()))
				.thenReturn(registrationCenterMachineDevices);
		when(registrationCenterMachineDeviceRepository.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.any(),
				Mockito.any())).thenReturn(registrationCenterMachineDevices);
		when(registrationCenterUserMachineRepository.findAllByRegistrationCenterId(Mockito.any()))
				.thenReturn(registrationCenterUserMachines);
		when(registrationCenterUserMachineRepository.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.any(),
				Mockito.any())).thenReturn(registrationCenterUserMachines);
		when(registrationCenterUserRepository.findAllByRegistrationCenterId(Mockito.any()))
				.thenReturn(registrationCenterUsers);
		when(registrationCenterUserRepository.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.any(),
				Mockito.any())).thenReturn(registrationCenterUsers);
	}

	@Test
	public void syncMasterDataSuccess() throws Exception {
		mockSuccess();
		mockMvc.perform(get("/v1.0/masterdata/{machineId}", "1001")).andExpect(status().isOk());
	}

	@Test
	public void syncMasterDataSuccessWithlastUpadtedTimestamp() throws Exception {
		mockSuccess();
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:01:01.021Z", "1001"))
				.andExpect(status().isOk());
	}

	@Test
	public void syncMasterDataInvalidTimeStampException() throws Exception {
		mockSuccess();
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:101:01.021Z", "1001"))
				.andExpect(status().isOk());
	}

	@Test
	public void syncMasterDataApplicationFetchException() throws Exception {
		mockSuccess();
		when(applicationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataMachineFetchException() throws Exception {
		mockSuccess();
		when(machineRepository.findAllLatestCreatedUpdateDeleted(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataMachineSpecFetchException() throws Exception {
		mockSuccess();
		when(machineSpecificationRepository.findLatestByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataMachineTypeFetchException() throws Exception {
		mockSuccess();
		when(machineTypeRepository.findLatestByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDeviceFetchException() throws Exception {
		mockSuccess();
		when(deviceRepository.findLatestDevicesByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDeviceSpecFetchException() throws Exception {
		mockSuccess();
		when(deviceSpecificationRepository.findLatestDeviceTypeByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDeviceTypeFetchException() throws Exception {
		mockSuccess();
		when(deviceTypeRepository.findLatestDeviceTypeByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataTemplateFetchException() throws Exception {
		mockSuccess();
		when(templateRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataTemplateFileFormatFetchException() throws Exception {
		mockSuccess();
		when(templateFileFormatRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataTemplateTypeFetchException() throws Exception {
		mockSuccess();
		when(templateTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataHolidayFetchException() throws Exception {
		mockSuccess();
		when(holidayRepository.findAllLatestCreatedUpdateDeletedByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataBiometricAttrFetchException() throws Exception {
		mockSuccess();
		when(biometricAttributeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataBiometricTypeFetchException() throws Exception {
		mockSuccess();
		when(biometricTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDocCategoryFetchException() throws Exception {
		mockSuccess();
		when(documentCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDocTypeFetchException() throws Exception {
		mockSuccess();
		when(documentTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataLanguageFetchException() throws Exception {
		mockSuccess();
		when(languageRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataGenderFetchException() throws Exception {
		mockSuccess();
		when(genderTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataLocationFetchException() throws Exception {
		mockSuccess();
		when(locationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataIdTypesFetchException() throws Exception {
		mockSuccess();
		when(idTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterRepository.findLatestRegistrationCenterByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterTypeFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterTypeRepository.findLatestRegistrationCenterTypeByMachineId(Mockito.anyString(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataValidFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterTypeRepository.findLatestRegistrationCenterTypeByMachineId(Mockito.anyString(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataBlackListedWordFetchException() throws Exception {
		mockSuccess();
		when(blacklistedWordsRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataReasonCatFetchException() throws Exception {
		mockSuccess();
		when(reasonCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataReasonListFetchException() throws Exception {
		mockSuccess();
		when(reasonListRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataTitleFetchException() throws Exception {
		mockSuccess();
		when(titleRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDatavalidDocumentFetchException() throws Exception {
		mockSuccess();
		when(validDocumentRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterMachineFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterMachineRepository.findAllLatestByMachineIdCreatedUpdatedDeleted(Mockito.anyString(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterDeviceFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterDeviceRepository
				.findAllLatestByRegistrationCenterCreatedUpdatedDeleted(Mockito.anyString(), Mockito.any()))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "111"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterMachineDeviceFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterMachineDeviceRepository
				.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.anyString(), Mockito.any()))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterUserMachineFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterUserMachineRepository
				.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.anyString(), Mockito.any()))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterUserFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterUserRepository.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.anyString(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/masterdata/{machineId}?lastUpdated=2018-11-01T12:10:01.021Z", "1001"))
				.andExpect(status().isInternalServerError());
	}

}
