package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dao.DocumentFormatDAO;
import io.mosip.registration.entity.DocumentFormat;
import io.mosip.registration.repositories.DocumentFormatRepository;
import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * implementation class of {@link DocumentFormatDAO}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class DocumentFormatDAOImpl implements DocumentFormatDAO {
	/** instance of {@link DocumentFormatRepository} */
	@Autowired
	private DocumentFormatRepository documentFormatRepository;
	/** instance of {@link MosipLogger} */

	private static MosipLogger LOGGER;

	/**
	 * Initialize the logger
	 * 
	 * @param mosipRollingFileAppender
	 */
	@Autowired
	public void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * (non-javadoc)
	 * 
	 * @see io.mosip.registration.dao.DocumentFormatDAO#getDocumentFormats()
	 */

	@Override
	public List<DocumentFormat> getDocumentFormats() {
		LOGGER.debug("REGISTRATION-PACKET_CREATION-DOCUMENTFORMATDAO", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "fetching the documentformats");

		return documentFormatRepository.findAll();
	}

}
