package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;

/**
 * The Class CheckSumValidation.
 *
 * @author M1048358 Alok Ranjan
 */

public class CheckSumValidation {

	/** The Constant HMAC_FILE. */
	public static final String HMAC_FILE = "HMACFILE";

	/** The adapter. */
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/**
	 * Instantiates a new check sum validation.
	 *
	 * @param adapter the adapter
	 */
	public CheckSumValidation(FileSystemAdapter<InputStream, Boolean> adapter) {
		this.adapter = adapter;

	}

	/**
	 * Checksumvalidation.
	 *
	 * @param registrationId the registration id
	 * @param packetInfo the packet info
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public boolean checksumvalidation(String registrationId, PacketInfo packetInfo) throws IOException {
		HashSequence hashSequence = packetInfo.getHashSequence();

		// Getting checksum from HMAC File
		InputStream hmacFileStream = adapter.getFile(registrationId, HMAC_FILE);
		byte[] hmacFileHashByte = IOUtils.toByteArray(hmacFileStream);

		// Generating checksum using hashSequence
		CheckSumGeneration checkSumGeneration = new CheckSumGeneration(adapter);
		byte[] generatedHash = checkSumGeneration.generatePacketInfoHash(hashSequence, registrationId);

		return Arrays.equals(generatedHash, hmacFileHashByte);
	}

}
