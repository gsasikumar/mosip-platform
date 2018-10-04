package io.mosip.registration.test.dao.impl;

import static org.mockito.Mockito.when;

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
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.dao.impl.RegTransactionDAOImpl;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegTransactionRepository;

public class RegTransactionDAOTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@InjectMocks
	private RegTransactionDAOImpl regTransactionDAOImpl;
	@Mock
	private RegTransactionRepository regTransactionRepository;
	
	@Before
	public void initialize() {
		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(regTransactionDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void testSaveRegistration() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(regTransactionDAOImpl, "LOGGER", logger);

		when(regTransactionRepository.create(Mockito.any(RegistrationTransaction.class))).thenReturn(new RegistrationTransaction());
		regTransactionDAOImpl.save("11111");
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void testTransactionException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(regTransactionDAOImpl, "LOGGER", logger);
		when(regTransactionRepository.create(Mockito.any(RegistrationTransaction.class))).thenThrow(new RuntimeException());
		regTransactionDAOImpl.save("file");
	}
	
	@Test
	public void insertPacketTransDetailsTest() {
		ReflectionTestUtils.setField(regTransactionDAOImpl, "LOGGER", logger);
		List<RegistrationTransaction> packetListnew = new ArrayList<RegistrationTransaction>();
		packetListnew.add(new RegistrationTransaction());
		when(regTransactionRepository.saveAll(Mockito.anyListOf(RegistrationTransaction.class))).thenReturn(packetListnew);
		regTransactionDAOImpl.insertPacketTransDetails(packetListnew);
	}

}
