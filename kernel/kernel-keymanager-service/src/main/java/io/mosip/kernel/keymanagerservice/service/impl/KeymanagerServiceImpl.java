package io.mosip.kernel.keymanagerservice.service.impl;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponseDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyResponseDto;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.repository.KeymanagerRepository;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.mosip.kernel.keymanagerservice.util.KeyPairUtil;
import io.mosip.kernel.keymanagerservice.util.MetadataUtil;

/**
 * This class provides the implementation for the methods of KeymanagerService
 * interface.
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class KeymanagerServiceImpl implements KeymanagerService {

	/**
	 * Keystore to handles and store cryptographic keys.
	 */
	@Autowired
	KeyStore keyStore;

	/**
	 * Decryptor instance to decrypt data
	 */
	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	/**
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	KeymanagerRepository keymanagerRepository;

	/**
	 * Utility to generate KeyPair
	 */
	@Autowired
	KeyPairUtil keyPairUtil;

	/**
	 * Utility to generate Metadata
	 */
	@Autowired
	MetadataUtil metadataUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#getPublicKey(java.lang.
	 * String, java.time.LocalDateTime, java.util.Optional)
	 */
	public PublicKeyResponseDto getPublicKey(String applicationId, LocalDateTime timeStamp,
			Optional<String> referenceId) {

		String alias;
		List<KeyAlias> keyAliases;
		PublicKeyResponseDto keyResponseDto = new PublicKeyResponseDto();

		if (referenceId.isPresent()) {
			keyAliases = keymanagerRepository.findByApplicationIdAndReferenceId(applicationId, referenceId.get());
		} else {
			keyAliases = keymanagerRepository.findByApplicationId(applicationId);
		}

		keyAliases.forEach(System.out::println);

		Optional<KeyAlias> currentKeyAlias = keyAliases.stream().sorted(
				(keyAlias1, keyAlias2) -> keyAlias2.getKeyGenerationTime().compareTo(keyAlias1.getKeyGenerationTime()))
				.findFirst();

		System.out.println(currentKeyAlias);

		if (!currentKeyAlias.isPresent()) {

			System.out.println("!!!Creating new");
			alias = UUID.randomUUID().toString();
			KeyAlias keyAlias = keyPairUtil.createNewKeyPair(applicationId, referenceId, alias);
			keymanagerRepository.create(metadataUtil.setMetaData(keyAlias));
		} else {

			System.out.println("!!!Already exists");
			alias = currentKeyAlias.get().getAlias();
			X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
			try {
				
				certificate.checkValidity();
				System.out.println("!!!Valid");
			} catch (CertificateExpiredException | CertificateNotYetValidException e) {
				
				System.out.println("!!!Not Valid");
				alias = UUID.randomUUID().toString();
				KeyAlias keyAlias = keyPairUtil.createNewKeyPair(applicationId, referenceId, alias);
				keymanagerRepository.create(metadataUtil.setMetaData(keyAlias));
			}
		}
		System.out.println(alias);
		PublicKey publicKey = keyStore.getPublicKey(alias);
		keyResponseDto.setPublicKey(publicKey.getEncoded());
		return keyResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#decryptSymmetricKey(java
	 * .lang.String, java.time.LocalDateTime, java.util.Optional, byte[])
	 */
	@Override
	public SymmetricKeyResponseDto decryptSymmetricKey(SymmetricKeyRequestDto symmetricKeyRequestDto) {

		SymmetricKeyResponseDto keyResponseDto = new SymmetricKeyResponseDto();
		List<KeyAlias> keyAliases = keymanagerRepository.findByApplicationIdAndReferenceId(
				symmetricKeyRequestDto.getApplicationId(), symmetricKeyRequestDto.getReferenceId());
		keyAliases.forEach(System.out::println);

		Optional<KeyAlias> matchingAlias = keyAliases.stream().filter(
				keyAlias -> keyAlias.getKeyGenerationTime().compareTo(symmetricKeyRequestDto.getTimeStamp()) < 0)
				.sorted((keyAlias1, keyAlias2) -> keyAlias2.getKeyGenerationTime()
						.compareTo(keyAlias1.getKeyGenerationTime()))
				.findFirst();

		if (matchingAlias.isPresent()) {
			PrivateKey privateKey = keyStore.getPrivateKey(matchingAlias.get().getAlias());
			System.out.println(matchingAlias.get().getAlias());
			byte[] decryptedSymmetricKey = decryptor.asymmetricPrivateDecrypt(privateKey,
					symmetricKeyRequestDto.getEncryptedSymmetricKey());
			keyResponseDto.setSymmetricKey(decryptedSymmetricKey);
		}

		return keyResponseDto;
	}
}
