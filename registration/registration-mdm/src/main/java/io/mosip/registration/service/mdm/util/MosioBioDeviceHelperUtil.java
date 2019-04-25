package io.mosip.registration.service.mdm.util;

import java.util.Collection;

import org.springframework.stereotype.Component;

@Component
public class MosioBioDeviceHelperUtil {

	/**
	 * Checks the given collection is not empty
	 * 
	 * @param values
	 * @return
	 */
	public static boolean isListNotEmpty(Collection<?> values) {
		return values != null && !values.isEmpty();
	}
}
