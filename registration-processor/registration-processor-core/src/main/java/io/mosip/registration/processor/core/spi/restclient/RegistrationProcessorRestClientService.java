package io.mosip.registration.processor.core.spi.restclient;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.RestUriConstant;

/**
 * The Interface RegistrationProcessorRestClientService.
 *
 * @param <T> the generic type
 * 
 * @author Rishabh Keshari
 */
public interface RegistrationProcessorRestClientService<T> {

	/**
	 * Gets the api.
	 *
	 * @param apiName the api name
	 * @param uri the uri
	 * @param queryParam the query param
	 * @param queryParamValue the query param value
	 * @param responseType the response type
	 * @return the api
	 */
	public T getApi(ApiName apiName, RestUriConstant uri, String queryParam, String queryParamValue, Class<?> responseType);
	
	
	/**
	 * Post api.
	 *
	 * @param apiName the api name
	 * @param url the url
	 * @param queryParam the query param
	 * @param queryParamValue the query param value
	 * @param requestedData the requested data
	 * @param responseType the response type
	 * @return the t
	 */
	public T postApi(ApiName apiName, RestUriConstant url, String queryParam, String queryParamValue,T requestedData, Class<?> responseType);


}
