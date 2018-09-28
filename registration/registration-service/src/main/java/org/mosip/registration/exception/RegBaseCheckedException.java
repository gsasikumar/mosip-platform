package org.mosip.registration.exception;

import org.mosip.kernel.core.exception.BaseCheckedException;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * The class to handle all the checked exception in REG
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class RegBaseCheckedException extends BaseCheckedException {

	/**
	 * Serializable Version Id
	 */
	private static final long serialVersionUID = 1287307733283461272L;
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Constructs a new checked exception
	 */
	public RegBaseCheckedException() {
		super();
	}

	/**
	 * Constructs a new checked exception with the specified detail message and
	 * error code.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the detail message.
	 */
	public RegBaseCheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		LOGGER.debug("REGISTRATION - CHECKED_EXCEPTION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), errorCode + "-->" + errorMessage);
	}

	/**
	 * Constructs a new checked exception with the specified detail message and
	 * error code.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the detail message
	 * @param throwable
	 *            the specified cause
	 */
	public RegBaseCheckedException(String errorCode, String errorMessage, Throwable throwable) {
		super(errorCode, errorMessage, throwable);
		LOGGER.debug("REGISTRATION - CHECKED_EXCEPTION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), errorCode + "-->" + errorMessage);
	}
}
