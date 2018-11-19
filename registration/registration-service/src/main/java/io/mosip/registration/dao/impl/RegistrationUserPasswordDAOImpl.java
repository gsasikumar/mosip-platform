package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.RegistrationUserPasswordDAO;
import io.mosip.registration.entity.RegistrationUserPassword;
import io.mosip.registration.repositories.RegistrationUserPasswordRepository;

/**
 * The implementation class of {@link RegistrationUserPasswordDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationUserPasswordDAOImpl implements RegistrationUserPasswordDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationUserPasswordDAOImpl.class);

	/** The registrationUserPassword repository. */
	@Autowired
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationUserPasswordDAO#getPassword(java.lang.
	 * String,java.lang.String)
	 */
	public boolean getPassword(String userId, String hashPassword) {

		LOGGER.debug("REGISTRATION - USER_CREDENTIALS - REGISTRATION_USER_PASSWORD_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID, "Fetching User credentials");

		List<RegistrationUserPassword> registrationUserPwd = registrationUserPasswordRepository
				.findByRegistrationUserPasswordIdUsrIdAndIsActiveTrue(userId);

		String userData = !registrationUserPwd.isEmpty() ? registrationUserPwd.get(0).getPwd() : null;

		LOGGER.debug("REGISTRATION - USER_CREDENTIALS - REGISTRATION_USER_PASSWORD_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID,
				"User credentials fetched successfully");

		return userData != null && hashPassword.equals(userData);
	}

}
