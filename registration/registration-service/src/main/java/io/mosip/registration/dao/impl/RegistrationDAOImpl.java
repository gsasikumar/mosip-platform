package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.registration.constants.RegClientStatusCode;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.constants.RegTranType;
import io.mosip.registration.constants.RegType;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;

/**
 * The implementation class of {@link RegistrationDAO}.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Repository
@Transactional
public class RegistrationDAOImpl implements RegistrationDAO {

	/** The registration repository. */
	@Autowired
	private RegistrationRepository registrationRepository;
	
	/** Object for Logger. */
	private static MosipLogger LOGGER;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.RegistrationDAO#save(java.lang.String, java.lang.String)
	 */
	@Override
	public void save(String zipFileName, String individualName) throws RegBaseCheckedException {
		try {
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - REGISTRATION_DAO", 
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
					"Packet encryption had been ended");

			OffsetDateTime time = OffsetDateTime.now();

			Registration registration = new Registration();
			registration.setId(zipFileName.substring(zipFileName.lastIndexOf(File.separator) + 1));

			registration.setRegType(RegType.NEW.getCode());
			registration.setRefRegId("12345");
			registration.setStatusCode(RegClientStatusCode.CREATED.getCode());
			registration.setLangCode("ENG");
			registration.setStatusTimestamp(time);
			registration.setAckFilename(zipFileName + "_Ack."+RegConstants.IMAGE_FORMAT);
			registration.setClientStatusCode(RegClientStatusCode.CREATED.getCode());
			// TODO: Get from Session Context - RegUsr, Reg Center
			registration.setIndividualName(individualName);

			registration.setIsActive(true);
			registration.setCrBy("mosip");
			registration.setCrDtime(time);
			
			List<RegistrationTransaction> registrationTransactions = new ArrayList<>();
			RegistrationTransaction registrationTxn = new RegistrationTransaction();
			registrationTxn.setTrnTypeCode(RegTranType.CREATED.getCode());
			registrationTxn.setLangCode("ENG");
			registrationTxn.setIsActive(true);
			registrationTxn.setStatusCode(RegClientStatusCode.CREATED.getCode());
			registrationTxn.setCrBy("mosip");
			registrationTxn.setCrDtime(time);
			registrationTransactions.add(registrationTxn);
			registration.setRegistrationTransaction(registrationTransactions);

			registrationRepository.create(registration);
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - REGISTRATION_DAO", 
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
					"Packet encryption had been ended");
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.CREATE_PACKET_ENTITY,
					runtimeException.toString());
		}
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.RegistrationDAO#updateStatus(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Registration updateStatus(String id, String clientStatusCode, String approverUsrId,
			String statusComments, String updBy) {
		OffsetDateTime timestamp = OffsetDateTime.now();
		Registration registration = registrationRepository.getOne(id);

		registration.setClientStatusCode(clientStatusCode);
		registration.setStatusTimestamp(timestamp);
		registration.setApproverUsrId(approverUsrId);
		registration.setStatusComment(statusComments);
		registration.setUpdBy(updBy);
		registration.setUpdDtimes(timestamp);
		List<RegistrationTransaction> registrationTransaction = registration.getRegistrationTransaction();
		RegistrationTransaction registrationTxn = new RegistrationTransaction();
		registrationTxn.setTrnTypeCode(RegTranType.UPDATED.getCode());
		registrationTxn.setLangCode("ENG");
		registrationTxn.setIsActive(true);
		registrationTxn.setStatusCode(clientStatusCode);
		registrationTxn.setStatusComment(statusComments);
		registrationTxn.setCrBy(updBy);
		registrationTxn.setCrDtime(timestamp);
		registrationTransaction.add(registrationTxn);

		return registrationRepository.update(registration);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.RegistrationDAO#getEnrollmentByStatus(java.lang.String)
	 */
	@Override
	public List<Registration> getEnrollmentByStatus(String status) {
		return registrationRepository.findByclientStatusCode(status);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.RegistrationDAO#approvalList()
	 */
	@Override
	public List<Registration> approvalList() {
		return registrationRepository.findByclientStatusCode("R");
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.RegistrationDAO#upload(java.lang.Object)
	 */
	@Override
	public boolean upload(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.RegistrationDAO#view(java.lang.String)
	 */
	@Override
	public List<String> view(String zipFileName) {
		// TODO Auto-generated method stub
		return null;
	}

}
