package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.service.integration.KeyManager;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;

/**
 * The Class BaseAuthFilter - The Base Auth Filter that does all necessary
 * authentication/authorization before allowing the request to the respective
 * controllers.
 * 
 * @author Manoj SP
 * @author Sanjay Murali
 */
@Component
public abstract class BaseAuthFilter implements Filter {

	private static final String RES_TIME = "resTime";

	private static final String DATETIME_PATTERN = "datetime.pattern";

	private static final String REQ_TIME = "reqTime";

	private static final String DEFAULT_VERSION = "v1.0";

	/** The env. */
	@Autowired
	protected Environment env;

	@Autowired
	protected EncryptorImpl encryptor;

	@Autowired
	protected KeyManager keyManager;

	/** The Constant BASE_AUTH_FILTER. */
	private static final String BASE_AUTH_FILTER = "BaseAuthFilter";

	/** The Constant EVENT_FILTER. */
	private static final String EVENT_FILTER = "Event_filter";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionId";

	/** The mapper. */
	@Autowired
	protected ObjectMapper mapper;

	/** The Constant EMPTY_JSON_OBJ_STRING. */
	private static final String EMPTY_JSON_OBJ_STRING = "{";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(BaseAuthFilter.class);

	/** The request time. */
	private String requestTime;

	/** The Constant MOSIP_TSP_ORGANIZATION. */
	private static final String MOSIP_TSP_ORGANIZATION = "mosip.jws.certificate.organization";

	/** The Constant MOSIP_JWS_CERTIFICATE_ALGO. */
	private static final String MOSIP_JWS_CERTIFICATE_ALGO = "mosip.jws.certificate.algo";

	protected PublicKey publicKey;

	/** The Constant FULL_ADDRESS. */
	private static final String FULL_ADDRESS = "fullAddress";

	/** The Constant PERSONAL_IDENTITY. */
	private static final String PERSONAL_IDENTITY = "personalIdentity";
	
	/** The Constant ADDRESS. */
	private static final String ADDRESS = "address";
	
	/** The Constant BIO_INFOS. */
	private static final String BIO_INFOS = "bioInfos";

	/** The Constant BIO. */
	private static final String BIO = "bio";

	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "authType";

	/** The Constant INFO. */
	private static final String INFO = "info";

	/** The Constant MATCH_INFOS. */
	private static final String MATCH_INFOS = "matchInfos";

	/** The Constant TXN_ID. */
	private static final String TXN_ID = "txnID";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(filterConfig.getServletContext());
		env = context.getBean(Environment.class);
		mapper = context.getBean(ObjectMapper.class);
		encryptor = context.getBean(EncryptorImpl.class);
		keyManager = context.getBean(KeyManager.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String ver = null;
		if (request instanceof HttpServletRequest) {
			String url = ((HttpServletRequest) request).getRequestURL().toString();
			String context = ((HttpServletRequest) request).getContextPath();

			ver = getVersionFromUrl(url, context);
		}

		requestTime = DateUtils.formatDate(new Date(), env.getProperty(DATETIME_PATTERN));
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Request received at : " + requestTime);
		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(
				(HttpServletRequest) request);
		String signature = requestWrapper.getHeader("Authorization");// FIXME header name
		CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
		byte[] requestAsByte = IOUtils.toByteArray(requestWrapper.getInputStream());
		logSize(new String(requestAsByte));
		requestWrapper.resetInputStream();
		try {
			ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
			Map<String, Object> requestBody = getRequestBody(requestWrapper.getInputStream());
			if (!validateSignature(signature, requestAsByte)) {
				mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Invalid Signature");
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_SIGNATURE.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_SIGNATURE.getErrorMessage());
			}
			Map<String, Object> decodedRequest = decodedRequest(requestBody);
			mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
					"Input Request: \n" + objectWriter.writeValueAsString(decodedRequest));
			requestWrapper.resetInputStream();

			requestWrapper.replaceData(objectWriter.writeValueAsString(decodedRequest).getBytes());
			requestWrapper.resetInputStream();

			responseWrapper = new CharResponseWrapper((HttpServletResponse) response);

			chain.doFilter(requestWrapper, responseWrapper);

