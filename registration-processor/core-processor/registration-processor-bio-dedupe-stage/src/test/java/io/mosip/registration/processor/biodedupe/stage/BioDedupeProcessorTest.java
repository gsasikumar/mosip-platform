package io.mosip.registration.processor.biodedupe.stage;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.processor.biodedupe.dao.BioDedupDao;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.StageNameConstant;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

/**
 * The Class BioDedupeStageTest.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class BioDedupeProcessorTest {

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The bio dedupe service. */
	@Mock
	private BioDedupeService bioDedupeService;

	@Mock
	private RegistrationStatusDao registrationStatusDao;

	@Mock
	BioDedupDao bioDedupDao;

	/** The dto. */
	MessageDTO dto = new MessageDTO();

	/** The vertx. */
	private Vertx vertx;

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The matched reg ids. */
	List<String> matchedRegIds = new ArrayList<String>();

	@Mock
	RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	@InjectMocks
	private BioDedupeProcessor bioDedupeProcessor;

	private String stageName = "BioDedupeStage";

	@Mock
	Utilities utilities;

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Mock
	RegistrationStatusEntity entity = new RegistrationStatusEntity();

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(bioDedupeProcessor, "ageLimit", "4");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(auditResponseDto);
		Mockito.doReturn(responseWrapper).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_405.toString(), EventName.UPDATE.toString(),
				EventType.BUSINESS.toString(), "1234testcase", ApiName.AUDIT);
		dto.setRid("reg1234");
		registrationStatusDto.setRegistrationId("reg1234");
		registrationStatusDto.setRegistrationType("New");
		Mockito.when(utilities.getElapseStatus(any(), any())).thenReturn("New-packet");
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any()))
				.thenReturn("1233445566".getBytes("UTF-16"));
		Mockito.when(registrationStatusMapperUtil.getStatusCode(any())).thenReturn("ERROR");

		entity.setLatestRegistrationTransactionId("t123");
		Mockito.when(registrationStatusDao.findById(any())).thenReturn(entity);
	}

	/**
	 * Test bio dedupe success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testBioDedupeInsertToAbisHandlerSuccess() throws Exception {

		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertTrue(messageDto.getDestinationStage().equalsIgnoreCase(StageNameConstant.ABISHANDLERSTAGE));

	}

	@Test
	public void testBioDedupeMoveToUinSuccess() throws Exception {

		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(null);
		Mockito.when(utilities.getApplicantAge(any())).thenReturn(2);
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertTrue(messageDto.getDestinationStage().equalsIgnoreCase(StageNameConstant.UINGENERATORSTAGE));

	}

	@Test
	public void testBioDedupeHandlerIdentifyUINStage() throws Exception {
		Mockito.when(utilities.getElapseStatus(any(), any())).thenReturn("Handler");
		Mockito.when(bioDedupDao.getAbisResponseDetailRecords(any(), any())).thenReturn(Collections.emptyList());
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertTrue(messageDto.getDestinationStage().equalsIgnoreCase(StageNameConstant.UINGENERATORSTAGE));

	}

	@Test
	public void testBioDedupeHandlerIdentifyManualStage() throws Exception {
		Mockito.when(utilities.getElapseStatus(any(), any())).thenReturn("Handler");

		AbisResponseDetEntity abisDet = new AbisResponseDetEntity();
		abisDet.setCrBy("mosip");

		AbisResponseEntity abis = new AbisResponseEntity();
		abis.setStatusCode("status");

		List<AbisResponseDetEntity> abisResponseDetEntities = new ArrayList<>();

		abisResponseDetEntities.add(abisDet);
		Mockito.when(bioDedupDao.getAbisResponseDetailRecords(any(), any())).thenReturn(abisResponseDetEntities);
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertTrue(messageDto.getDestinationStage().equalsIgnoreCase(StageNameConstant.MANUALVERIFICATIONSTAGE));

	}
}
