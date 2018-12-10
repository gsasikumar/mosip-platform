package io.mosip.kernel.masterdata.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.getresponse.MachineResponseDto;
import io.mosip.kernel.masterdata.service.MachineService;

/*@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = MachineDetailController.class, secure = false)*/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MachineControllerTest1 {

	@Autowired
	public MockMvc mockMvc;

	@MockBean
	private MachineService machineService;

	MachineResponseDto machineResponseDto;

	@Before
	public void setUp() {

		machineResponseDto = new MachineResponseDto();
		List<MachineDto> machineDtoList = new ArrayList<>();
		MachineDto machineDto = new MachineDto();
		machineDto.setId("1000");
		machineDto.setName("HP");
		machineDto.setSerialNum("1234567890");
		machineDto.setMacAddress("100.100.100.80");
		machineDto.setLangCode("ENG");
		machineDto.setIsActive(true);
		machineDtoList.add(machineDto);
		machineResponseDto.setMachines(machineDtoList);
	}

	@Test
	public void getMachineIdLangcodeTest() throws Exception {
		Mockito.when(machineService.getMachine(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(machineResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/machines/1000/ENG")).andExpect(status().isOk());
	}

	@Test
	public void getMachineLangcodeTest() throws Exception {
		Mockito.when(machineService.getMachine(Mockito.anyString())).thenReturn(machineResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/machines/ENG")).andExpect(status().isOk());
	}

	@Test
	public void getMachineAllTest() throws Exception {
		Mockito.when(machineService.getMachineAll()).thenReturn(machineResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/machines")).andExpect(status().isOk());
	}

}
