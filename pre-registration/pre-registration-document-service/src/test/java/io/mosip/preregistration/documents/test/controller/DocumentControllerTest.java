package io.mosip.preregistration.documents.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.PutMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.AuthProvider;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.documents.code.DocumentStatusMessages;
import io.mosip.preregistration.documents.controller.DocumentController;
import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.dto.DocumentResponseDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.service.DocumentService;
import io.mosip.preregistration.documents.service.util.DocumentServiceUtil;
import io.mosip.preregistration.documents.test.DocumentTestApplication;

/**
 * Test class to test the DocumentUploader Controller methods
 * 
 * @author Sanober Noor
 * @author Rajath KR
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @since 1.0.0
 * 
 */
@SpringBootTest(classes = { DocumentTestApplication.class })
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DocumentControllerTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Autowired reference for {@link #MockMvc}
	 */
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthProvider authProvider;

	private MockMultipartFile mockMultipartFile;

	/**
	 * Creating Mock Bean for DocumentUploadService
	 */
	@MockBean
	private DocumentService service;

	@MockBean
	private DocumentServiceUtil serviceutil;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Creating Mock Bean for FilesystemCephAdapterImpl
	 */
	@MockBean
	private FileSystemAdapter fs;

	List<DocumentEntity> DocumentList = new ArrayList<>();
	Map<String, String> response = null;

	String documentId;
	boolean flag;
	
	String docJson = "";

	Map<String, String> map = new HashMap<>();
	MainListResponseDTO responseCopy = new MainListResponseDTO<>();
	MainListResponseDTO responseDelete = new MainListResponseDTO<>();
	MainListResponseDTO<DocumentResponseDTO> responseMain = new MainListResponseDTO<>();
	DocumentRequestDTO documentDto = null;
	List<DocumentResponseDTO> docResponseDtos = new ArrayList<>();

	/**
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {

		documentDto = new DocumentRequestDTO("POA", "address", "ENG");
		// "59276903416082",
		docJson = "{\"id\": \"mosip.pre-registration.document.upload\",\"version\" : \"1.0\","
				+ "\"requesttime\" : \"2018-12-28T05:23:08.019Z\",\"request\" :" + "{\"docCatCode\" "
				+ ": \"POA\",\"docTypCode\" : \"address\",\"langCode\":\"ENG\"}}";

		ObjectMapper mapper = new ObjectMapper();

		response = new HashMap<String, String>();
		response.put("DocumentId", "1");
		response.put("Status", "Pending_Appoinment");
		documentId = response.get("DocumentId");
		flag = true;

		List responseCopyList = new ArrayList<>();
		responseCopyList.add(DocumentStatusMessages.DOCUMENT_UPLOAD_SUCCESSFUL);
		responseCopy.setResponse(responseCopyList);

		List responseDeleteList = new ArrayList<>();
		responseCopyList.add(DocumentStatusMessages.DOCUMENT_DELETE_SUCCESSFUL);
		responseDelete.setResponse(responseDeleteList);

		DocumentResponseDTO responseDto = new DocumentResponseDTO();
		responseDto.setDocCatCode("POA");
		responseDto.setDocumentId("12345");
		responseDto.setPreRegistrationId("123546987412563");

		docResponseDtos.add(responseDto);
		responseMain.setResponse(docResponseDtos);
	}

	@WithUserDetails("individual")
	@Test
	public void successFileupload() throws Exception {
		String preRegistrationId = "123546987412563";
		MockMultipartFile jsonMultiPart = new MockMultipartFile("Document request", "docJson", "application/json",
				docJson.getBytes());

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("Doc.pdf").getFile());
		mockMultipartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));

		Mockito.when(service.uploadDocument(mockMultipartFile, docJson, preRegistrationId)).thenReturn(responseMain);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.multipart("/documents/{preRegistrationId}", preRegistrationId).file(jsonMultiPart)
				.file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	

	/**
	 * @throws Exception
	 */
	@WithUserDetails("individual")
	@Test
	public void successDelete() throws Exception {
		String preRegistrationId = "1234567847847";
		String documentId = "2ebbd74e-55e3-11e9-a7b4-b1f3d4442a79";
		Mockito.when(service.deleteDocument(documentId, preRegistrationId)).thenReturn(responseCopy);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/documents/{documentId}", documentId)
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("preRegistrationId", preRegistrationId);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 */
	@WithUserDetails("individual")
	@Test
	public void getAllDocumentforPreidTest() throws Exception {
		String preRegistrationId = "48690172097498";
		Mockito.when(service.getAllDocumentForPreId("48690172097498")).thenReturn(responseCopy);
		mockMvc.perform(
				get("/documents/{preRegistrationId}", preRegistrationId).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 */
	@WithUserDetails("individual")
	@Test
	public void deletetAllDocumentByPreidTest() throws Exception {
		String preRegistrationId = "48690172097498";
		Mockito.when(service.deleteAllByPreId("48690172097498")).thenReturn(responseDelete);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete("/documents/preregistration/{preRegistrationId}", preRegistrationId)
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 */
	@WithUserDetails("individual")
	@Test
	public void copyDocumentTest() throws Exception {
		Mockito.when(service.copyDocument("POA", "48690172097498", "1234567891")).thenReturn(responseCopy);

		String preRegistrationId = "1232462566658";
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/documents/{preRegistrationId}", preRegistrationId)
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("catCode", "POA").param("sourcePrId", "48690172097498");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 */
	@WithUserDetails("individual")
	@Test(expected = Exception.class)
	public void failureGetAllDocumentforPreidTest() throws Exception {
		Mockito.when(service.getAllDocumentForPreId("2")).thenThrow(Exception.class);
		mockMvc.perform(get("/documents").contentType(MediaType.APPLICATION_JSON_VALUE).param("preId", "2"))
				.andExpect(status().isInternalServerError());

	}

	/**
	 * @throws Exception
	 */
	@WithUserDetails("individual")
	@Test(expected = Exception.class)
	public void FailurecopyDocumentTest() throws Exception {
		Mockito.when(service.copyDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(Exception.class);

		mockMvc.perform(post("/documents/copy").contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("catCype", Mockito.anyString()).param("sourcePrId", Mockito.anyString())
				.param("destinationPreId", Mockito.anyString())).andExpect(status().isBadRequest());

	}

}
