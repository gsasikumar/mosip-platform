package org.mosip.registration.processor.packet.scanner.job.impl.tasklet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import org.mosip.registration.processor.packet.manager.service.FileManager;
import org.mosip.registration.processor.packet.scanner.job.impl.tasklet.LandingZoneScannerTasklet;
import org.mosip.registration.processor.status.code.RegistrationStatusCode;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.exception.TablenotAccessibleException;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

@RunWith(SpringRunner.class)
public class LandingZoneScannerTaskletTest {

	

	@InjectMocks
	LandingZoneScannerTasklet landingZoneToVirusScanTasklet;

	@Mock
	FileManager<DirectoryPathDto, InputStream> filemanager;

	@Mock
	RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	@MockBean
	StepContribution stepContribution;

	@MockBean
	ChunkContext chunkContext;

	RegistrationStatusDto dto1;
	RegistrationStatusDto dto2;
	List<RegistrationStatusDto> list;

	@Before
	public void setup() {
		dto1 = new RegistrationStatusDto("1001", "landingZone", 0, null, null);
		dto2 = new RegistrationStatusDto("1002", "landingZone", 0, null, null);
		list = new ArrayList<RegistrationStatusDto>();
	}

	@Test
	public void landingZoneToVirusScanTaskletSuccessTest() throws Exception {
		list.add(dto1);
		list.add(dto2);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.when(filemanager.checkIfFileExists(any(DirectoryPathDto.class), any(String.class))).
				thenReturn(true);
		Mockito.doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),
				any(String.class));

		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any(RegistrationStatusDto.class));

		RepeatStatus status = landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void noFilesToBeMovedTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		

		RepeatStatus status = landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("There are currently no files to be moved");
			}
		}));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void registrationStatusServiceFindingEntitiesfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		
		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService).findbyfilesByThreshold(any(String.class));

		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The Enrolment Status table is not accessible");
			}
		}));
		

	}

	@SuppressWarnings("unchecked")
	@Test
	public void registrationStatusServiceUpdateEnrolmentfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		list.add(dto1);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.when(filemanager.checkIfFileExists(any(DirectoryPathDto.class), any(String.class))).
		thenReturn(true);
		Mockito.doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),
				any(String.class));
		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService).updateRegistrationStatus(any(RegistrationStatusDto.class));
		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The Enrolment Status table is not accessible");
			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void CopyfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		list.add(dto1);

		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doThrow(IOException.class).when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));

		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage().
						contains("The Virus Scan Path set by the System is not accessible");
			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void CleanUpfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		list.add(dto1);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.when(filemanager.checkIfFileExists(any(DirectoryPathDto.class), any(String.class))).
		thenReturn(true);
		Mockito.doThrow(FileNotFoundInDestinationException.class).when(filemanager).cleanUpFile(any(DirectoryPathDto.class),
				any(DirectoryPathDto.class), any(String.class));

		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage().
						contains("The Virus Scan Path set by the System is not accessible");
			}
		}));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void CheckifExistsfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		list.add(dto1);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.doThrow(FileNotFoundInDestinationException.class).when(filemanager).checkIfFileExists(any(DirectoryPathDto.class),
				 any(String.class));

		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage().
						contains("The Virus Scan Path set by the System is not accessible");
			}
		}));
	}
}
