package io.mosip.authentication.core.spi.provider.bio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import io.mosip.authentication.core.spi.provider.bio.CogentFingerprintProvider;

/**
 * @author Manoj SP
 *
 */
public class CogentFingerprintProviderTest {
	
	CogentFingerprintProvider fp = new CogentFingerprintProvider();

	/*@Test
	public void testDeviceInfo() {
		assertNull(fp.deviceInfo());
	}*/
	
	@Test
	public void testCaptureFingerprint() {
		assertFalse(fp.captureFingerprint(0, 0).isPresent());
	}
	
	@Test
	public void testSegmentFingerprint() {
		assertFalse(fp.segmentFingerprint(new byte[1]).isPresent());
	}
}
