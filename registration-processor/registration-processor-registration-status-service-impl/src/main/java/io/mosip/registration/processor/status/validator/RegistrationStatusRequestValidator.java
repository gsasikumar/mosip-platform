package io.mosip.registration.processor.status.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.status.dto.RegistrationStatusRequestDTO;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.exception.RegStatusValidationException;

/**
 * The Class RegistrationStatusRequestValidator.
 * 
 * @author Rishabh Keshari
 */
@Component
public class RegistrationStatusRequestValidator {

	/** The Constant DATETIME_TIMEZONE. */
	private static final String DATETIME_TIMEZONE = "mosip.registration.processor.timezone";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The mosip logger. */
	Logger mosipLogger = RegProcessorLogger.getLogger(RegistrationStatusRequestValidator.class);

	/** The Constant REG_STATUS_SERVICE. */
	private static final String REG_STATUS_SERVICE = "RegStatusService";

	/** The Constant REG_STATUS_APPLICATION_VERSION. */
	private static final String REG_STATUS_APPLICATION_VERSION = "mosip.registration.processor.registration.status.version";

	/** The env. */
	@Autowired
	private Environment env;

	@Value("${server.port}")
	private int port;

	@Value("${mosip.registration.processor.grace.period}")
	private int gracePeriod;

	/** The id. */
	private Map<String, String> id = new HashMap<>();

	/**
	 * Validate.
	 *
	 * @param registrationStatusRequestDTO
	 *            the registration status request DTO
	 * @param serviceId
	 *            the service id
	 * @throws RegStatusAppException
	 *             the reg status app exception
	 */
	public void validate(RegistrationStatusRequestDTO registrationStatusRequestDTO, String serviceId)
			throws RegStatusAppException {
		id.put("status", serviceId);
		validateId(registrationStatusRequestDTO.getId());
		validateVersion(registrationStatusRequestDTO.getVersion());
		validateReqTime(registrationStatusRequestDTO.getRequesttime());
	}

	/**
	 * Validate id.
	 *
	 * @param id
	 *            the id
	 * @throws RegStatusAppException
	 *             the reg status app exception
	 */
	private void validateId(String id) throws RegStatusAppException {
		RegStatusValidationException exception = new RegStatusValidationException();

		if (Objects.isNull(id)) {

			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER_ID, exception);
		} else if (!this.id.containsValue(id)) {

			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER_ID, exception);

		}
	}

	/**
	 * Validate ver.
	 *
	 * @param ver
	 *            the ver
	 * @throws RegStatusAppException
	 *             the reg status app exception
	 */
	private void validateVersion(String ver) throws RegStatusAppException {
		String version = env.getProperty(REG_STATUS_APPLICATION_VERSION);
		RegStatusValidationException exception = new RegStatusValidationException();
		if (Objects.isNull(ver)) {
			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER_VERSION, exception);

		} else if (!version.equals(ver)) {

			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER_VERSION, exception);
		}
	}

	/**
	 * Validate req time.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @throws RegStatusAppException
	 *             the reg status app exception
	 */
	private void validateReqTime(String timestamp) throws RegStatusAppException {
		RegStatusValidationException exception = new RegStatusValidationException();

		if (Objects.isNull(timestamp)) {
			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER_TIMESTAMP, exception);

		} else {
			try {
				if (Objects.nonNull(env.getProperty(DATETIME_PATTERN))) {
					DateTimeFormatterFactory timestampFormat = new DateTimeFormatterFactory(
							env.getProperty(DATETIME_PATTERN));
					timestampFormat.setTimeZone(TimeZone.getTimeZone(env.getProperty(DATETIME_TIMEZONE)));
					if (!(DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter())
							.isAfter(new DateTime().minusSeconds(gracePeriod))
							&& DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter())
									.isBefore(new DateTime().plusSeconds(gracePeriod)))) {
						throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER_TIMESTAMP,
								exception);
					}

				}
			} catch (IllegalArgumentException e) {
				mosipLogger.error(REG_STATUS_SERVICE, "IdRequestValidator", "validateReqTime",
						"\n" + ExceptionUtils.getStackTrace(e));
				throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER_TIMESTAMP,
						exception);
			}
		}
	}

}
