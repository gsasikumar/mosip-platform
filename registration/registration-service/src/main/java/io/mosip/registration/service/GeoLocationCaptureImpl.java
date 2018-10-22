package io.mosip.registration.service;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * The {@code GeoLocationCaptureImpl) for capturing geo location of machine.
 */
@Service
public class GeoLocationCaptureImpl implements GeoLocationCapture {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender
	 *            the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.GeoLocationCapture#getLatLongDtls()
	 */
	@Override
	public Map<String, Object> getLatLongDtls() {
		LOGGER.debug("REGISTRATION - GEO-CAPTURE - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Geo location capture for machine has been started");

		Map<String, Object> map = new HashMap<>();
		BigDecimal deviceLatitute = new BigDecimal("12.99194");
		BigDecimal deviceLongitude = new BigDecimal("80.2471");
		BigDecimal latlong = new BigDecimal("0.0");
		if ((latlong.compareTo(deviceLatitute) != 0) && (latlong.compareTo(deviceLongitude) != 0)) {
			map.put("latitude", deviceLatitute.doubleValue());
			map.put("longitude", deviceLongitude.doubleValue());
			map.put("errorMessage", "success");
		} else {
			map.put("latitude", deviceLatitute.doubleValue());
			map.put("longitude", deviceLongitude.doubleValue());
			map.put("errorMessage", "failure");
		}

		LOGGER.debug("REGISTRATION - GEO-CAPTURE - REGISTRATION", APPLICATION_NAME, APPLICATION_ID,
				"Geo location capture for machine has been started");
		return map;
	}

}
