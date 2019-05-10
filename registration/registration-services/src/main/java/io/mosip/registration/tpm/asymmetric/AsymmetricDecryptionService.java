package io.mosip.registration.tpm.asymmetric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;

import tss.Tpm;
import tss.tpm.TPMS_NULL_ASYM_SCHEME;

/**
 * Class for decrypting the encrypted data using asymmetric cryto-alogirthm
 * in TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class AsymmetricDecryptionService {

	private static final Logger LOGGER = AppConfig.getLogger(AsymmetricDecryptionService.class);
	@Autowired
	private AsymmetricKeyCreationService asymmetricKeyCreationService;

	/**
	 * Decrypts the encrypted data using the {@link Tpm} instance
	 * 
	 * @param tpm
	 *            the instance of the {@link Tpm}
	 * @param encryptedData
	 *            the encrypted data
	 * @return the byte array of decrypted data
	 */
	public byte[] decryptUsingTPM(Tpm tpm, byte[] encryptedData) {
		LOGGER.info(LoggerConstants.TPM_ASYM_DECRYPTION, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Decrypting the data by asymmetric algorithm using TPM");

		return new String(tpm.RSA_Decrypt(asymmetricKeyCreationService.createPersistentKey(tpm), encryptedData,
				new TPMS_NULL_ASYM_SCHEME(), RegistrationConstants.NULL_VECTOR)).trim().getBytes();
	}

}