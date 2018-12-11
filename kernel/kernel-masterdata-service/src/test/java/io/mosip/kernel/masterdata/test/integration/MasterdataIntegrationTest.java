package io.mosip.kernel.masterdata.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.IdTypeDto;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.IdTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterHistoryResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.BiometricAttribute;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.Gender;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.IdType;
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
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.TemplateType;
import io.mosip.kernel.masterdata.entity.Title;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.CodeLangCodeAndRsnCatCodeID;
import io.mosip.kernel.masterdata.entity.id.GenderID;
import io.mosip.kernel.masterdata.entity.id.HolidayID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterDeviceID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineDeviceID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
import io.mosip.kernel.masterdata.repository.BiometricAttributeRepository;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.repository.DocumentTypeRepository;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;
import io.mosip.kernel.masterdata.repository.LanguageRepository;
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
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserMachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.repository.TemplateTypeRepository;
import io.mosip.kernel.masterdata.repository.TitleRepository;
import io.mosip.kernel.masterdata.repository.ValidDocumentRepository;

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
 * @since 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MasterdataIntegrationTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private BlacklistedWordsRepository wordsRepository;

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

	@MockBean
	private TemplateRepository templateRepository;

	@MockBean
	private TemplateTypeRepository templateTypeRepository;

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
	ReasonCategoryRepository reasonRepository;

	@MockBean
	ReasonListRepository reasonListRepository;

	@MockBean
	RegistrationCenterTypeRepository registrationCenterTypeRepository;

	private List<ReasonCategory> reasoncategories;

	private List<ReasonList> reasonList;

	private CodeLangCodeAndRsnCatCodeID reasonListId;

	private static final String REASON_LIST_REQUEST = "{ \"request\":  { \"code\": \"RL1\", \"name\": \"reas_list\", \"description\": \"reason List\", \"rsnCatCode\": \"RC5\", \"langCode\": \"ENG\", \"isActive\": true }}";

	private static final String REASON_CATEGORY_REQUEST = "{ \"request\": { \"code\": \"RC9\", \"name\": \"reason_category\", \"description\": \"reason categroy\", \"langCode\": \"ENG\" ,\"isActive\": true } }";

	@MockBean
	RegistrationCenterHistoryRepository repository;

	RegistrationCenterHistory center;
	Device device;
	private DeviceDto deviceDto;

	List<RegistrationCenterHistory> centers = new ArrayList<>();

	@MockBean
	RegistrationCenterRepository registrationCenterRepository;

	RegistrationCenter registrationCenter;
	RegistrationCenter banglore;
	RegistrationCenter chennai;

	List<RegistrationCenter> registrationCenters = new ArrayList<>();

	@MockBean
	RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;

	RegistrationCenterUserMachineHistory registrationCenterUserMachineHistory;

	RegistrationCenterMachineUserID registrationCenterUserMachineHistoryId;

	List<RegistrationCenterUserMachineHistory> registrationCenterUserMachineHistories = new ArrayList<>();

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

	@Before
	public void setUp() {
		mapper = new ObjectMapper();
		blacklistedSetup();

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

		DeviceSpecsetUp();

		machineHistorySetUp();
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
		machineHistory.setLangCode("ENG");
		machineHistoryList.add(machineHistory);

	}

	List<DeviceSpecification> deviceSpecList;
	DeviceSpecification deviceSpecification;

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
		deviceSpecList.add(deviceSpecification);
	}

	private List<Machine> machineList;
	private Machine machine;

	private void machineSetUp() {
		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		machineList = new ArrayList<>();
		machine = new Machine();
		machine.setId("1000");
		machine.setName("HP");
		machine.setSerialNum("1234567890");
		machine.setMacAddress("100.100.100.80");
		machine.setLangCode("ENG");
		machine.setIsActive(true);
		machine.setValidityDateTime(specificDate);
		machineList.add(machine);
	}

	private void registrationCenterDeviceSetup() {
		registrationCenterDeviceDto = new RegistrationCenterDeviceDto();
		registrationCenterDeviceDto.setDeviceId("101");
		registrationCenterDeviceDto.setRegCenterId("1");
		registrationCenterDeviceDto.setIsActive(true);

		registrationCenterDevice = new RegistrationCenterDevice();
		RegistrationCenterDeviceID rcId = new RegistrationCenterDeviceID();
		rcId.setDeviceId(registrationCenterDeviceDto.getDeviceId());
		rcId.setRegCenterId(registrationCenterDeviceDto.getRegCenterId());
		registrationCenterDevice.setRegistrationCenterDevicePk(rcId);
		registrationCenterDevice.setIsActive(true);
		registrationCenterDevice.setCreatedBy("admin");
		registrationCenterDevice.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));

		registrationCenterDeviceHistory = new RegistrationCenterDeviceHistory();
		RegistrationCenterDeviceHistoryPk rcIdH = new RegistrationCenterDeviceHistoryPk();
		rcIdH.setDeviceId(rcId.getDeviceId());
		rcIdH.setRegCenterId(rcId.getRegCenterId());
		registrationCenterDeviceHistory.setRegistrationCenterDeviceHistoryPk(rcIdH);
		registrationCenterDeviceHistory.setCreatedDateTime(registrationCenterDevice.getCreatedDateTime());
		registrationCenterDeviceHistory.setCreatedBy("admin");
		registrationCenterDeviceHistory.setIsActive(true);
	}

	private void registrationCenterMachineSetup() {
		registrationCenterMachineDto = new RegistrationCenterMachineDto();
		registrationCenterMachineDto.setMachineId("1789");
		registrationCenterMachineDto.setRegCenterId("1");
		registrationCenterMachineDto.setIsActive(true);

		registrationCenterMachine = new RegistrationCenterMachine();
		RegistrationCenterMachineID rmId = new RegistrationCenterMachineID();
		rmId.setMachineId(registrationCenterMachineDto.getMachineId());
		rmId.setRegCenterId(registrationCenterMachineDto.getRegCenterId());
		registrationCenterMachine.setRegistrationCenterMachinePk(rmId);
		registrationCenterMachine.setIsActive(true);
		registrationCenterMachine.setCreatedBy("admin");
		registrationCenterMachine.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));

		registrationCenterMachineHistory = new RegistrationCenterMachineHistory();
		RegistrationCenterMachineID rmIdH = new RegistrationCenterMachineID();
		rmIdH.setMachineId(rmId.getMachineId());
		rmIdH.setRegCenterId(rmId.getRegCenterId());
		registrationCenterMachineHistory.setRegistrationCenterMachineHistoryPk(rmIdH);
		registrationCenterMachineHistory.setCreatedDateTime(registrationCenterMachine.getCreatedDateTime());
		registrationCenterMachineHistory.setCreatedBy("admin");
		registrationCenterMachineHistory.setIsActive(true);
	}

	private void registrationCenterMachineDeviceSetup() {
		registrationCenterMachineDeviceDto = new RegistrationCenterMachineDeviceDto();
		registrationCenterMachineDeviceDto.setMachineId("1789");
		registrationCenterMachineDeviceDto.setDeviceId("101");
		registrationCenterMachineDeviceDto.setRegCenterId("1");
		registrationCenterMachineDeviceDto.setIsActive(true);

		registrationCenterMachineDevice = new RegistrationCenterMachineDevice();
		RegistrationCenterMachineDeviceID rcmdId = new RegistrationCenterMachineDeviceID();
		rcmdId.setDeviceId("101");
		rcmdId.setMachineId("1789");
		rcmdId.setRegCenterId("1");
		registrationCenterMachineDevice.setRegistrationCenterMachineDevicePk(rcmdId);
		registrationCenterMachineDevice.setIsActive(true);
		registrationCenterMachineDevice.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		registrationCenterMachineDevice.setCreatedBy("admin");

		registrationCenterMachineDeviceHistory = new RegistrationCenterMachineDeviceHistory();
		RegistrationCenterMachineDeviceID rcmdIdH = new RegistrationCenterMachineDeviceID();
		rcmdIdH.setDeviceId("101");
		rcmdIdH.setMachineId("1789");
		rcmdIdH.setRegCenterId("1");
		registrationCenterMachineDeviceHistory.setRegistrationCenterMachineDeviceHistoryPk(rcmdIdH);
		registrationCenterMachineDeviceHistory.setCreatedDateTime(registrationCenterMachineDevice.getCreatedDateTime());
		registrationCenterMachineDeviceHistory.setIsActive(true);
		registrationCenterMachineDeviceHistory.setCreatedBy("admin");

	}

	List<Device> deviceList;
	List<Object[]> objectList;

	private void deviceSetup() {

		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		Timestamp validDateTime = Timestamp.valueOf(specificDate);
		deviceDto = new DeviceDto();
		deviceDto.setDeviceSpecId("123");
		deviceDto.setId("1");
		deviceDto.setIpAddress("asd");
		deviceDto.setIsActive(true);
		deviceDto.setLangCode("asd");
		deviceDto.setMacAddress("asd");
		deviceDto.setName("asd");
		deviceDto.setSerialNum("asd");

		deviceList = new ArrayList<>();
		device = new Device();
		device.setId("1000");
		device.setName("Printer");
		device.setLangCode("ENG");
		device.setIsActive(true);
		device.setMacAddress("127.0.0.0");
		device.setIpAddress("127.0.0.10");
		device.setSerialNum("234");
		device.setDeviceSpecId("234");
		device.setValidityDateTime(specificDate);
		deviceList.add(device);

		objectList = new ArrayList<>();
		Object objects[] = { "1001", "Laptop", "129.0.0.0", "123", "129.0.0.0", "1212", "ENG", true, validDateTime,
				"LaptopCode" };
		objectList.add(objects);
	}

	private void addValidDocumentSetUp() {
		validDocument = new ValidDocument();
		validDocument.setDocTypeCode("ttt");
		validDocument.setDocCategoryCode("ddd");
	}

	private void languageTestSetup() {
		// creating data coming from user

		languageDto = new LanguageDto();
		languageDto.setCode("ter");
		languageDto.setName("terman");
		languageDto.setIsActive(Boolean.TRUE);

		language = new Language();
		language.setCode("ter");
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
		Title title = new Title();
		titleId = new CodeAndLanguageCodeID();
		titleId.setLangCode("ENG");
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
		registrationCenterUserMachineHistoryId = new RegistrationCenterMachineUserID("1", "1", "1");
		registrationCenterUserMachineHistory = new RegistrationCenterUserMachineHistory();
		registrationCenterUserMachineHistory.setId(registrationCenterUserMachineHistoryId);
		registrationCenterUserMachineHistory.setEffectivetimes(LocalDateTime.now().minusDays(1));
	}

	private void registrationCenterSetup() {
		registrationCenter = new RegistrationCenter();
		registrationCenter.setId("1");
		registrationCenter.setName("bangalore");
		registrationCenter.setLatitude("12.9180722");
		registrationCenter.setLongitude("77.5028792");
		registrationCenter.setLanguageCode("ENG");
		registrationCenters.add(registrationCenter);

		Location location = new Location();
		location.setCode("BLR");

		banglore = new RegistrationCenter();
		banglore.setId("1");
		banglore.setName("bangalore");
		banglore.setLatitude("12.9180722");
		banglore.setLongitude("77.5028792");
		banglore.setLanguageCode("ENG");
		banglore.setLocationCode("LOC");
		chennai = new RegistrationCenter();
		chennai.setId("2");
		chennai.setName("Bangalore Central");
		chennai.setLanguageCode("ENG");
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
		center.setLanguageCode("ENG");
		center.setLocationCode("BLR");
		centers.add(center);
	}

	private void packetRejectionSetup() {
		ReasonCategory reasonCategory = new ReasonCategory();
		ReasonList reasonListObj = new ReasonList();
		reasonList = new ArrayList<>();
		reasonListObj.setCode("RL1");
		reasonListObj.setLangCode("ENG");
		reasonListObj.setRsnCatCode("RC1");
		reasonListObj.setDescription("reasonList");
		reasonList.add(reasonListObj);
		reasonCategory.setReasonList(reasonList);
		reasonCategory.setCode("RC1");
		reasonCategory.setLangCode("ENG");
		reasoncategories = new ArrayList<>();
		reasoncategories.add(reasonCategory);
		titleId = new CodeAndLanguageCodeID();
		titleId.setCode("RC1");
		titleId.setLangCode("ENG");
		reasonListId = new CodeLangCodeAndRsnCatCodeID();
		reasonListId.setCode("RL1");
		reasonListId.setLangCode("ENG");
		reasonListId.setRsnCatCode("RC1");
	}

	private void idTypeSetup() {
		idType = new IdType();
		idType.setIsActive(true);
		idType.setCreatedBy("testCreation");
		idType.setLangCode("ENG");
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
		holiday.setHolidayId(new HolidayID("KAR", date, "ENG"));
		holiday.setId(1);
		holiday.setHolidayName("Diwali");
		holiday.setCreatedBy("John");
		holiday.setCreatedDateTime(specificDate);
		holiday.setHolidayDesc("Diwali");
		holiday.setIsActive(true);

		Holiday holiday2 = new Holiday();
		holiday2.setHolidayId(new HolidayID("KAH", date, "ENG"));
		holiday2.setId(1);
		holiday2.setHolidayName("Durga Puja");
		holiday2.setCreatedBy("John");
		holiday2.setCreatedDateTime(specificDate);
		holiday2.setHolidayDesc("Diwali");
		holiday2.setIsActive(true);

		holidays.add(holiday);
		holidays.add(holiday2);
	}

	private void genderTypeSetup() {

		genderDto = new GenderTypeDto();
		genderDto.setCode("1");
		genderDto.setGenderName("abc");
		genderDto.setIsActive(true);
		genderDto.setLangCode("ENG");

		genderTypes = new ArrayList<>();
		genderTypesNull = new ArrayList<>();
		genderType = new Gender();
		genderId = new GenderID();
		genderId.setGenderCode("123");
		genderId.setGenderName("Raj");
		genderType.setIsActive(true);
		genderType.setCreatedBy("John");
		genderType.setCreatedDateTime(null);
		genderType.setIsDeleted(true);
		genderType.setDeletedDateTime(null);
		genderType.setLangCode("ENG");
		genderType.setUpdatedBy("Dom");
		genderType.setUpdatedDateTime(null);
		genderTypes.add(genderType);
	}

	private void blacklistedSetup() {
		words = new ArrayList<>();

		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setWord("abc");
		blacklistedWords.setLangCode("ENG");
		blacklistedWords.setDescription("no description available");

		words.add(blacklistedWords);
		blacklistedWords.setLangCode("TST");
		blacklistedWords.setIsActive(true);
		blacklistedWords.setWord("testword");
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
				.andExpect(status().isCreated());
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
				.andExpect(status().isBadRequest());

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
				.andExpect(status().isCreated());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isCreated());
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
				.andExpect(status().isBadRequest());
	}

	// -----------------------------LanguageImplementationTest----------------------------------
	@Test
	public void saveLanguagesTest() throws Exception {
		RequestDto<LanguageDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.language.create");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(languageDto);
		String content = mapper.writeValueAsString(requestDto);
		when(languageRepository.create(Mockito.any())).thenReturn(language);
		mockMvc.perform(post("/v1.0/languages").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isCreated());

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
				.andExpect(status().isBadRequest());

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
		mockMvc.perform(get("/v1.0/blacklistedwords/{langcode}", "ENG")).andExpect(status().isNotFound());
	}

	@Test
	public void getAllWordsBylangCodeEmptyArrayResponseTest() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenReturn(new ArrayList<>());
		mockMvc.perform(get("/v1.0/blacklistedwords/{langcode}", "ENG")).andExpect(status().isNotFound());
	}

	@Test
	public void getAllWordsBylangCodeFetchExceptionTest() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/blacklistedwords/{langcode}", "ENG")).andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllWordsBylangCodeNullArgExceptionTest() throws Exception {
		mockMvc.perform(get("/v1.0/blacklistedwords/{langcode}", " ")).andExpect(status().isNotFound());
	}

	// -----------------------------GenderTypeTest----------------------------------
	@Test
	public void getGenderByLanguageCodeFetchExceptionTest() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull("ENG"))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/gendertype/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getGenderByLanguageCodeNotFoundExceptionTest() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull("ENG"))
				.thenReturn(genderTypesNull);

		mockMvc.perform(get("/v1.0/gendertype/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void getAllGenderFetchExceptionTest() throws Exception {

		Mockito.when(genderTypeRepository.findAll(Gender.class)).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/gendertype").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getAllGenderNotFoundExceptionTest() throws Exception {

		Mockito.when(genderTypeRepository.findAll(Gender.class)).thenReturn(genderTypesNull);

		mockMvc.perform(get("/v1.0/gendertype").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void getGenderByLanguageCodeTest() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(genderTypes);
		mockMvc.perform(get("/v1.0/gendertype/{languageCode}", "ENG")).andExpect(status().isOk());

	}

	@Test
	public void getAllGendersTest() throws Exception {
		Mockito.when(genderTypeRepository.findAll(Gender.class)).thenReturn(genderTypes);
		mockMvc.perform(get("/v1.0/gendertype")).andExpect(status().isOk());

	}

	// -----------------------------HolidayTest----------------------------------

	@Test
	public void getHolidayAllHolidaysSuccessTest() throws Exception {
		when(holidayRepository.findAll(Holiday.class)).thenReturn(holidays);
		mockMvc.perform(get("/v1.0/holidays")).andExpect(status().isOk());
	}

	@Test
	public void getAllHolidaNoHolidayFoundTest() throws Exception {
		mockMvc.perform(get("/v1.0/holidays")).andExpect(status().isNotFound());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllHolidaysHolidayFetchExceptionTest() throws Exception {
		when(holidayRepository.findAll(Mockito.any(Class.class))).thenThrow(DataRetrievalFailureException.class);
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
		mockMvc.perform(get("/v1.0/holidays/{holidayId}", 1)).andExpect(status().isNotFound());
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
		mockMvc.perform(get("/v1.0/holidays/{holidayId}/{languagecode}", 1, "ENG")).andExpect(status().isNotFound());
	}

	@Test
	public void addHolidayTypeTest() throws Exception {
		String json = "{ \"id\": \"string\", \"request\": { \"holidayDate\": \"2019-01-01\", \"holidayDay\": \"Sunday\", \"holidayDesc\": \"New Year\", \"holidayMonth\": \"January\", \"holidayName\": \"New Year\", \"holidayYear\": \"2019\", \"id\": 1, \"isActive\": true, \"langCode\": \"ENG\", \"locationCode\": \"BLR\" }, \"timestamp\": \"2018-12-06T08:49:32.190Z\", \"ver\": \"string\"}";
		when(holidayRepository.create(Mockito.any())).thenReturn(holiday);
		mockMvc.perform(post("/v1.0/holidays").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	public void addHolidayTypeExceptionTest() throws Exception {

		String json = "{ \"id\": \"string\", \"request\": { \"holidayDate\": \"2019-01-01\", \"holidayDay\": \"Sunday\", \"holidayDesc\": \"New Year\", \"holidayMonth\": \"January\", \"holidayName\": \"New Year\", \"holidayYear\": \"2019\", \"id\": 1, \"isActive\": true, \"langCode\": \"ENG\", \"locationCode\": \"BLR\" }, \"timestamp\": \"2018-12-06T08:49:32.190Z\", \"ver\": \"string\"}";
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
		mockMvc.perform(get("/v1.0/idtypes/HIN").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
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
		Mockito.when(reasonRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull())
				.thenReturn(reasoncategories);
		mockMvc.perform(get("/v1.0/packetrejectionreasons")).andExpect(status().isOk());
	}

	@Test
	public void getAllRejectionReasonByCodeAndLangCodeTest() throws Exception {
		Mockito.when(
				reasonRepository.findReasonCategoryByCodeAndLangCode(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(reasoncategories);
		mockMvc.perform(get("/v1.0/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isOk());
	}

	@Test
	public void getAllRjectionReasonFetchExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull())
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/packetrejectionreasons")).andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllRejectionReasonByCodeAndLangCodeFetchExceptionTest() throws Exception {
		Mockito.when(
				reasonRepository.findReasonCategoryByCodeAndLangCode(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllRjectionReasonRecordsNotFoundTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(null);
		mockMvc.perform(get("/v1.0/packetrejectionreasons")).andExpect(status().isNotFound());
	}

	@Test
	public void getRjectionReasonByCodeAndLangCodeRecordsNotFoundExceptionTest() throws Exception {
		Mockito.when(
				reasonRepository.findReasonCategoryByCodeAndLangCode(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(null);
		mockMvc.perform(get("/v1.0/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getRjectionReasonByCodeAndLangCodeRecordsEmptyExceptionTest() throws Exception {
		Mockito.when(
				reasonRepository.findReasonCategoryByCodeAndLangCode(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(new ArrayList<ReasonCategory>());
		mockMvc.perform(get("/v1.0/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getAllRjectionReasonRecordsEmptyExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull())
				.thenReturn(new ArrayList<ReasonCategory>());
		mockMvc.perform(get("/v1.0/packetrejectionreasons")).andExpect(status().isNotFound());
	}

	@Test
	public void createReasonCateogryTest() throws Exception {
		Mockito.when(reasonRepository.create(Mockito.any())).thenReturn(reasoncategories.get(0));
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON)
				.content(REASON_CATEGORY_REQUEST.getBytes())).andExpect(status().isCreated());
	}

	@Test
	public void createReasonListTest() throws Exception {
		Mockito.when(reasonListRepository.create(Mockito.any())).thenReturn(reasonList.get(0));
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasonlist").contentType(MediaType.APPLICATION_JSON)
				.content(REASON_LIST_REQUEST.getBytes())).andExpect(status().isCreated());
	}

	@Test
	public void createReasonCateogryFetchExceptionTest() throws Exception {
		Mockito.when(reasonRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON)
				.content(REASON_CATEGORY_REQUEST.getBytes())).andExpect(status().isInternalServerError());
	}

	@Test
	public void createReasonListFetchExceptionTest() throws Exception {
		Mockito.when(reasonListRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(post("/v1.0/packetrejectionreasons/reasonlist").contentType(MediaType.APPLICATION_JSON)
				.content(REASON_LIST_REQUEST.getBytes())).andExpect(status().isInternalServerError());
	}

	// -----------------------------RegistrationCenterTest----------------------------------

	@Test
	public void getSpecificRegistrationCenterByIdTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsDeletedFalse("1", "ENG",
				LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenReturn(centers);

		MvcResult result = mockMvc.perform(get("/v1.0/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		RegistrationCenterHistoryResponseDto returnResponse = mapper
				.readValue(result.getResponse().getContentAsString(), RegistrationCenterHistoryResponseDto.class);

		assertThat(returnResponse.getRegistrationCentersHistory().get(0).getId(), is("1"));
	}

	@Test
	public void getRegistrationCentersHistoryNotFoundExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsDeletedFalse("1", "ENG",
				LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenReturn(null);
		mockMvc.perform(get("/v1.0/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getRegistrationCentersHistoryEmptyExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsDeletedFalse("1", "ENG",
				LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenReturn(new ArrayList<RegistrationCenterHistory>());
		mockMvc.perform(get("/v1.0/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getRegistrationCentersHistoryFetchExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsDeletedFalse("1", "ENG",
				LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/v1.0/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError()).andReturn();
	}

	@Test
	public void getRegistrationCenterByHierarchylevelAndTextAndLanguageCodeTest() throws Exception {
		centers.add(center);
		when(registrationCenterRepository.findRegistrationCenterHierarchyLevelName("COUNTRY", "INDIA", "ENG"))
				.thenReturn(registrationCenters);
		MvcResult result = mockMvc
				.perform(get("/v1.0/registrationcenters/COUNTRY/INDIA/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(2).getName(), is("Bangalore Central"));
	}

	@Test
	public void getSpecificRegistrationCenterHierarchyLevelFetchExceptionTest() throws Exception {

		when(registrationCenterRepository.findRegistrationCenterHierarchyLevelName("ENG", "CITY", "BANGALORE"))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/registrationcenters/ENG/CITY/BANGALORE").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getRegistrationCenterHierarchyLevelNotFoundExceptionTest() throws Exception {

		List<RegistrationCenter> emptyList = new ArrayList<>();
		when(registrationCenterRepository.findRegistrationCenterHierarchyLevelName("ENG", "CITY", "BANGALORE"))
				.thenReturn(emptyList);

		mockMvc.perform(get("/v1.0/registrationcenters/ENG/CITY/BANGALORE").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	// -----------------------------RegistrationCenterIntegrationTest----------------------------------

	@Test
	public void getSpecificRegistrationCenterByIdAndLangCodeNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findByIdAndLanguageCode("1", "ENG")).thenReturn(null);

		mockMvc.perform(get("/v1.0/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void getSpecificRegistrationCenterByIdAndLangCodeFetchExceptionTest() throws Exception {

		when(registrationCenterRepository.findByIdAndLanguageCode("1", "ENG"))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getCoordinateSpecificRegistrationCentersRegistrationCenterNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG"))
				.thenReturn(new ArrayList<>());
		mockMvc.perform(get("/v1.0/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();
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
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();
	}

	@Test
	public void getSpecificRegistrationCenterByLocationCodeAndLangCodeNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findByLocationCodeAndLanguageCode("ENG", "BLR")).thenReturn(null);

		mockMvc.perform(get("/v1.0/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void getSpecificRegistrationCenterByLocationCodeAndLangCodeFetchExceptionTest() throws Exception {

		when(registrationCenterRepository.findByLocationCodeAndLanguageCode("BLR", "ENG"))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/v1.0/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getAllRegistrationCentersNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findAll(RegistrationCenter.class))
				.thenReturn(new ArrayList<RegistrationCenter>());

		mockMvc.perform(get("/v1.0/registrationcenters").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

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
		when(registrationCenterRepository.findByIdAndLanguageCode("1", "ENG")).thenReturn(banglore);

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
		when(registrationCenterRepository.findByLocationCodeAndLanguageCode("BLR", "ENG"))
				.thenReturn(registrationCenters);
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
		when(registrationCenterRepository.findByLocationCodeAndLanguageCode("BLR", "ENG"))
				.thenReturn(registrationCenters);
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
		when(registrationCenterUserMachineHistoryRepository.findByIdAndEffectivetimesLessThanEqualAndIsDeletedFalse(
				registrationCenterUserMachineHistoryId, LocalDateTime.parse("2018-10-30T19:20:30.45")))
						.thenReturn(registrationCenterUserMachineHistories);
		mockMvc.perform(get("/v1.0/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getRegistrationCentersMachineUserMappingFetchExceptionTest() throws Exception {
		when(registrationCenterUserMachineHistoryRepository.findByIdAndEffectivetimesLessThanEqualAndIsDeletedFalse(
				registrationCenterUserMachineHistoryId, LocalDateTime.parse("2018-10-30T19:20:30.45")))
						.thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/v1.0/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError()).andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersDateTimeParseExceptionTest() throws Exception {
		mockMvc.perform(get("/v1.0/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45+5:30/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();
	}

	// @Test
	public void getRegistrationCentersMachineUserMappingTest() throws Exception {
		registrationCenterUserMachineHistories.add(registrationCenterUserMachineHistory);
		when(registrationCenterUserMachineHistoryRepository.findByIdAndEffectivetimesLessThanEqualAndIsDeletedFalse(
				registrationCenterUserMachineHistoryId, LocalDateTime.parse("2018-10-30T19:20:30.45")))
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

	// -----------------------------TitleIntegrationTest----------------------------------
	@Test
	public void getTitleByLanguageCodeNotFoundExceptionTest() throws Exception {

		titlesNull = new ArrayList<>();

		Mockito.when(titleRepository.getThroughLanguageCode("ENG")).thenReturn(titlesNull);

		mockMvc.perform(get("/v1.0/title/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

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

		mockMvc.perform(get("/v1.0/title").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

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
	// ----------------------------------------document-category----------------------------------------

	@Test
	public void addDocumentCategoryTest() throws Exception {
		String json = "{\"id\":\"mosip.documentcategories.create\",\"ver\":\"1.0\",\"timestamp\":\"\",\"request\":{\"name\":\"POI\",\"langCode\":\"ENG\",\"code\":\"D001\",\"description\":\"Proof Of Identity\",\"isActive\":\"true\"}}";
		when(documentCategoryRepository.create(Mockito.any())).thenReturn(category);
		mockMvc.perform(post("/v1.0/documentcategories").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	public void addDocumentCategoryDatabaseConnectionExceptionTest() throws Exception {
		String json = "{\"id\":\"mosip.documentcategories.create\",\"ver\":\"1.0\",\"timestamp\":\"\",\"request\":{\"name\":\"POI\",\"langCode\":\"ENG\",\"code\":\"D001\",\"description\":\"Proof Of Identity\",\"isActive\":\"true\"}}";
		when(documentCategoryRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(post("/v1.0/documentcategories").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());
	}

	// -----------------------------------document-type----------------------------------------
	@Test
	public void addDocumentTypeListTest() throws Exception {
		String json = "{\"id\":\"mosip.documentcategories.create\",\"ver\":\"1.0\",\"timestamp\":\"\",\"request\":{\"name\":\"POI\",\"langCode\":\"ENG\",\"code\":\"D001\",\"description\":\"Proof Of Identity\",\"isActive\":\"true\"}}";
		when(documentTypeRepository.create(Mockito.any())).thenReturn(type);
		mockMvc.perform(post("/v1.0/documenttypes").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	public void addDocumentTypesDatabaseConnectionExceptionTest() throws Exception {
		String json = "{\"id\":\"mosip.documentcategories.create\",\"ver\":\"1.0\",\"timestamp\":\"\",\"request\":{\"name\":\"POI\",\"langCode\":\"ENG\",\"code\":\"D001\",\"description\":\"Proof Of Identity\",\"isActive\":\"true\"}}";
		when(documentTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(post("/v1.0/documenttypes").contentType(MediaType.APPLICATION_JSON).content(json))
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
		mockMvc.perform(post("/v1.0/gendertype").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isCreated());
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
		mockMvc.perform(post("/v1.0/gendertype").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());

	}
	// ------------------------------------------valid-document-------------------------------------------

	@Test
	public void insertValidDocumentTest() throws Exception {
		String json = "{\"id\":\"mosip.documentcategories.create\",\"ver\":\"1.0\",\"timestamp\":\"\",\"request\":{\"docTypeCode\":\"ttt\",\"docCategoryCode\":\"ddd\",\"langCode\":\"ENG\",\"isActive\":\"true\"}}";
		when(validDocumentRepository.create(Mockito.any())).thenReturn(validDocument);
		mockMvc.perform(post("/v1.0/validdocuments").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	public void insertValidDocumentExceptionTest() throws Exception {
		String json = "{\"id\":\"mosip.documentcategories.create\",\"ver\":\"1.0\",\"timestamp\":\"\",\"request\":{\"docTypeCode\":\"ttt\",\"docCategoryCode\":\"ddd\",\"langCode\":\"ENG\",\"isActive\":\"true\"}}";
		when(validDocumentRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(post("/v1.0/validdocuments").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());
	}

	// ----------------------------------BiometricAttributeCreateApiTest--------------------------------------------------
	@Test
	public void createBiometricAttributeTest() throws Exception {
		BiometricAttribute biometricAttribute = new BiometricAttribute();
		biometricAttribute.setCode("BA222");
		biometricAttribute.setLangCode("ENG");
		String json = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"\", \"request\": { \"code\": \"BA222\", \"name\": \"sample data\", \"description\": \"sample data desc\", \"biometricTypeCode\": \"4\", \"langCode\": \"ENG\", \"isActive\": true }}";
		Mockito.when(biometricAttributeRepository.create(Mockito.any())).thenReturn(biometricAttribute);
		mockMvc.perform(post("/v1.0/biometricattributes").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	public void createBiometricAttributeExceptionTest() throws Exception {
		String json = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"\", \"request\": { \"code\": \"BA222\", \"name\": \"sample data\", \"description\": \"sample data desc\", \"biometricTypeCode\": \"4\", \"langCode\": \"ENG\", \"isActive\": true }}";
		when(biometricAttributeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(post("/v1.0/biometricattributes").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());
	}

	// ----------------------------------TemplateCreateApiTest--------------------------------------------------
	@Test
	public void createTemplateTest() throws Exception {
		Template template = new Template();
		template.setId("T222");
		String json = "{ \"id\": \"string\",  \"ver\": \"string\",  \"timestamp\": \"\",  \"request\": {    \"id\": \"T222\",    \"name\": \"Email template\",    \"description\": null,    \"fileFormatCode\": \"xml\",    \"model\": null,    \"fileText\": null,    \"moduleId\": \"preregistation\",    \"moduleName\": null,    \"templateTypeCode\": \"EMAIL\",    \"langCode\": \"ENG\",    \"isActive\": true  }}";
		Mockito.when(templateRepository.create(Mockito.any())).thenReturn(template);
		mockMvc.perform(post("/v1.0/templates").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	public void createTemplateExceptionTest() throws Exception {
		String json = "{ \"id\": \"string\",  \"ver\": \"string\",  \"timestamp\": \"\",  \"request\": {    \"id\": \"TT22\",    \"name\": \"Email template\",    \"description\": null,    \"fileFormatCode\": \"xml\",    \"model\": null,    \"fileText\": null,    \"moduleId\": \"preregistation\",    \"moduleName\": null,    \"templateTypeCode\": \"EMAIL\",    \"langCode\": \"ENG\",    \"isActive\": true  }}";
		when(templateRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(post("/v1.0/templates").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());
	}

	// ----------------------------------TemplateTypeCreateApiTest--------------------------------------------------
	@Test
	public void createTemplateTypeTest() throws Exception {
		TemplateType templateType = new TemplateType();
		templateType.setCode("TTC222");
		templateType.setLangCode("ENG");
		String json = "{ \"id\": \"string\",  \"ver\": \"string\",  \"timestamp\": \"\",  \"request\": {    \"code\": \"TTC222\",    \"description\": \"Template type desc\",    \"isActive\": true,    \"langCode\": \"ENG\"  }}";
		Mockito.when(templateTypeRepository.create(Mockito.any())).thenReturn(templateType);
		mockMvc.perform(post("/v1.0/templatetypes").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	public void createTemplatetypeExceptionTest() throws Exception {
		String json = "{ \"id\": \"string\",  \"ver\": \"string\",  \"timestamp\": \"\",  \"request\": {    \"code\": \"TTC222\",    \"description\": \"Template type desc\",    \"isActive\": true,    \"langCode\": \"ENG\"  }}";
		when(templateTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(post("/v1.0/templatetypes").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());
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
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}", "ENG")).andExpect(status().isNotFound());
	}

	@Test
	public void findDeviceSpecLangcodeFetchExceptionTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}", "ENG"))
				.andExpect(status().isInternalServerError());
	}

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
				.andExpect(status().isNotFound());
	}

	@Test
	public void findDeviceSpecByLangCodeAndDevTypeCodeFetchExceptionTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(
				Mockito.anyString(), Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}/{devicetypecode}", "ENG", "laptop"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void createDeviceSpecificationTest() throws Exception {
		DeviceSpecification deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("1000");
		deviceSpecification.setLangCode("ENG");

		String json = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T07:01:16.196Z\", \"request\": { \"brand\": \"HP\", \"description\": \"dercs\", \"deviceTypeCode\": \"6655\", \"id\": \"666555\", \"isActive\": true, \"langCode\": \"ENG\", \"minDriverversion\": \"min driver\", \"model\": \"Model\", \"name\": \"HP\" } }";

		Mockito.when(deviceSpecificationRepository.create(Mockito.any())).thenReturn(deviceSpecification);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isCreated());
	}

	@Test
	public void createDeviceSpecificationExceptionTest() throws Exception {
		String json = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T07:01:16.196Z\", \"request\": { \"brand\": \"HP\", \"description\": \"dercs\", \"deviceTypeCode\": \"6655\", \"id\": \"666555\", \"isActive\": true, \"langCode\": \"ENG\", \"minDriverversion\": \"min driver\", \"model\": \"Model\", \"name\": \"HP\" } }";

		Mockito.when(deviceSpecificationRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isInternalServerError());
	}

	// ---------------------------------DeviceTypeTest------------------------------------------------

	@Test
	public void createDeviceTypeTest() throws Exception {
		DeviceType deviceType = new DeviceType();
		deviceType.setCode("1000");
		deviceType.setLangCode("ENG");
		String deviceTypeJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T06:58:37.498Z\", \"request\": { \"code\": \"6655\", \"description\": \"descr\", \"isActive\": true, \"langCode\": \"ENG\", \"name\": \"Printer\" } }";

		Mockito.when(deviceTypeRepository.create(Mockito.any())).thenReturn(deviceType);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicetypes").contentType(MediaType.APPLICATION_JSON)
				.content(deviceTypeJson)).andExpect(status().isCreated());
	}

	@Test
	public void createDeviceTypeExceptionTest() throws Exception {
		String deviceTypeJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T06:58:37.498Z\", \"request\": { \"code\": \"6655\", \"description\": \"descr\", \"isActive\": true, \"langCode\": \"ENG\", \"name\": \"Printer\" } }";
		Mockito.when(deviceTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicetypes").contentType(MediaType.APPLICATION_JSON)
				.content(deviceTypeJson)).andExpect(status().isInternalServerError());
	}

	// -------------------------------MachineSpecificationTest-------------------------------
	@Test
	public void createMachineSpecificationTest() throws Exception {

		MachineSpecification machineSpeicification = new MachineSpecification();
		machineSpeicification.setId("1000");
		machineSpeicification.setLangCode("ENG");

		String machineSpecJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T11:08:41.265Z\", \"request\": { \"brand\": \"intel\", \"description\": \"intel Description\", \"id\": \"1000\", \"isActive\": true, \"langCode\": \"ENG\", \"machineTypeCode\": \"1010\", \"minDriverversion\": \"10\", \"model\": \"2014\", \"name\": \"Laptop\" } }";

		Mockito.when(machineSpecificationRepository.create(Mockito.any())).thenReturn(machineSpeicification);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(machineSpecJson)).andExpect(status().isCreated());
	}

	@Test
	public void createMachineSpecificationExceptionTest() throws Exception {

		String machineSpecJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T11:08:41.265Z\", \"request\": { \"brand\": \"intel\", \"description\": \"intel Description\", \"id\": \"1000\", \"isActive\": true, \"langCode\": \"ENG\", \"machineTypeCode\": \"1010\", \"minDriverversion\": \"10\", \"model\": \"2014\", \"name\": \"Laptop\" } }";

		Mockito.when(machineSpecificationRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(machineSpecJson))
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
		mockMvc.perform(get("/v1.0/machines")).andExpect(status().isNotFound());
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
		mockMvc.perform(get("/v1.0/machines/{id}/{langcode}", "1000", "ENG")).andExpect(status().isNotFound());
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
		mockMvc.perform(get("/v1.0/machines/{langcode}", "ENG")).andExpect(status().isNotFound());
	}

	@Test
	public void getMachineLangcodeFetchExceptionTest() throws Exception {
		when(machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/machines/{langcode}", "ENG")).andExpect(status().isInternalServerError());
	}

	// -------------------------------------------
	@Test
	public void createMachineTest() throws Exception {

		Machine machine = new Machine();
		machine.setId("1000");
		machine.setLangCode("ENG");

		String machineJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"\", \"request\": { \"id\": \"1000\", \"ipAddress\": \"127.0.0.1\", \"isActive\": true, \"langCode\": \"ENG\", \"macAddress\": \"127.0.0.2\", \"machineSpecId\": \"1010\", \"name\": \"Printer\", \"serialNum\": \"12345\", \"validityDateTime\": \"2018-12-06T10:57:09.103Z\" } }";
		Mockito.when(machineRepository.create(Mockito.any())).thenReturn(machine);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machines").contentType(MediaType.APPLICATION_JSON)
				.content(machineJson)).andExpect(status().isCreated());
	}

	@Test
	public void createMachineExceptionTest() throws Exception {

		String machineJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"\", \"request\": { \"id\": \"1000\", \"ipAddress\": \"127.0.0.1\", \"isActive\": true, \"langCode\": \"ENG\", \"macAddress\": \"127.0.0.2\", \"machineSpecId\": \"1010\", \"name\": \"Printer\", \"serialNum\": \"12345\", \"validityDateTime\": \"2018-12-06T10:57:09.103Z\" } }";
		Mockito.when(machineRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machines").contentType(MediaType.APPLICATION_JSON)
				.content(machineJson)).andExpect(status().isInternalServerError());
	}

	// -----------------------------MachineTypeTest-------------------------------------------

	@Test
	public void createMachineTypeTest() throws Exception {

		MachineType machineType = new MachineType();
		machineType.setCode("1000");
		machineType.setLangCode("ENG");

		String machineTypeJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T11:18:51.265Z\", \"request\": { \"code\": \"1000\", \"description\": \"Printer Description\", \"isActive\": true, \"langCode\": \"ENG\", \"name\": \"Printer\" } }";

		Mockito.when(machineTypeRepository.create(Mockito.any())).thenReturn(machineType);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machinetypes").contentType(MediaType.APPLICATION_JSON)
				.content(machineTypeJson)).andExpect(status().isCreated());
	}

	@Test
	public void createMachineTypeExceptionTest() throws Exception {

		String machineTypeJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T11:18:51.265Z\", \"request\": { \"code\": \"1000\", \"description\": \"Printer Description\", \"isActive\": true, \"langCode\": \"ENG\", \"name\": \"Printer\" } }";

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
		mockMvc.perform(get("/v1.0/devices/{langcode}", "ENG")).andExpect(status().isNotFound());
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
				.andExpect(status().isNotFound());
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
		mockMvc.perform(
				MockMvcRequestBuilders.post("/v1.0/devices").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isCreated());
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

	// -----------------------------------------MachineHistory---------------------------------------------
	@Test
	public void getMachineHistroyIdLangEffDTimeSuccessTest() throws Exception {
		when(machineHistoryRepository
				.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(machineHistoryList);
		mockMvc.perform(
				get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG", "2018-01-01T10:10:30.956"))
				.andExpect(status().isOk());
	}

	@Test
	public void getMachineHistroyIdLangEffDTimeNullResponseTest() throws Exception {
		when(machineHistoryRepository
				.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(null);
		mockMvc.perform(
				get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG", "2018-01-01T10:10:30.956"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getMachineHistroyIdLangEffDTimeFetchExceptionTest() throws Exception {
		when(machineHistoryRepository
				.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
						Mockito.anyString(), Mockito.anyString(), Mockito.any()))
								.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(
				get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG", "2018-01-01T10:10:30.956"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void addBlackListedWordTest() throws Exception {
		RequestDto<BlacklistedWordsDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("test  word");
		blacklistedWordsDto.setLangCode("TST");
		blacklistedWordsDto.setDescription("test description");
		blacklistedWordsDto.setIsActive(true);
		requestDto.setRequest(blacklistedWordsDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setLangCode("TST");
		Mockito.when(wordsRepository.create(Mockito.any())).thenReturn(blacklistedWords);
		mockMvc.perform(post("/v1.0/blacklistedwords").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isCreated());
	}

	@Test
	public void addBlackListedWordExceptionTest() throws Exception {
		RequestDto<BlacklistedWordsDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("test  word");
		blacklistedWordsDto.setLangCode("TST");
		blacklistedWordsDto.setDescription("test description");
		blacklistedWordsDto.setIsActive(true);
		requestDto.setRequest(blacklistedWordsDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(wordsRepository.create(Mockito.any())).thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(post("/v1.0/blacklistedwords").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void addRegistrationCenterTypeListTest() throws Exception {
		RequestDto<RegistrationCenterTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterTypeDto registrationCenterTypeDto = new RegistrationCenterTypeDto();
		registrationCenterTypeDto.setCode("testcode");
		registrationCenterTypeDto.setDescr("testdescription");
		registrationCenterTypeDto.setIsActive(true);
		registrationCenterTypeDto.setLangCode("ENG");
		registrationCenterTypeDto.setName("testname");
		requestDto.setRequest(registrationCenterTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(registrationCenterTypeRepository.create(Mockito.any())).thenReturn(regCenterType);
		mockMvc.perform(
				post("/v1.0/registrationcentertypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isCreated());
	}

	@Test
	public void addRegistrationCenterTypeListTestExceptionTest() throws Exception {
		RequestDto<RegistrationCenterTypeDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.idtype.create");
		requestDto.setVer("1.0");
		RegistrationCenterTypeDto registrationCenterTypeDto = new RegistrationCenterTypeDto();
		registrationCenterTypeDto.setCode("testcode");
		registrationCenterTypeDto.setDescr("testdescription");
		registrationCenterTypeDto.setIsActive(true);
		registrationCenterTypeDto.setLangCode("ENG");
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
		idTypeDto.setLangCode("ENG");
		idTypeDto.setName("testname");
		requestDto.setRequest(idTypeDto);
		String content = mapper.writeValueAsString(requestDto);
		IdType idType = new IdType();
		idType.setCode("IDT001");
		when(idTypeRepository.create(Mockito.any())).thenReturn(idType);
		mockMvc.perform(post("/v1.0/idtypes").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isCreated());
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
		idTypeDto.setLangCode("ENG");
		idTypeDto.setName("testname");
		requestDto.setRequest(idTypeDto);
		String contentJson = mapper.writeValueAsString(requestDto);
		when(idTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(post("/v1.0/idtypes").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

}