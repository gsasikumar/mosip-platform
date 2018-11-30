package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.BiometricTypeErrorCode;
import io.mosip.kernel.masterdata.dto.BiometricTypeData;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.BiometricTypeRepository;
import io.mosip.kernel.masterdata.service.BiometricTypeService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * 
 * @author Neha
 * @since 1.0.0
 */
@Service
public class BiometricTypeServiceImpl implements BiometricTypeService {

	@Autowired
	DataMapper dataMapper;

	@Autowired
	private MetaDataUtils metaDataUtils;

	@Autowired
	private BiometricTypeRepository biometricTypeRepository;
	private List<BiometricTypeDto> biometricTypeDtoList;
	private List<BiometricType> biometricTypesList;

	/**
	 * Method to fetch all Biometric Type details
	 * 
	 * @return BiometricTypeDTO list
	 * 
	 * @throws BiometricTypeFetchException
	 *             If fails to fetch required Biometric Type
	 * 
	 * @throws BiometricTypeMappingException
	 *             If not able to map Biometric Type entity with BiometricType Dto
	 * 
	 * @throws BiometricTypeNotFoundException
	 *             If given required Biometric Type not found
	 */
	@Override
	public BiometricTypeResponseDto getAllBiometricTypes() {
		biometricTypeDtoList = new ArrayList<>();
		try {
			biometricTypesList = biometricTypeRepository.findAllByIsDeletedFalse(BiometricType.class);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (!(biometricTypesList.isEmpty())) {
			biometricTypesList.forEach(biometricType -> {
				BiometricTypeDto biometricTypeDto = new BiometricTypeDto();
				dataMapper.map(biometricType, biometricTypeDto, true, null, null, true);
				biometricTypeDtoList.add(biometricTypeDto);
			});
		} else {
			throw new DataNotFoundException(BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorMessage());
		}
		BiometricTypeResponseDto biometricTypeResponseDto = new BiometricTypeResponseDto();
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtoList);
		return biometricTypeResponseDto;
	}

	/**
	 * Method to fetch all Biometric Type details based on language code
	 * 
	 * @param langCode
	 *            The language code
	 * 
	 * @return BiometricTypeDTO list
	 * 
	 * @throws BiometricTypeFetchException
	 *             If fails to fetch required Biometric Type
	 * 
	 * @throws BiometricTypeMappingException
	 *             If not able to map Biometric Type entity with BiometricType Dto
	 * 
	 * @throws BiometricTypeNotFoundException
	 *             If given required Biometric Type not found
	 */
	@Override
	public BiometricTypeResponseDto getAllBiometricTypesByLanguageCode(String langCode) {
		biometricTypeDtoList = new ArrayList<>();
		try {
			biometricTypesList = biometricTypeRepository.findAllByLangCodeAndIsDeletedFalse(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (!(biometricTypesList.isEmpty())) {
			biometricTypesList.forEach(biometricType -> {
				BiometricTypeDto biometricTypeDto = new BiometricTypeDto();
				dataMapper.map(biometricType, biometricTypeDto, true, null, null, true);
				biometricTypeDtoList.add(biometricTypeDto);
			});
		} else {
			throw new DataNotFoundException(BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorMessage());
		}
		BiometricTypeResponseDto biometricTypeResponseDto = new BiometricTypeResponseDto();
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtoList);
		return biometricTypeResponseDto;
	}

	/**
	 * Method to fetch all Biometric Type details based on id and language code
	 * 
	 * @param code
	 *            The id of Biometric Type
	 * 
	 * @param langCode
	 *            The language code
	 * 
	 * @return BiometricTypeDTO list
	 * 
	 * @throws BiometricTypeFetchException
	 *             If fails to fetch required Biometric Type
	 * 
	 * @throws BiometricTypeMappingException
	 *             If not able to map Biometric Type entity with BiometricType Dto
	 * 
	 * @throws BiometricTypeNotFoundException
	 *             If given required Biometric Type not found
	 */
	@Override
	public BiometricTypeResponseDto getBiometricTypeByCodeAndLangCode(String code, String langCode) {
		BiometricType biometricType;
		BiometricTypeDto biometricTypeDto = new BiometricTypeDto();
		try {
			biometricType = biometricTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(code, langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}

		if (biometricType != null) {
			dataMapper.map(biometricType, biometricTypeDto, true, null, null, true);
		} else {
			throw new DataNotFoundException(BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorMessage());
		}
		List<BiometricTypeDto> biometricTypeDtos = new ArrayList<>();
		biometricTypeDtos.add(biometricTypeDto);
		BiometricTypeResponseDto biometricTypeResponseDto = new BiometricTypeResponseDto();
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtos);
		return biometricTypeResponseDto;
	}

	@Override
	public CodeAndLanguageCodeID addBiometricType(RequestDto<BiometricTypeData> biometricTypeRequestDto) {
		BiometricType entity = metaDataUtils.setCreateMetaData(biometricTypeRequestDto.getRequest().getBiometricType(),
				BiometricType.class);
		BiometricType biometricType;
		try {
			biometricType = biometricTypeRepository.create(entity);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_INSERT_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		CodeAndLanguageCodeID codeAndLanguageCodeId = new CodeAndLanguageCodeID();
		dataMapper.map(biometricType, codeAndLanguageCodeId, true, null, null, true);
		return codeAndLanguageCodeId;
	}

}
