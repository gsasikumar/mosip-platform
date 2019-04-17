package io.mosip.authentication.core.spi.provider.bio;

import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;

/**
 * The Interface MosipFingerprintProvider.
 *
 * @author Manoj SP
 */
public interface MosipFingerprintProvider extends MosipBiometricProvider {
	
	/**
	 * Contains the fingerprint TDevice info.
	 *
	 * @return the fingerprint device info
	 */
//	FingerprintDeviceInfo deviceInfo();
	
	/**
	 * Capture fingerprint.
	 *
	 * @param quality the quality
	 * @param timeout the timeout
	 * @return Fingerprint image
	 */
	Optional<byte[]> captureFingerprint(Integer quality, Integer timeout);
	
	
	/**
	 * Segment fingerprint.
	 *
	 * @param fingerImage the finger image
	 * @return the optional
	 */
	@SuppressWarnings("rawtypes")
	Optional<Map> segmentFingerprint(byte[] fingerImage);
}