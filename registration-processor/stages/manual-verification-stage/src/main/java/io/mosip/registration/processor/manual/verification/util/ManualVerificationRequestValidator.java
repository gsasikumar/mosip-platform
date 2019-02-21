package io.mosip.registration.processor.manual.verification.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.manual.verification.exception.ManualVerificationAppException;
import io.mosip.registration.processor.manual.verification.exception.ManualVerificationValidationException;
import io.vertx.core.json.JsonObject;

/**
 * The Class ManualVerificationRequestValidator.
 * @author Rishabh Keshari
 */
@Component
public class ManualVerificationRequestValidator{

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant verPattern. */
	private static final Pattern verPattern = Pattern.compile("^[0-9](\\.\\d{1,1})?$");

	/** The Constant DATETIME_TIMEZONE. */
	private static final String DATETIME_TIMEZONE = "mosip.registration.processor.timezone";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The mosip logger. */
	Logger mosipLogger = RegProcessorLogger.getLogger(ManualVerificationRequestValidator.class);

	/** The Constant ID_REPO_SERVICE. */
	private static final String MAN_VERI_SERVICE = "ManualVerificationService";

	/** The env. */
	@Autowired
	private Environment env;

	/** The id. */
	//	@Resource
	private Map<String, String> id=new HashMap<>();


	/**
	 * Validate.
	 *
	 * @param obj the obj
	 * @param serviceId the service id
	 * @throws ManualVerificationAppException the manual verification app exception
	 */
	public void validate(JsonObject obj,String serviceId){
		id.put("manual", serviceId);
		validateId(obj.getString("id"));
		validateVersion(obj.getString("version"));
		validateReqTime(obj.getString("timestamp"));
	}





	/**
	 * Validate id.
	 *
	 * @param id            the id
	 * @throws ManualVerificationAppException the manual verification app exception
	 */
	private void validateId(String id) {
		ManualVerificationValidationException exception = new ManualVerificationValidationException();
		
		if (Objects.isNull(id)) {
			
			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_MVS_MISSING_INPUT_PARAMETER_ID,exception);
		} else if (!this.id.containsValue(id)) {

			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_MVS_INVALID_INPUT_PARAMETER_ID,exception);
		
		}
	}

	/**
	 * Validate ver.
	 *
	 * @param ver            the ver
	 * @throws ManualVerificationAppException the manual verification app exception
	 */
	private void validateVersion(String ver){
		ManualVerificationValidationException exception = new ManualVerificationValidationException();
		
		if (Objects.isNull(ver)) {
			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_MVS_MISSING_INPUT_PARAMETER_VERSION,exception);
			
		} else if ((!verPattern.matcher(ver).matches())) {
			
			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_MVS_INVALID_INPUT_PARAMETER_VERSION,exception);
			}
	}


	/**
	 * Validate req time.
	 *
	 * @param timestamp            the timestamp
	 * @throws ManualVerificationAppException the manual verification app exception
	 */
	private void validateReqTime(String timestamp){
		ManualVerificationValidationException exception = new ManualVerificationValidationException();
		
		if (Objects.isNull(timestamp)) {
			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_MVS_MISSING_INPUT_PARAMETER_TIMESTAMP,exception);
			
			} else {
			try {
				if (Objects.nonNull(env.getProperty(DATETIME_PATTERN))) {
					DateTimeFormatterFactory timestampFormat = new DateTimeFormatterFactory(
							env.getProperty(DATETIME_PATTERN));
					timestampFormat.setTimeZone(TimeZone.getTimeZone(env.getProperty(DATETIME_TIMEZONE)));
					if (!DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter()).isBeforeNow()) {
						throw new ManualVerificationAppException(PlatformErrorMessages.RPR_MVS_INVALID_INPUT_PARAMETER_TIMESTAMP,exception);
							}

				}
			} catch (IllegalArgumentException e) {
				mosipLogger.error(MAN_VERI_SERVICE, "ManReqRequestValidator", "validateReqTime",
						"\n" + ExceptionUtils.getStackTrace(e));
				throw new ManualVerificationAppException(PlatformErrorMessages.RPR_MVS_INVALID_INPUT_PARAMETER_TIMESTAMP,exception);
				}
		}
	}


}
