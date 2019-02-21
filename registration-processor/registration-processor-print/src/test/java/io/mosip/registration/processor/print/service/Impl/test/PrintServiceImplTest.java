package io.mosip.registration.processor.print.service.Impl.test;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.print.service.PrintService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.spi.uincardgenerator.UinCardGenerator;
import io.mosip.registration.processor.message.sender.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.kernel.dto.IdResponseDTO;
import io.mosip.registration.processor.print.kernel.dto.ResponseDTO;
import io.mosip.registration.processor.print.service.impl.PrintServiceImpl;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class PrintServiceImplTest {
	
	/** The rest client service. */
	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The id response. */
	private IdResponseDTO idResponse = new IdResponseDTO();

	/** The response. */
	private ResponseDTO response = new ResponseDTO();

	/** The template generator. */
	@Mock
	private TemplateGenerator templateGenerator;

	/** The uin card generator. */
	@Mock
	private UinCardGenerator<ByteArrayOutputStream> uinCardGenerator;

	/** The utility. */
	@Mock
	private Utilities utility;
	
	@Mock
	private QrCodeGenerator<QrVersion> qrCodeGenerator;
	
	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	/** The stage. */
	@InjectMocks
	private PrintService<byte[]> printService = new PrintServiceImpl();
	
	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws Exception {
		System.setProperty("primary.language", "eng");
		
		List<String> uinList = new ArrayList<>();
		uinList.add("4238135072");
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(uinList);

		LinkedHashMap<String, Object> identityMap = new LinkedHashMap<>();
		Map<String, String> map = new HashMap<>();
		map.put("language", "eng");
		map.put("value", "Alok");
		JSONObject j1 = new JSONObject(map);

		Map<String, String> map2 = new HashMap<>();
		map2.put("language", "ara");
		map2.put("value", "Alok");
		JSONObject j2 = new JSONObject(map2);
		JSONArray array = new JSONArray();
		array.add(j1);
		array.add(j2);
		identityMap.put("fullName", array);
		identityMap.put("gender", array);
		identityMap.put("addressLine1", array);
		identityMap.put("addressLine2", array);
		identityMap.put("addressLine3", array);
		identityMap.put("city", array);
		identityMap.put("province", array);
		identityMap.put("region", array);
		identityMap.put("dateOfBirth", "1980/11/14");
		identityMap.put("phone", "9967878787");
		identityMap.put("email", "raghavdce@gmail.com");
		identityMap.put("postalCode", "900900");

		Object identity = identityMap;
		response.setIdentity(identity);
		idResponse.setResponse(response);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(idResponse);

		String artifact = "UIN Card Template";
		InputStream artifactStream = new ByteArrayInputStream(artifact.getBytes());
		Mockito.when(templateGenerator.getTemplate(any(), any(), anyString())).thenReturn(artifactStream);

		byte[] buffer = new byte[8192];
		int bytesRead;
		
		while ((bytesRead = artifactStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		
		Mockito.when(uinCardGenerator.generateUinCard(any(), any())).thenReturn(outputStream);

		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");

		String value = "{\r\n" + "	\"identity\": {\r\n" + "		\"name\": {\r\n"
				+ "			\"value\": \"fullName\",\r\n" + "			\"weight\": 20\r\n" + "		},\r\n"
				+ "		\"gender\": {\r\n" + "			\"value\": \"gender\",\r\n" + "			\"weight\": 20\r\n"
				+ "		},\r\n" + "		\"dob\": {\r\n" + "			\"value\": \"dateOfBirth\",\r\n"
				+ "			\"weight\": 20\r\n" + "		},\r\n" + "		\"pheoniticName\": {\r\n"
				+ "			\"weight\": 20\r\n" + "		},\r\n" + "		\"poa\": {\r\n"
				+ "			\"value\" : \"proofOfAddress\"\r\n" + "		},\r\n" + "		\"poi\": {\r\n"
				+ "			\"value\" : \"proofOfIdentity\"\r\n" + "		},\r\n" + "		\"por\": {\r\n"
				+ "			\"value\" : \"proofOfRelationship\"\r\n" + "		},\r\n" + "		\"pob\": {\r\n"
				+ "			\"value\" : \"proofOfDateOfBirth\"\r\n" + "		},\r\n"
				+ "		\"individualBiometrics\": {\r\n" + "			\"value\" : \"individualBiometrics\"\r\n"
				+ "		},\r\n" + "		\"age\": {\r\n" + "			\"value\" : \"age\"\r\n" + "		},\r\n"
				+ "		\"addressLine1\": {\r\n" + "			\"value\" : \"addressLine1\"\r\n" + "		},\r\n"
				+ "		\"addressLine2\": {\r\n" + "			\"value\" : \"addressLine2\"\r\n" + "		},\r\n"
				+ "		\"addressLine3\": {\r\n" + "			\"value\" : \"addressLine3\"\r\n" + "		},\r\n"
				+ "		\"region\": {\r\n" + "			\"value\" : \"region\"\r\n" + "		},\r\n"
				+ "		\"province\": {\r\n" + "			\"value\" : \"province\"\r\n" + "		},\r\n"
				+ "		\"postalCode\": {\r\n" + "			\"value\" : \"postalCode\"\r\n" + "		},\r\n"
				+ "		\"phone\": {\r\n" + "			\"value\" : \"phone\"\r\n" + "		},\r\n"
				+ "		\"email\": {\r\n" + "			\"value\" : \"email\"\r\n" + "		},\r\n"
				+ "		\"localAdministrativeAuthority\": {\r\n"
				+ "			\"value\" : \"localAdministrativeAuthority\"\r\n" + "		},\r\n"
				+ "		\"idschemaversion\": {\r\n" + "			\"value\" : \"IDSchemaVersion\"\r\n" + "		},\r\n"
				+ "		\"cnienumber\": {\r\n" + "			\"value\" : \"CNIENumber\"\r\n" + "		},\r\n"
				+ "		\"city\": {\r\n" + "			\"value\" : \"city\"\r\n" + "		}\r\n" + "	}\r\n" + "} ";

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", anyString(), anyString()).thenReturn(value);
		
		byte[] qrcode = "QRCODE GENERATED".getBytes();
		Mockito.when(qrCodeGenerator.generateQrCode(any(), any())).thenReturn(qrcode);
	}
	
	@Test
	public void testPdfGeneratedwithUINSuccess() {
		String uin = "2046958192";
		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getPdf(IdType.UIN, uin );
		assertArrayEquals(expected, result);
	}
	
	@Test
	public void testPdfGeneratedwithRIDSuccess() {
		List<String> uinList = new ArrayList<>();
		uinList.add("2046958192");
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(uinList);
		
		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getPdf(IdType.UIN, uinList.get(0) );
		assertArrayEquals(expected, result);
	}
	
	/**
	 * Test UIN not found.
	 */
	@Test(expected = PDFGeneratorException.class)
	public void testUINNotFound() {
		List<String> uinList = new ArrayList<>();
		uinList.add(null);
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(uinList);

		printService.getPdf(IdType.RID, "2046958192");
	}
	
	/**
	 * Test template processing failure.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test(expected = TemplateProcessingFailureException.class)
	public void testTemplateProcessingFailure() throws ApisResourceAccessException, IOException {
		TemplateProcessingFailureException e = new TemplateProcessingFailureException();
		Mockito.doThrow(e).when(templateGenerator).getTemplate(any(), any(), anyString());
		
		List<String> uinList = new ArrayList<>();
		uinList.add("2046958192");
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(uinList);
		
		printService.getPdf(IdType.UIN, uinList.get(0) );
	}
	
	/**
	 * Test PDF generator exception.
	 */
	@Test(expected = PDFGeneratorException.class)
	public void testPDFGeneratorException() {
		PDFGeneratorException e = new PDFGeneratorException(null, null);
		Mockito.doThrow(e).when(uinCardGenerator).generateUinCard(any(), any());
		
		List<String> uinList = new ArrayList<>();
		uinList.add("2046958192");
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(uinList);
		
		printService.getPdf(IdType.UIN, uinList.get(0) );
	}
	
	/**
	 * Test api resource exception.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	@Test(expected = PDFGeneratorException.class)
	public void testApiResourceException() throws ApisResourceAccessException {
		ApisResourceAccessException e = new ApisResourceAccessException();
		Mockito.doThrow(e).when(restClientService).getApi(any(), any(), any(), any(), any());

		List<String> uinList = new ArrayList<>();
		uinList.add("2046958192");
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(uinList);
		
		printService.getPdf(IdType.UIN, uinList.get(0) );
	}
	
}
