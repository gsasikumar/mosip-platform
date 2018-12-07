package io.mosip.registrationprocessor.stages.demodedupe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.stages.demodedupe.DemoDedupeAuthentication;
import io.mosip.registration.processor.stages.demodedupe.DemodedupeStage;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class DemodedupeStageTest {
	
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;
	
	@Mock
	private DemoDedupeAuthentication demoDedupeAuthentication;
	
	MessageDTO dto = new MessageDTO();

	@InjectMocks
	private DemodedupeStage demodedupeStage = new DemodedupeStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String clusterAddress, String localhost) {
			return null;
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
	};

	@Test
	public void testDeployVerticle() {
		demodedupeStage.deployVerticle();
	}
	
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();
	
	@Before
	public void setUp() throws Exception {
		
		dto.setRid("2018701130000410092018110735");

		MockitoAnnotations.initMocks(this);
		
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_405.toString(), EventName.UPDATE.toString(),
				EventType.BUSINESS.toString(), "1234testcase");

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
	
	}
	
	@Test
	public void testDemoDedupeSuccess() {
		
		Set<String> emptyDuplicateIdSet = new HashSet<>();
		Mockito.when(packetInfoManager.performDedupe(anyString())).thenReturn(emptyDuplicateIdSet);

		MessageDTO messageDto = demodedupeStage.process(dto);
		assertTrue(messageDto.getIsValid());

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testDemoDedupePotentialMatch() throws ApisResourceAccessException, IOException {
		
		Set<String> duplicateIds = new HashSet<>();
		duplicateIds.add("2018701130000410092018110736");
		duplicateIds.add("2018701130000410092018110737");
		
		Mockito.when(packetInfoManager.performDedupe(anyString())).thenReturn(duplicateIds);
		Mockito.when(packetInfoManager.findUINById(anyString())).thenReturn("12345");
		
		Mockito.when(demoDedupeAuthentication.authenticateDuplicates(anyString(), anyList())).thenReturn(false);
		
		MessageDTO messageDto = demodedupeStage.process(dto);
		assertFalse(messageDto.getIsValid());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testDemoDedupeFailure() throws ApisResourceAccessException, IOException{
		Set<String> duplicateIds = new HashSet<>();
		duplicateIds.add("2018701130000410092018110736");
		duplicateIds.add("2018701130000410092018110737");
		
		Mockito.when(packetInfoManager.performDedupe(anyString())).thenReturn(duplicateIds);
		Mockito.when(packetInfoManager.findUINById(anyString())).thenReturn("12345");
		
		Mockito.when(demoDedupeAuthentication.authenticateDuplicates(anyString(), anyList())).thenReturn(true);
		
		demodedupeStage.process(dto);
	}
	
}
