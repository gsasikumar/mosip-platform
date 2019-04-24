package io.mosip.registration.processor.packet.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.exception.JschConnectionException;
import io.mosip.registration.processor.core.exception.SftpFileOperationException;
import io.mosip.registration.processor.core.packet.dto.SftpJschConnectionDto;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.decryptor.Decryptor;
import io.mosip.registration.processor.packet.uploader.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.uploader.service.PacketUploaderService;
import io.mosip.registration.processor.packet.uploader.service.impl.PacketUploaderServiceImpl;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

@RefreshScope
@RunWith(SpringRunner.class)
//@RunWith(PowerMockRunner.class)

public class PacketUploaderServiceTest {
	
	@InjectMocks
	private PacketUploaderService<MessageDTO> packetuploaderservice = new PacketUploaderServiceImpl();
	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet archiver. */
	@Mock
	private PacketArchiver packetArchiver;

	/** The env. */
	@Mock
	private Environment env;

	/** The file manager. */
	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The adapter. */
	@Mock
	private FileSystemAdapter adapter;

	/** The dto. */
	MessageDTO dto = new MessageDTO();

	/** The foo logger. */
	private Logger fooLogger;
	private SyncRegistrationEntity regEntity;

	/** The list appender. */
	private ListAppender<ILoggingEvent> listAppender;

	/** The entry. */
	InternalRegistrationStatusDto entry = new InternalRegistrationStatusDto();
	
	@Mock
	private InputStream is;
	
	private byte[] enrypteddata;
	
	@Mock
	private VirusScanner<Boolean, InputStream> virusScannerService;
	
	@Mock
	private SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;
	
	@Mock
	private Decryptor decryptor;
	
	@Mock 
	private SftpJschConnectionDto sftpDTO;
	
	private File file;
	
	@Mock
	private ChannelSftp channelSftp = new ChannelSftp();
	

	@Before
	public void setUp() throws IOException
	{
		//ReflectionTestUtils.setField(packetuploaderservice, "extention", ".zip");
	    ReflectionTestUtils.setField(packetuploaderservice, "host", "localhost");
	    ReflectionTestUtils.setField(packetuploaderservice, "dmzPort","7878");
	    //ReflectionTestUtils.setField(packetuploaderservice, "host", "5");
		file = new File("src/test/resources/1001.zip");
		dto.setRid("1001");
		entry.setRegistrationId("1001");
		entry.setRetryCount(0);
		entry.setStatusComment("virus scan");
		regEntity = new SyncRegistrationEntity();
		regEntity.setCreateDateTime(LocalDateTime.now());
		regEntity.setCreatedBy("Mosip");
		regEntity.setId("001");
		regEntity.setIsActive(true);
		regEntity.setLangCode("eng");
		regEntity.setRegistrationId("0000");
		regEntity.setRegistrationType("new");
		regEntity.setStatusCode("NEW_REGISTRATION");
		regEntity.setStatusComment("registration begins");
		is=new FileInputStream(file);
		enrypteddata=IOUtils.toByteArray(is);
		String ppkFileLocation=null;
		String ppkFileName=null;
		sftpDTO=new SftpJschConnectionDto();
		sftpDTO.setHost("localhost");
		sftpDTO.setPort(7878);
		sftpDTO.setPpkFileLocation(ppkFileLocation+File.separator+ppkFileName);
		//sftpDTO.set
		Mockito.when(syncRegistrationService.findByRegistrationId(Mockito.any())).thenReturn(regEntity);
		
		
	}
	
