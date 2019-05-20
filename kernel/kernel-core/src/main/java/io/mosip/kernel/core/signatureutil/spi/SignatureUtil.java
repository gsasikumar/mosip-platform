package io.mosip.kernel.core.signatureutil.spi;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;

/**
 * SignatureUtil interface.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
public interface SignatureUtil {


	public SignatureResponse sign(String data, String timestamp);

	/**
	 * Validate with public key.
	 *
	 * @param signature the response signature
	 * @param data      the response body
	 * @param publickey         the base64 encoded public key string
	 * @return true, if successful
	 * @throws InvalidKeySpecException  the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public boolean validateWithPublicKey(String signature, String data, String publickey)
			throws InvalidKeySpecException, NoSuchAlgorithmException;

	/**
	 * Validate with public key.
	 *
	 * @param signature the response signature
	 * @param data      the response body
	 * @param timestamp the sign timestamp
	 * @return true, if successful
	 * @throws InvalidKeySpecException  the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public boolean validate(String signature, String data, String timestamp)
			throws InvalidKeySpecException, NoSuchAlgorithmException;
	
	/**
	 * Sign response.
	 *
	 * @param response
	 *            the response
	 * @return the string
	 */
	public SignatureResponse signResponseByCertificate(String response);

	
	/**
	 * Validate
	 *
	 * @param responseSignature
	 *            the response signature
	 * @param responseBody
	 *            the response body
	 * @return true, if successful
	 * @throws InvalidKeySpecException
	 *             the invalid key spec exception
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 */
	public boolean validateWithCertificate(String responseSignature, String responseBody,LocalDateTime responseTime) throws InvalidKeySpecException, NoSuchAlgorithmException;
}
