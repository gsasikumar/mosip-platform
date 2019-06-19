package io.mosip.authentication.core.spi.bioauth.util;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MasterDataFetcher;

/**
 * The Interface DemoNormalizer is used to normalize address and name 
 *  to support the authentication effectively.
 *  
 *  @author Arun Bose S
 */
public interface DemoNormalizer {

	
	public String normalizeName(String nameInfo, String language, MasterDataFetcher titleFetcher)
			throws IdAuthenticationBusinessException;
	
	
	public String normalizeAddress(String address, String language);
}