	@Test
	public void testvalidateAndUploadPacketSuccess() throws Exception
	{
		Mockito.when(registrationStatusService.getRegistrationStatus(Mockito.any())).thenReturn(entry);
		ReflectionTestUtils.setField(packetuploaderservice, "maxRetryCount",3);
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(enrypteddata);
		Mockito.when(virusScannerService.scanFile(Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.decrypt(Mockito.any(),Mockito.any())).thenReturn(is);
		Mockito.when(adapter.storePacket(Mockito.any(),Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(adapter).unpackPacket(Mockito.any());
		Mockito.when(adapter.isPacketPresent(Mockito.any())).thenReturn(Boolean.TRUE);
		Mockito.when(packetArchiver.archivePacket(Mockito.any(),Mockito.any())).thenReturn(Boolean.TRUE);
		Mockito.when(fileManager.cleanUp(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(Boolean.TRUE);
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertTrue(result.getIsValid());
	}
	
	@Test
	public void testvalidateAndUploadPacketFailureRetry() throws Exception
	{
		Mockito.when(registrationStatusService.getRegistrationStatus(Mockito.any())).thenReturn(entry);
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(enrypteddata);
		Mockito.when(virusScannerService.scanFile(Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.decrypt(Mockito.any(),Mockito.any())).thenReturn(is);
		Mockito.when(adapter.storePacket(Mockito.any(),Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(adapter).unpackPacket(Mockito.any());
		Mockito.when(adapter.isPacketPresent(Mockito.any())).thenReturn(Boolean.TRUE);
		Mockito.when(packetArchiver.archivePacket(Mockito.any(),Mockito.any())).thenReturn(Boolean.TRUE);
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testTableNotaccessibleException() throws Exception
	{
		Mockito.when(registrationStatusService.getRegistrationStatus(Mockito.any())).thenThrow(new TablenotAccessibleException());
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(enrypteddata);
		Mockito.when(virusScannerService.scanFile(Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.decrypt(Mockito.any(),Mockito.any())).thenReturn(is);
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testPacketNotFoundException() throws JschConnectionException, SftpFileOperationException
	{
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new PacketNotFoundException());
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testFSAdapterException() throws Exception
	{
		ReflectionTestUtils.setField(packetuploaderservice, "maxRetryCount",3);
		Mockito.when(registrationStatusService.getRegistrationStatus(Mockito.any())).thenReturn(entry);
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(enrypteddata);
		Mockito.when(virusScannerService.scanFile(Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.decrypt(Mockito.any(),Mockito.any())).thenReturn(is);
		Mockito.when(adapter.storePacket(Mockito.any(),Mockito.any(InputStream.class))).thenThrow(new FSAdapterException("",""));
		//Mockito.doNothing().when(adapter).unpackPacket(Mockito.any());
		//Mockito.when(adapter.isPacketPresent(Mockito.any())).thenReturn(Boolean.TRUE);
		Mockito.when(packetArchiver.archivePacket(Mockito.any(),Mockito.any())).thenReturn(Boolean.TRUE);
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testJschConnectionException() throws JschConnectionException, SftpFileOperationException
	{
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new JschConnectionException());
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testSftpFileOperationException() throws JschConnectionException, SftpFileOperationException
	{
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new SftpFileOperationException());
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	@Ignore
	public void testIOException() throws SftpException, JschConnectionException, SftpFileOperationException
	{
		//Mockito.when(channelSftp.get(Mockito.any())).thenThrow(new IOException());
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new IOException());
		//Mockito.doThrow(new IOException()).when(channelSftp.get(Mockito.any()));
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testUploadfailure() throws Exception
	{
		Mockito.when(registrationStatusService.getRegistrationStatus(Mockito.any())).thenReturn(entry);
		ReflectionTestUtils.setField(packetuploaderservice, "maxRetryCount",3);
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(enrypteddata);
		Mockito.when(virusScannerService.scanFile(Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.decrypt(Mockito.any(),Mockito.any())).thenReturn(is);
		Mockito.when(adapter.storePacket(Mockito.any(),Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(adapter).unpackPacket(Mockito.any());
		Mockito.when(adapter.isPacketPresent(Mockito.any())).thenReturn(Boolean.TRUE);
		Mockito.when(packetArchiver.archivePacket(Mockito.any(),Mockito.any())).thenReturn(Boolean.TRUE);
		Mockito.when(fileManager.cleanUp(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(Boolean.FALSE);
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testArchiverFailure() throws Exception
	{
		Mockito.when(registrationStatusService.getRegistrationStatus(Mockito.any())).thenReturn(entry);
		ReflectionTestUtils.setField(packetuploaderservice, "maxRetryCount",3);
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(enrypteddata);
		Mockito.when(virusScannerService.scanFile(Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.decrypt(Mockito.any(),Mockito.any())).thenReturn(is);
		Mockito.when(adapter.storePacket(Mockito.any(),Mockito.any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(adapter).unpackPacket(Mockito.any());
		Mockito.when(adapter.isPacketPresent(Mockito.any())).thenReturn(Boolean.TRUE);
		Mockito.when(packetArchiver.archivePacket(Mockito.any(),Mockito.any())).thenReturn(Boolean.FALSE);
		//Mockito.when(fileManager.cleanUp(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(Boolean.FALSE);
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
		
	}
	
	@Test
	public void testVirusScanFailedException() throws JschConnectionException, SftpFileOperationException
	{
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(enrypteddata);
		Mockito.when(virusScannerService.scanFile(Mockito.any(InputStream.class))).thenReturn(Boolean.FALSE);
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testScannerServiceFailedException() throws JschConnectionException, SftpFileOperationException
	{
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(enrypteddata);
		Mockito.when(virusScannerService.scanFile(Mockito.any(InputStream.class))).thenThrow(new VirusScannerException());
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testException() throws JschConnectionException, SftpFileOperationException
	{
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new IndexOutOfBoundsException());
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
	
	@Test
	public void testPacketUploaderFailed() throws JschConnectionException, SftpFileOperationException
	{
		Mockito.when(fileManager.getFile(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(null);
		MessageDTO result=packetuploaderservice.validateAndUploadPacket(dto.getRid());
		assertFalse(result.getIsValid());
	}
}
