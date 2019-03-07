package io.mosip.registration.processor.packet.service.util.encryptor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.SecretKey;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.service.dto.PublicKeyResponseDto;
import io.mosip.registration.processor.packet.service.exception.EncryptorBaseCheckedException;

@Component
public class EncryptorUtil {

	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

	/** The encryptor. */
	@Autowired
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/** The Constant RSA. */
	public static final String RSA = "RSA";

	/** The Constant AES_KEY_CIPHER_SPLITTER. */
	public static final String AES_KEY_CIPHER_SPLITTER = "#KEY_SPLITTER#";

	/** The Constant APPLICATION_ID. */
	public static final String APPLICATION_ID = "REGISTRATION";

	/** The registration processor rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;
	

	@Value("${mosip.kernel.rid.centerid-length}")
	private int centerIdLength;	

	
	
	
	
	public void encryptUinUpdatePacket(InputStream decryptedFile,String regId) throws IOException, ApisResourceAccessException, InvalidKeySpecException, JSONException, NoSuchAlgorithmException {
		try (InputStream decryptedPacketStream = new BufferedInputStream(decryptedFile);
				InputStream encryptPacketStream = encrypt(decryptedPacketStream,regId)) {// close input stream
			
			
			File targetFile =new File("C:\\Users\\M1049387\\Desktop\\encrypted\\10031100110000220190307115748.zip");
			
			if(!(targetFile.exists())) {
				targetFile.createNewFile();	
			}
			
		    FileUtils.copyInputStreamToFile(encryptPacketStream, targetFile);
			
			//save input stream in decrypted folder.
			
			
			
			
			
		}
	}


	
	
	
	
	/**
	 * Encrypt.
	 *
	 * @param streamToEncrypt the stream to encrypt
	 * @return the input stream
	 * @throws EncryptorBaseCheckedException the encryptor base checked exception
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws JSONException the JSON exception
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public InputStream encrypt(final InputStream streamToEncrypt,String regId)
			throws ApisResourceAccessException, JSONException, InvalidKeySpecException,
			java.security.NoSuchAlgorithmException, IOException {

		try {
			
			String centerId = regId.substring(0,centerIdLength); 

			byte[] dataToEncrypt = IOUtils.toByteArray(streamToEncrypt);

			// Enable AES 256 bit encryption
			Security.setProperty("crypto.policy", "unlimited");
System.out.println("1");
			// Generate AES Session Key
			final SecretKey symmetricKey = keyGenerator.getSymmetricKey();
			System.out.println("2");
			// Encrypt the Data using AES
			final byte[] encryptedData = encryptor.symmetricEncrypt(symmetricKey, dataToEncrypt);
			System.out.println("3");
			// Encrypt the AES Session Key using RSA
			final byte[] rsaEncryptedKey = encryptRSA(symmetricKey.getEncoded(),centerId);
			System.out.println("4");
			return new ByteArrayInputStream(CryptoUtil
					.encodeBase64(CryptoUtil.combineByteArray(encryptedData, rsaEncryptedKey, AES_KEY_CIPHER_SPLITTER))
					.getBytes());
			
		} catch (MosipInvalidDataException mosipInvalidDataException) {
			throw new EncryptorBaseCheckedException(mosipInvalidDataException.getErrorCode(),
					mosipInvalidDataException.getErrorText());
		} catch (MosipInvalidKeyException mosipInvalidKeyException) {
			throw new EncryptorBaseCheckedException(mosipInvalidKeyException.getErrorCode(),
					mosipInvalidKeyException.getErrorText());
		} catch (RuntimeException runtimeException) {
			throw new EncryptorBaseCheckedException(runtimeException.getMessage());
		}
	}

	/**
	 * Encrypt RSA.
	 *
	 * @param sessionKey the session key
	 * @return the byte[]
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	private byte[] encryptRSA(final byte[] sessionKey,String centerId)
			throws ApisResourceAccessException, InvalidKeySpecException, java.security.NoSuchAlgorithmException {
		try {
			System.out.println("5");
			// encrypt AES Session Key using RSA public key
			List<String> pathsegments = new ArrayList<>();
			System.out.println("6");
			pathsegments.add(APPLICATION_ID);
			System.out.println("7"+registrationProcessorRestClientService);
			String publicKeytest = (String) registrationProcessorRestClientService.getApi(ApiName.ENCRYPTIONSERVICE,
					pathsegments, "timeStamp,referenceId", DateUtils.getUTCCurrentDateTimeString() + ',' + centerId,
					String.class);
			System.out.println("7");
			Gson gsonObj = new Gson();
			PublicKeyResponseDto publicKeyResponsedto = gsonObj.fromJson(publicKeytest, PublicKeyResponseDto.class);
			PublicKey publicKey = KeyFactory.getInstance(RSA).generatePublic(
					new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKeyResponsedto.getPublicKey())));

			return encryptor.asymmetricPublicEncrypt(publicKey, sessionKey);

		} catch (NoSuchAlgorithmException compileTimeException) {
			throw new EncryptorBaseCheckedException(compileTimeException.getMessage(), compileTimeException.toString(),
					compileTimeException);
		} catch (RuntimeException runtimeException) {
			throw new EncryptorBaseCheckedException(runtimeException.getMessage(), runtimeException.toString(),
					runtimeException);
		}
	}

}
