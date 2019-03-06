package io.mosip.registration.test.service;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dao.ValidDocumentDAO;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.service.impl.ValidDocumentServiceImpl;

public class ValidDocumentServiceImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private ValidDocumentDAO validDocumentDAO;

	@Mock
	private MasterSyncDao masterSyncDao;

	@InjectMocks
	private ValidDocumentServiceImpl validDocumentServiceImpl;

	@Test
	public void getDocumentCategoriesTest() {

		List<ValidDocument> validDocuments = new ArrayList<>();
		ValidDocument validDocument = new ValidDocument();

		validDocuments.add(validDocument);

		List<String> list = new ArrayList<>();
		list.add("DocType");

		List<DocumentType> documentTypes = new ArrayList<>();
		DocumentType documentType = new DocumentType();
		documentType.setCode("DocType");
		documentType.setLangCode("eng");
		documentType.setName("Passport");
		documentTypes.add(documentType);

		Mockito.when(validDocumentDAO.getValidDocuments("007", "POA", "eng")).thenReturn(validDocuments);
		Mockito.when(masterSyncDao.getDocumentTypes(list, "eng")).thenReturn(documentTypes);

		assertNotNull(validDocumentServiceImpl.getDocumentCategories("007", "POA", "eng"));
	}

}