			requestWrapper.resetInputStream();

			Map<String, Object> responseMap = setResponseParam(getRequestBody(requestWrapper.getInputStream()),
					getResponseBody(responseWrapper.toString()));
			responseMap.put("ver", ver);
			response.getWriter().write(mapper.writeValueAsString(encodedResponse(responseMap)));

			logResponseTime((String) getResponseBody(responseWrapper.toString()).get(RES_TIME));
		} catch (IdAuthenticationAppException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "\n" + ExceptionUtils.getStackTrace(e));
			requestWrapper.resetInputStream();
			responseWrapper = sendErrorResponse(response, chain, requestWrapper);
		} finally {
			logSize(responseWrapper.toString());
		}
	}

	private void logSize(String data) {
		double size = ((double) data.length()) / 1024;
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Data size : " + ((size > 0) ? size : 1) + " kb");
	}

	/**
	 * Gets the request body.
	 *
	 * @param inputStream
	 *            the input stream
	 * @return the request body
	 * @throws IdAuthenticationAppException
	 *             - the id authentication app exception
	 */
	private Map<String, Object> getRequestBody(InputStream inputStream) throws IdAuthenticationAppException {
		try {
			return mapper.readValue(IOUtils.toString(inputStream, Charset.defaultCharset()),
					new TypeReference<Map<String, Object>>() {
					});
		} catch (IOException | ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Gets the response body.
	 *
	 * @param output
	 *            the output
	 * @return the response body
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getResponseBody(String output) throws IdAuthenticationAppException {
		try {
			return mapper.readValue(output, Map.class);
		} catch (IOException | ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Encode.
	 *
	 * @param stringToEncode
	 *            the string to encode
	 * @return the string
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected String encode(String stringToEncode) throws IdAuthenticationAppException {
		try {
			if (stringToEncode != null) {
				return Base64.encodeBase64String(stringToEncode.getBytes());
			} else {
				return stringToEncode;
			}
		} catch (IllegalArgumentException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Decode.
	 *
	 * @param stringToDecode
	 *            the string to decode
	 * @return the object
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected Object decode(String stringToDecode) throws IdAuthenticationAppException {
		try {
			if (stringToDecode != null) {
				// return Base64.getDecoder().decode(stringToDecode);
				return Base64.decodeBase64(stringToDecode);
			} else {
				return stringToDecode;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Log response time.
	 *
	 * @param responseTime
	 *            the response time
	 */
	private void logResponseTime(String responseTime) {
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Response sent at : " + responseTime);
		long duration = Duration.between(
				LocalDateTime.parse(requestTime, DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN))),
				LocalDateTime.parse(responseTime, DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN))))
				.toMillis();
		mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
				"Time difference between request and response in millis:" + duration
						+ ".  Time difference between request and response in Seconds: " + (duration / 1000));
	}

	/**
	 * Send error response.
	 *
	 * @param response
	 *            the response
	 * @param chain
	 *            the chain
	 * @param requestWrapper
	 *            the request wrapper
	 * @return the char response wrapper
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IdAuthenticationAppException
	 */
	private CharResponseWrapper sendErrorResponse(ServletResponse response, FilterChain chain,
			ResettableStreamHttpServletRequest requestWrapper) throws IOException, ServletException {
		Map<String, Object> requestMap = null;
		CharResponseWrapper responseWrapper = null;
		try {
			requestMap = getRequestBody(requestWrapper.getInputStream());
			requestWrapper.resetInputStream();
		} catch (IdAuthenticationAppException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
					"Cannot log time \n" + ExceptionUtils.getStackTrace(e));
		}
		responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
		requestWrapper.replaceData(EMPTY_JSON_OBJ_STRING.getBytes());
		chain.doFilter(requestWrapper, responseWrapper);
		try {
			Map<String, Object> responseMap = getResponseBody(responseWrapper.toString());
			if (Objects.nonNull(requestMap) && Objects.nonNull(requestMap.get(REQ_TIME))
					&& isDate((String) requestMap.get(REQ_TIME))) {
				ZoneId zone = ZonedDateTime
						.parse((CharSequence) requestMap.get(REQ_TIME), DateTimeFormatter.ISO_ZONED_DATE_TIME)
						.getZone();
				responseMap.replace(RES_TIME,
						DateUtils.formatDate(
								DateUtils.parseToDate((String) responseMap.get(RES_TIME),
										env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)),
								env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)));
			}
			response.getWriter().write(mapper.writeValueAsString(setResponseParam(requestMap, responseMap)));
			logResponseTime((String) getResponseBody(responseWrapper.toString()).get(RES_TIME));
		} catch (IdAuthenticationAppException e1) {
			String responseTime = mapper.convertValue(new Date(), String.class);
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
					"Cannot log time \n" + ExceptionUtils.getStackTrace(e1));
			long duration = Duration.between(
					LocalDateTime.parse(requestTime, DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN))),
					LocalDateTime.parse(responseTime, DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN))))
					.toMillis();
			mosipLogger.info(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER,
					"Cannot log time. Response sent at : " + responseTime + ". Time taken in millis: " + duration
							+ ". Time taken in seconds: " + (duration / 1000));
		}
		return responseWrapper;
	}

	/**
	 * To validate a string whether its a date or not
	 * 
	 * @param date
	 * @return
	 */
	protected boolean isDate(String date) {
		try {
			DateUtils.parseToDate(date, env.getProperty(DATETIME_PATTERN));
			return true;
		} catch (ParseException | java.text.ParseException e) {
			mosipLogger.error("sessionId", BASE_AUTH_FILTER, "validateDate", "\n" + ExceptionUtils.getStackTrace(e));
		}
		return false;
	}

	/**
	 * Decoded request.
	 *
	 * @param requestBody
	 *            the request body
	 * @return the map
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected abstract Map<String, Object> decodedRequest(Map<String, Object> requestBody)
			throws IdAuthenticationAppException;

	/**
	 * Encoded response.
	 *
	 * @param responseBody
	 *            the response body
	 * @return the map
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected abstract Map<String, Object> encodedResponse(Map<String, Object> responseBody)
			throws IdAuthenticationAppException;

	/**
	 * Sets the txn id.
	 *
	 * @param requestBody
	 *            the request body
	 * @param responseBody
	 *            the response body
	 * @return the map
	 * @throws IdAuthenticationAppException
	 */
	protected abstract Map<String, Object> setResponseParam(Map<String, Object> requestBody,
			Map<String, Object> responseBody) throws IdAuthenticationAppException;

	/**
	 * Get version of url.
	 * 
	 * @param url
	 *            url
	 * @param context
	 *            context-path
	 * @return version of url
	 */
	protected String getVersionFromUrl(String url, String context) {
		String ver = null;

		if ((url != null && !url.isEmpty()) && (context != null && !context.isEmpty())) {
			String[] splitedUrlByContext = url.split(context);
			String versionStr = Arrays.stream(splitedUrlByContext[1].split("/")).filter(s -> !s.isEmpty()).findFirst()
					.orElse(DEFAULT_VERSION);
			ver = versionStr.replaceAll("[\\s+a-zA-Z]", "");
		}

		return ver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {

	}

	/**
	 * Validate signature.
	 *
	 * @param signature
	 *            the signature
	 * @param requestAsByte
	 *            the request as byte
	 * @return true, if successful
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		boolean isSigned = false;
		JsonWebSignature jws = new JsonWebSignature();
		try {
			jws.setCompactSerialization(signature);
			List<X509Certificate> certificateChainHeaderValue = jws.getCertificateChainHeaderValue();
			if (certificateChainHeaderValue.size() == NumberUtils.INTEGER_ONE
					&& jws.getAlgorithmHeaderValue().equals(env.getProperty(MOSIP_JWS_CERTIFICATE_ALGO))) {
				X509Certificate certificate = certificateChainHeaderValue.get(0);
				certificate.checkValidity();
				publicKey = certificate.getPublicKey();
				certificate.verify(publicKey);
				jws.setKey(publicKey);
				isSigned = checkValidSign(requestAsByte, isSigned, certificate, jws);
			} else {
				mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "certificate not present");
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.INVALID_CERTIFICATE.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_CERTIFICATE.getErrorMessage());
			}
		} catch (JoseException | InvalidKeyException | CertificateException | NoSuchAlgorithmException
				| NoSuchProviderException | SignatureException e) {
			mosipLogger.error(SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Invalid certificate");
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_CERTIFICATE.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_CERTIFICATE.getErrorMessage());
		}
		return isSigned;
	}

	/**
	 * Check valid sign.
	 *
	 * @param requestAsByte
	 *            the request as byte
	 * @param isSigned
	 *            the is signed
	 * @param certificate
	 *            the certificate
	 * @param jws
	 *            the jws
	 * @return true, if successful
	 * @throws JoseException
	 *             the jose exception
	 */
	private boolean checkValidSign(byte[] requestAsByte, boolean isSigned, X509Certificate certificate,
			JsonWebSignature jws) throws JoseException {
		if (jws.verifySignature() && validateOrg(certificate) && jws.getPayload()
				.equalsIgnoreCase(HMACUtils.digestAsPlainText(HMACUtils.generateHash((requestAsByte))))) {
			isSigned = true;
		}
		return isSigned;
	}

	/**
	 * Validate org.
	 *
	 * @param certNew
	 *            the cert new
	 * @return true, if successful
	 */
	private boolean validateOrg(X509Certificate certNew) {
		String[] subject = certNew.getSubjectDN().getName().split(",");
		return Stream.of(subject).map(s -> s.split("=")).filter(ar -> ar.length == 2)
				.filter(ar -> ar[0].trim().equals("O"))
				.anyMatch(ar -> ar[1].trim().equals(env.getProperty(MOSIP_TSP_ORGANIZATION)));
	}
	
	/**
	 * Check demo enabled auth type.
	 *
	 * @param authType the auth type
	 * @return true, if successful
	 */
	protected boolean checkDemoEnabledAuthType(Map<String, Object> authType) {
		return (authType.get(PERSONAL_IDENTITY) instanceof Boolean && (boolean) authType.get(PERSONAL_IDENTITY))
				|| (authType.get(FULL_ADDRESS) instanceof Boolean && (boolean) authType.get(FULL_ADDRESS))
				|| (authType.get(ADDRESS) instanceof Boolean && (boolean) authType.get(ADDRESS));
	}
	
	/**
	 * Sets the auth response param.
	 *
	 * @param requestBody the request body
	 * @param responseBody the response body
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> setAuthResponseParam(Map<String, Object> requestBody, Map<String, Object> responseBody) {
		try {
			if (null != responseBody.get(INFO)) {
				Map<String, Object> authType = (Map<String, Object>) requestBody.get(AUTH_TYPE);
				if (!checkDemoEnabledAuthType(authType)) {
					Map<String, Object> info = (Map<String, Object>) responseBody.get(INFO);
					info.remove(MATCH_INFOS);
					responseBody.replace(INFO, info);
				}
				if (!(authType.get(BIO) instanceof Boolean) || !(boolean) authType.get(BIO)) {
					Map<String, Object> info = (Map<String, Object>) responseBody.get(INFO);
					info.remove(BIO_INFOS);
					responseBody.replace(INFO, info);
				} 
			}			
			if (Objects.nonNull(requestBody) && Objects.nonNull(requestBody.get(TXN_ID))) {
				responseBody.replace(TXN_ID, requestBody.get(TXN_ID));
			}

			if (Objects.nonNull(requestBody) && Objects.nonNull(requestBody.get(REQ_TIME))
					&& isDate((String) requestBody.get(REQ_TIME))) {
				ZoneId zone = ZonedDateTime.parse((CharSequence) requestBody.get(REQ_TIME)).getZone();
				responseBody.replace(RES_TIME,
						DateUtils.formatDate(
								DateUtils.parseToDate((String) responseBody.get(RES_TIME),
										env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)),
								env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)));
				return responseBody;
			} else {
				return responseBody;
			}
		} catch (DateTimeParseException e) {
			mosipLogger.error("sessionId", "IdAuthFilter", "setResponseParam", "\n" + ExceptionUtils.getStackTrace(e));
			return responseBody;
		}
	}

}
