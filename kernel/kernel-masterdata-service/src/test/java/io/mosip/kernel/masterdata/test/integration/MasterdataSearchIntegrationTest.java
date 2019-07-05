package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentTypeExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.LocationExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.exception.ValidationException;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.service.LocationService;
import io.mosip.kernel.masterdata.test.TestBootApplication;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MasterdataSearchIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FilterTypeValidator filterTypeValidator;

	@MockBean
	private MasterdataSearchHelper masterdataSearchHelper;

	@MockBean
	private MasterDataFilterHelper masterDataFilterHelper;

	@MockBean
	private LocationService locationService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private MachineRepository machineRepository;
	
	@MockBean
	private DeviceRepository deviceRepository;

	private RegistrationCenterType centerTypeEntity;
	private RegistrationCenter centerEntity;
	private Location locationRegionEntity;
	private Location locationProvinceEntity;
	private Location locationCityEntity;
	private Location locationLaaEntity;
	private Location locationPostalCodeEntity;
	private SearchFilter filter1;
	private SearchFilter filter2;
	private SearchFilter filter3;
	private SearchFilter filter4;
	private SearchFilter filter5;
	private SearchFilter filter6;
	private SearchFilter filter7;
	private SearchFilter machineSearchFilter;
	private SearchFilter deviceSearchFilter;
	private SearchSort sort;
	private SearchDto searchDto;
	private SearchDto machineSearchDto;
	private SearchDto deviceSearchDto;
	private RequestWrapper<SearchDto> request;
	private RequestWrapper<SearchDto> machineRequestDto;
	private RequestWrapper<SearchDto> deviceRequestDto;

	private DocumentType documentType;
	private List<DocumentType> documentTypes;

	@Before
	public void setup() throws JsonProcessingException {
		documentTypeSetUp();
		filter1 = new SearchFilter();
		filter1.setColumnName("name");
		filter1.setValue("*mosip*");
		filter1.setType("contains");

		filter2 = new SearchFilter();
		filter2.setColumnName("centertypename");
		filter2.setValue("*text*");
		filter2.setType("contains");

		filter3 = new SearchFilter();
		filter3.setColumnName("city");
		filter3.setValue("cityname");
		filter3.setType("contains");

		filter4 = new SearchFilter();
		filter4.setColumnName("postal code");
		filter4.setValue("12345");
		filter4.setType("equals");

		filter5 = new SearchFilter();
		filter5.setColumnName("region");
		filter5.setValue("12345");
		filter5.setType("equals");

		filter6 = new SearchFilter();
		filter6.setColumnName("laa");
		filter6.setValue("12345");
		filter6.setType("equals");

		filter7 = new SearchFilter();
		filter7.setColumnName("province");
		filter7.setValue("12345");
		filter7.setType("equals");

		sort = new SearchSort();
		sort.setSortField("updatedDateTime");
		sort.setSortType("desc");

		centerTypeEntity = new RegistrationCenterType("10001", "eng", "REG", "Center Type", null);
		centerEntity = new RegistrationCenter();
		centerEntity.setCenterTypeCode("10001");
		centerEntity.setId("10011");
		centerEntity.setLocationCode("100011");
		centerEntity.setName("Registration Center Name");
		centerEntity.setAddressLine1("address line1");
		centerEntity.setAddressLine2("address line2");
		centerEntity.setAddressLine3("address line3");

		locationRegionEntity = new Location("LOC01", "regionname", (short) 1, "region", "LOC00", "eng", null);
		locationProvinceEntity = new Location("LOC02", "provincename", (short) 2, "province", "LOC01", "eng", null);
		locationCityEntity = new Location("LOC03", "cityname", (short) 3, "city", "LOC02", "eng", null);
		locationLaaEntity = new Location("LOC04", "laa", (short) 4, "Local Administrative Authority", "LOC03", "eng",
				null);
		locationPostalCodeEntity = new Location("LOC05", "postalcode", (short) 5, "postalcode", "LOC04", "eng", null);

		request = new RequestWrapper<>();
		searchDto = new SearchDto();
		Pagination pagination = new Pagination(0, 10);
		searchDto.setLanguageCode("eng");
		searchDto.setPagination(pagination);
		searchDto.setSort(Arrays.asList(sort));
		request.setRequest(searchDto);

		machineRequestDto = new RequestWrapper<>();
		machineSearchFilter = new SearchFilter();
		machineSearchFilter.setColumnName("name");
		machineSearchFilter.setType("equals");
		machineSearchFilter.setValue("Dekstop");
		machineSearchDto = new SearchDto();
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineSearchDto.setLanguageCode("eng");
		machineSearchDto.setSort(Arrays.asList());
		machineSearchDto.setPagination(pagination);
		machineRequestDto.setRequest(machineSearchDto);
		
		deviceRequestDto = new RequestWrapper<>();
		deviceSearchFilter = new SearchFilter();
		deviceSearchFilter.setColumnName("name");
		deviceSearchFilter.setType("equals");
		deviceSearchFilter.setValue("Dekstop");
		deviceSearchDto = new SearchDto();
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceSearchDto.setLanguageCode("eng");
		deviceSearchDto.setSort(Arrays.asList());
		deviceSearchDto.setPagination(pagination);
		deviceRequestDto.setRequest(deviceSearchDto);

		when(filterTypeValidator.validate(ArgumentMatchers.<Class<LocationExtnDto>>any(), Mockito.anyList()))
				.thenReturn(true);
		when(filterTypeValidator.validate(ArgumentMatchers.<Class<RegistrationCenterTypeExtnDto>>any(),
				Mockito.anyList())).thenReturn(true);
		when(filterTypeValidator.validate(ArgumentMatchers.<Class<RegistrationCenterExtnDto>>any(), Mockito.anyList()))
				.thenReturn(true);
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenter>>any(), Mockito.any(),
				Mockito.anyList())).thenReturn(new PageImpl<>(Arrays.asList(centerEntity), PageRequest.of(0, 10), 1));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenterType>>any(),
				Mockito.any(), Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(centerTypeEntity), PageRequest.of(0, 10), 1));

	}

	private void documentTypeSetUp() {
		documentType = new DocumentType();
		documentType.setCode("DT001");
		documentTypes = new ArrayList<>();
		documentTypes.add(documentType);
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithNameSuccess() throws Exception {
		searchDto.setFilters(Arrays.asList(filter1));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithCenterTypeNameSuccess() throws Exception {
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithCityNameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationCityEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter3));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithPostalCodeNameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationPostalCodeEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter4));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithRegionNameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationRegionEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter5));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithLAANameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationLaaEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter6));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithProvinceNameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationProvinceEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter7));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCenterFilterTypeSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList())).thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));

		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenterType>>any(),
				Mockito.any(), Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(centerTypeEntity), PageRequest.of(0, 10), 0));
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCityName() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList())).thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));

		searchDto.setFilters(Arrays.asList(filter3));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCenterName() throws Exception {
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenterType>>any(),
				Mockito.any(), Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(centerTypeEntity), PageRequest.of(0, 10), 1));
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCenterTypeName() throws Exception {
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenterType>>any(),
				Mockito.any(), Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("test")
	public void searchMachineTest() throws Exception {
		String json = objectMapper.writeValueAsString(machineRequestDto);
		Machine machine = new Machine();
		machine.setId("1001");
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList(machine));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchMachineByMappedStatusFieldTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("assigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<String> machineIdList = new ArrayList<>();
		machineIdList.add("1001");
		Machine machine = new Machine();
		machine.setId("1001");
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList(machine));
		when(machineRepository.findMappedMachineId()).thenReturn(machineIdList);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());

	}

	@Test
	@WithUserDetails("test")
	public void searchMachineByMappedStatusFieldNotFoundExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("assigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<String> machineIdList = new ArrayList<>();
		machineIdList.add("1001");
		Machine machine = new Machine();
		machine.setId("1001");
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList());
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchMachineByNotMappedStatusFieldTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("unassigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<String> machineIdList = new ArrayList<>();
		machineIdList.add("1001");
		Machine machine = new Machine();
		machine.setId("1001");
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList(machine));
		when(machineRepository.findNotMappedMachineId()).thenReturn(machineIdList);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchMachineByNotMappedStatusFieldNotFoundExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("unassigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<String> machineIdList = new ArrayList<>();
		machineIdList.add("1001");
		Machine machine = new Machine();
		machine.setId("1001");
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList());
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchMachineRequestExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("invalidValue");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchMachineByMachineTypeNameTest() throws Exception {
		machineSearchFilter.setColumnName("machineTypeName");
		machineSearchFilter.setValue("Desktop");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		MachineType type = new MachineType();
		type.setCode("machineCode");
		Page<MachineType> pageContentData = new PageImpl<>(Arrays.asList(type));
		MachineSpecification specification = new MachineSpecification();
		specification.setId("1001");
		Page<MachineSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchMachineByMachineTypeNameNotFoundExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("machineTypeName");
		machineSearchFilter.setValue("Desktop");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		Page<MachineType> pageContentData = new PageImpl<>(Arrays.asList());
		MachineSpecification specification = new MachineSpecification();
		specification.setId("1001");
		Page<MachineSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchMachineByMachineTypeNameWithCorrespondingSpecificationIdNotFoundExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("machineTypeName");
		machineSearchFilter.setValue("Desktop");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		MachineType type = new MachineType();
		type.setCode("machineCode");
		Page<MachineType> pageContentData = new PageImpl<>(Arrays.asList(type));
		Page<MachineSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList());
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("test")
	public void searchDeviceTest() throws Exception {
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("test")
	public void searchDeviceByMappedStatusFieldTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("assigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		List<String> deviceIdList = new ArrayList<>();
		deviceIdList.add("1001");
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(deviceRepository.findMappedDeviceId()).thenReturn(deviceIdList);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());

	}
	
	@Test
	@WithUserDetails("test")
	public void searchDeviceByMappedStatusFieldNotFoundExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("assigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		List<String> deviceIdList = new ArrayList<>();
		deviceIdList.add("1001");
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList());
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchDeviceByNotMappedStatusFieldTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("un-assigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		List<String> deviceIdList = new ArrayList<>();
		deviceIdList.add("1001");
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(deviceRepository.findNotMappedDeviceId()).thenReturn(deviceIdList);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchDeviceByNotMappedStatusFieldNotFoundExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("un-assigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		List<String> deviceIdList = new ArrayList<>();
		deviceIdList.add("1001");
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchDeviceRequestExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("un-assigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchDeviceByDeviceTypeNameTest() throws Exception {
		deviceSearchFilter.setColumnName("deviceTypeName");
		deviceSearchFilter.setValue("Desktop");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		DeviceType type = new DeviceType();
		type.setCode("deviceCode");
		Page<DeviceType> pageContentData = new PageImpl<>(Arrays.asList(type));
		DeviceSpecification specification = new DeviceSpecification();
		specification.setId("1001");
		Page<DeviceSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchDeviceByDeviceTypeNameNotFoundExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("deviceTypeName");
		deviceSearchFilter.setValue("Desktop");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		Page<DeviceType> pageContentData = new PageImpl<>(Arrays.asList());
		DeviceSpecification specification = new DeviceSpecification();
		specification.setId("1001");
		Page<DeviceSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceType.class), Mockito.any(), Mockito.any()))
		.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceSpecification.class), Mockito.any(),
		Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchDeviceByDeviceTypeNameWithCorrespondingSpecificationIdNotFoundExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("deviceTypeName");
		deviceSearchFilter.setValue("Desktop");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		DeviceType type = new DeviceType();
		type.setCode("deviceCode");
		Page<DeviceType> pageContentData = new PageImpl<>(Arrays.asList(type));
		Page<DeviceSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList());
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceSpecification.class), Mockito.any(),
		Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void searchBlackListedWordsTest() throws Exception {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.setColumnName("word");
		searchFilter.setType("equals");
		searchFilter.setValue("damn");
		SearchDto searchDto = new SearchDto();
		searchDto.setFilters(Arrays.asList(searchFilter));
		searchDto.setLanguageCode("eng");
		Pagination pagination = new Pagination();
		pagination.setPageFetch(5);
		pagination.setPageStart(0);
		searchDto.setPagination(pagination);
		searchDto.setSort(Arrays.asList());
		request.setRequest(searchDto);
		String json = objectMapper.writeValueAsString(request);
		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setWord("BlackListedWord");
		Page<BlacklistedWords> pageContentData = new PageImpl<>(Arrays.asList(blacklistedWords));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(BlacklistedWords.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/blacklistedwords/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void filterMachineTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(Machine.class), Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Arrays.asList("machineName", "secondMachineName"));
		mockMvc.perform(post("/machines/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void filterBlackListedWordsTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("word");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(BlacklistedWords.class), Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Arrays.asList("damn", "dammit"));
		mockMvc.perform(post("/blacklistedwords/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void filterBlackListedWordsTestForArabicLanguage() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("كلمة في القائمة السوداء");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("ara");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(BlacklistedWords.class), Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Arrays.asList("damn", "dammit"));
		mockMvc.perform(post("/blacklistedwords/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void filterBlackListedWordsTestForInvalidLanguageCode() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("invalid");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("inv");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(BlacklistedWords.class), Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Arrays.asList("damn", "dammit"));
		mockMvc.perform(post("/blacklistedwords/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void filterDeviceTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(Device.class), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("deviceName", "secondDeviceName"));
		mockMvc.perform(post("/devices/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("test")
	public void filterDocumentTypeTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(DocumentType.class), Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Arrays.asList("Birth Certificate", "Canteen card of the Army","Certificate of residence"));
		mockMvc.perform(post("/documenttypes/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDocumentTypeTest() throws Exception {
		RequestWrapper<SearchDto> requestDto = new RequestWrapper<>();
		SearchDto searchDto = new SearchDto();
		List<SearchFilter> filters = new ArrayList<SearchFilter>();
		SearchFilter searchFilter1 = new SearchFilter();
		searchFilter1.setColumnName("name");
		searchFilter1.setType("CONTAINS");
		searchFilter1.setValue("card");
		filters.add(searchFilter1);
		List<SearchSort> sort = new ArrayList<SearchSort>();
		SearchSort searchSort = new SearchSort();
		searchSort.setSortField("name");
		searchSort.setSortType("ASC");
		sort.add(searchSort);

		Pagination pagination = new Pagination();
		pagination.setPageFetch(0);
		pagination.setPageStart(10);

		searchDto.setFilters(filters);
		searchDto.setSort(sort);
		searchDto.setPagination(pagination);
		searchDto.setLanguageCode("eng");

		requestDto.setId("mosip.idtype.create");
		requestDto.setVersion("1.0");
		requestDto.setRequest(searchDto);

		String contentJson = objectMapper.writeValueAsString(requestDto);
		Page<DocumentType> page = new PageImpl<>(documentTypes, PageRequest.of(0, 10), 1);
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<DocumentType>>any(),
				Mockito.any(SearchDto.class), Mockito.anyList())).thenReturn(page);
		mockMvc.perform(post("/documenttypes/search").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDocumentTypeFailureTest() throws Exception {
		RequestWrapper<SearchDto> requestDto = new RequestWrapper<>();
		SearchDto searchDto = new SearchDto();
		List<SearchFilter> filters = new ArrayList<SearchFilter>();
		SearchFilter searchFilter1 = new SearchFilter();
		searchFilter1.setColumnName("name");
		searchFilter1.setType("qwx");
		searchFilter1.setValue("card");
		filters.add(searchFilter1);
		List<SearchSort> sort = new ArrayList<SearchSort>();
		SearchSort searchSort = new SearchSort();
		searchSort.setSortField("name");
		searchSort.setSortType("ASC");
		sort.add(searchSort);

		Pagination pagination = new Pagination();
		pagination.setPageFetch(0);
		pagination.setPageStart(10);

		searchDto.setFilters(filters);
		searchDto.setSort(sort);
		searchDto.setPagination(pagination);
		searchDto.setLanguageCode("eng");

		requestDto.setId("mosip.idtype.create");
		requestDto.setVersion("1.0");
		requestDto.setRequest(searchDto);
		List<ServiceError> errors = new ArrayList<ServiceError>();
		ServiceError error = new ServiceError();
		errors.add(error);

		String contentJson = objectMapper.writeValueAsString(requestDto);
		Page<DocumentType> page = new PageImpl<>(documentTypes, PageRequest.of(0, 10), 1);

		when(filterTypeValidator.validate(ArgumentMatchers.<Class<DocumentTypeExtnDto>>any(), Mockito.anyList()))
				.thenThrow(new ValidationException(errors));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<DocumentType>>any(),
				Mockito.any(SearchDto.class), Mockito.anyList())).thenReturn(page);
		mockMvc.perform(post("/documenttypes/search").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isInternalServerError());
	}

}
