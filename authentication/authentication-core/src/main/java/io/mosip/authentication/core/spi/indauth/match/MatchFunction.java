package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * @author Dinesh Karuppiah
 */

@FunctionalInterface
public interface MatchFunction {

	/**
	 * Match Function
	 * 
	 * @param reqValues
	 * @param entityValues
	 * @param matchProperties
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */

	int match(Object reqValues, Object entityValues, Map<String, Object> matchProperties)
			throws IdAuthenticationBusinessException;

}
