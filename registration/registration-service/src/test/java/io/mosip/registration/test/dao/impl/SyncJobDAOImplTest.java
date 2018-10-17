package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.dao.SyncJobDAO.SyncJobInfo;
import io.mosip.registration.dao.impl.SyncJobDAOImpl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.repositories.SyncJobRepository;

public class SyncJobDAOImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@InjectMocks
	private SyncJobDAOImpl syncJobDAOImpl ;
	@Mock
	private SyncJobRepository syncStatusRepository;
	@Mock
	private RegistrationRepository registrationRepository;
	@Mock
	private SyncJobInfo syncJobnfo;
	@Mock
	private AuditFactory auditFactory;
	
	@Before
	public void initialize() throws IOException, URISyntaxException {
		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);

		ReflectionTestUtils.invokeMethod(syncJobDAOImpl, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(syncJobDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@Test
	public void testGetSyncStatus() {
		List<SyncControl> comparableList = new ArrayList<>();
		List<String> statusCodes = new ArrayList<>();
		statusCodes.add("R");
		statusCodes.add("I");
		statusCodes.add("A");
		statusCodes.add("C");
		statusCodes.add("U");
		statusCodes.add("L");
		statusCodes.add("S");
		statusCodes.add("H");
		statusCodes.add("E");
		
		List<Registration> registrationsList=new ArrayList<>();
		
		Registration registration1=new Registration();
		registration1.setId("101");
		registration1.setClientStatusCode("A");
		registration1.setAckFilename("Ack1");
		Registration registration2=new Registration();
		registration2.setId("102");
		registration2.setClientStatusCode("I");
		registration2.setAckFilename("Ack2");
		registrationsList.add(registration1);
		registrationsList.add(registration2);
		
		SyncControl syncControl1=new SyncControl();
		syncControl1.setSJobId("MDS_J00001");
		syncControl1.setLastSyncDtimez(OffsetDateTime.parse("2018-10-05T18:05:19.199+05:30"));
		SyncControl syncControl2=new SyncControl();
		syncControl2.setSJobId("LER_J00009");
		syncControl2.setLastSyncDtimez(OffsetDateTime.parse("2018-10-05T18:25:13.199+05:30"));
		
		comparableList.add(syncControl1);
		comparableList.add(syncControl2);

		Mockito.when(syncStatusRepository.findAll()).thenReturn(comparableList);
		Mockito.when(registrationRepository.findByClientStatusCodeIn(statusCodes)).thenReturn(registrationsList);
		
		syncJobnfo=syncJobDAOImpl.getSyncStatus();
		assertEquals("MDS_J00001",syncJobnfo.getSyncControlList().get(0).getSJobId());
		assertEquals("LER_J00009",syncJobnfo.getSyncControlList().get(1).getSJobId());
		assertEquals(OffsetDateTime.parse("2018-10-05T18:05:19.199+05:30"),syncJobnfo.getSyncControlList().get(0).getLastSyncDtimez());
		assertEquals(OffsetDateTime.parse("2018-10-05T18:25:13.199+05:30"),syncJobnfo.getSyncControlList().get(1).getLastSyncDtimez());
		if(syncJobnfo.getYetToExportCount()==registrationsList.size());{
			assertTrue(true);
		}
	}
}
