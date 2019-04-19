package io.mosip.registration.service.packet;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This class encrypts the Registration packet using RSA and AES algorithms.
 * Then saves the encrypted packet and acknowledgement receipt in the specified location.
 * And adds an entry in the {@link Registration} table
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface PacketEncryptionService {

	/**
	 * Encrypts the input data using AES algorithm followed by RSA. Stores the
	 * encrypted data in the specified location and saves the details in
	 * {@link Registration}
	 * 
	 * @param registrationDTO
	 *            the {@link RegistrationDTO} containing the registration details
	 * @param packetZipData
	 *            the data to be encrypted
	 * @return encrypted data as byte array
	 * @throws RegBaseCheckedException
	 *             the checked exception
	 */
	ResponseDTO encrypt(final RegistrationDTO registrationDTO, final byte[] packetZipData)
			throws RegBaseCheckedException;
}
