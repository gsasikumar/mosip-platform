package io.mosip.kernel.signature.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.signature.dto.PublicKeyRequestDto;
import io.mosip.kernel.signature.dto.SignRequestDto;
import io.mosip.kernel.signature.dto.TimestampRequestDto;
import io.mosip.kernel.signature.dto.ValidatorResponseDto;

public interface SignatureService {

	/**
	 * Sign response.
	 *
	 * @param signResponseRequestDto the signResponseRequestDto
	 * @return the SignatureResponse
	 */
	public SignatureResponse signResponse(SignRequestDto signResponseRequestDto);

	/**
	 * Validate with public key.
	 *
	 * @param validateWithPublicKeyRequestDto the ValidateWithPublicKeyRequestDto
	 * @return true, if successful
	 * @throws InvalidKeySpecException  the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public ValidatorResponseDto validateWithPublicKey(PublicKeyRequestDto publicKeyRequestDto)
			throws InvalidKeySpecException, NoSuchAlgorithmException;

	public ValidatorResponseDto validate(TimestampRequestDto timestampRequestDto)
			throws InvalidKeySpecException, NoSuchAlgorithmException;

	public SignatureResponse signCertificateResponse(SignRequestDto request);

	public ValidatorResponseDto certificateValidate(TimestampRequestDto request);
}
