package io.mosip.registration.test.packetStatusSync;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.dao.impl.RegPacketStatusDAOImpl;
import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.repositories.RegistrationRepository;

public class RegPacketStatusDaoImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	RegistrationRepository registrationRepository;
	
	@InjectMocks
	RegPacketStatusDAOImpl packetStatusDao;
	
	@Test
	public void getPacketIdsByStatusPostSyncedTest() {
		List<Registration> registrations = new ArrayList<>();
		Registration registration = new Registration();
		registration.setId("12345");
		registration.setClientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
		registrations.add(registration);
		when(registrationRepository.findByclientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode())).thenReturn(registrations);
		List<String> regIds = new ArrayList<>();
		regIds.add("12345");
		assertThat(packetStatusDao.getPacketIdsByStatusUploaded(), is(regIds));
	}
	
	@Test
	public void updatePacketIdsByServerStatusTest() {
		Registration registration = new Registration();
		registration.setId("12345");
		when(registrationRepository.findById(Registration.class, "12345")).thenReturn(registration);
		List<RegPacketStatusDTO> packetStatus = new ArrayList<>();
		RegPacketStatusDTO packetStatusDTO = new RegPacketStatusDTO("12345", "DECRYPTED");
		packetStatus.add(packetStatusDTO);
		packetStatusDao.updatePacketIdsByServerStatus(packetStatus);
	}
	
}
