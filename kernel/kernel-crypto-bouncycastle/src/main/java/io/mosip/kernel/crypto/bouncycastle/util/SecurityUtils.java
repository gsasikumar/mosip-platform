/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.bouncycastle.util;

import java.io.IOException;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import io.mosip.kernel.crypto.bouncycastle.constant.MosipSecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.bouncycastle.constant.MosipSecurityMethod;
import io.mosip.kernel.crypto.bouncycastle.exception.MosipInvalidDataException;
import io.mosip.kernel.crypto.bouncycastle.exception.MosipInvalidKeyException;
import io.mosip.kernel.crypto.bouncycastle.exception.MosipNullDataException;
import io.mosip.kernel.crypto.bouncycastle.exception.MosipNullKeyException;
import io.mosip.kernel.crypto.bouncycastle.exception.MosipNullMethodException;


/**
 * Utility class for security
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class SecurityUtils {

	/**
	 * Constructor for this class
	 */
	private SecurityUtils() {

	}

	/**
	 * {@link AsymmetricKeyParameter} from encoded private key
	 * 
	 * @param privateKey
	 *            private Key for processing
	 * @return {@link AsymmetricKeyParameter} from encoded private key '
	 */
	public static AsymmetricKeyParameter bytesToPrivateKey(byte[] privateKey) {
		AsymmetricKeyParameter keyParameter = null;
		try {
			keyParameter = PrivateKeyFactory.createKey(privateKey);
		} catch (NullPointerException e) {
			throw new MosipNullKeyException(
					MosipSecurityExceptionCodeConstant.MOSIP_NULL_KEY_EXCEPTION);
		} catch (ClassCastException e) {
			throw new MosipInvalidKeyException(
					MosipSecurityExceptionCodeConstant.MOSIP_INVALID_ASYMMETRIC_PRIVATE_KEY_EXCEPTION);
		} catch (IOException e) {
			throw new MosipInvalidKeyException(
					MosipSecurityExceptionCodeConstant.MOSIP_INVALID_KEY_CORRUPT_EXCEPTION);
		}
		return keyParameter;
	}

	/**
	 * {@link AsymmetricKeyParameter} from encoded public key
	 * 
	 * @param publicKey
	 *            private Key for processing
	 * @return {@link AsymmetricKeyParameter} from encoded public key
	 */
	public static AsymmetricKeyParameter bytesToPublicKey(byte[] publicKey) {
		AsymmetricKeyParameter keyParameter = null;
		try {
			keyParameter = PublicKeyFactory.createKey(publicKey);
		} catch (NullPointerException e) {
			throw new MosipNullKeyException(
					MosipSecurityExceptionCodeConstant.MOSIP_NULL_KEY_EXCEPTION);
		} catch (IllegalArgumentException e) {
			throw new MosipInvalidKeyException(
					MosipSecurityExceptionCodeConstant.MOSIP_INVALID_ASYMMETRIC_PUBLIC_KEY_EXCEPTION);
		} catch (IOException e) {
			throw new MosipInvalidKeyException(
					MosipSecurityExceptionCodeConstant.MOSIP_INVALID_KEY_CORRUPT_EXCEPTION);
		}
		return keyParameter;
	}

	/**
	 * This method verifies mosip security method
	 * 
	 * @param mosipSecurityMethod
	 *            mosipSecurityMethod given by user
	 */
	public static void checkMethod(MosipSecurityMethod mosipSecurityMethod) {
		if (mosipSecurityMethod == null) {
			throw new MosipNullMethodException(
					MosipSecurityExceptionCodeConstant.MOSIP_NULL_METHOD_EXCEPTION);
		}
	}
	
	/**
	 * Verify if data is null or empty
	 * 
	 * @param data
	 *            data provided by user
	 */
	public static void verifyData(byte[] data) {
		if (data == null) {
			throw new MosipNullDataException(
					MosipSecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION);
		} else if (data.length == 0) {
			throw new MosipInvalidDataException(
					MosipSecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION);
		}
	}
}
