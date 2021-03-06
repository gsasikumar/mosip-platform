package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.MachineSpecificationErrorCode;
import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.MachineSpecificationExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;
import io.mosip.kernel.masterdata.service.MachineSpecificationService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class have methods to save a Machine Specification Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class MachineSpecificationServiceImpl implements MachineSpecificationService {

	/**
	 * Field to hold Machine Repository object
	 */
	@Autowired
	MachineSpecificationRepository machineSpecificationRepository;

	@Autowired
	MachineTypeRepository machineTypeRepository;

	@Autowired
	MachineRepository machineRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#
	 * createMachineSpecification(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public IdAndLanguageCodeID createMachineSpecification(MachineSpecificationDto machineSpecification) {

		MachineSpecification renMachineSpecification = new MachineSpecification();

		MachineSpecification entity = MetaDataUtils.setCreateMetaData(machineSpecification, MachineSpecification.class);
		try {
			renMachineSpecification = machineSpecificationRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_INSERT_EXCEPTION.getErrorCode(),
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_INSERT_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(renMachineSpecification, idAndLanguageCodeID);

		return idAndLanguageCodeID;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#
	 * updateMachineSpecification(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public IdAndLanguageCodeID updateMachineSpecification(MachineSpecificationDto machineSpecification) {
		MachineSpecification updMachineSpecification = null;

		try {
			MachineSpecification renMachineSpecification = machineSpecificationRepository
					.findByIdAndLangCodeIsDeletedFalseorIsDeletedIsNull(machineSpecification.getId(),
							machineSpecification.getLangCode());
			if (renMachineSpecification != null) {

				MetaDataUtils.setUpdateMetaData(machineSpecification, renMachineSpecification, false);
				updMachineSpecification = machineSpecificationRepository.update(renMachineSpecification);
			} else {
				throw new RequestException(
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_UPDATE_EXCEPTION.getErrorCode(),
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_UPDATE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(updMachineSpecification, idAndLanguageCodeID);

		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#
	 * deleteMachineSpecification(java.lang.String)
	 */
	@Override
	public IdResponseDto deleteMachineSpecification(String id) {
		MachineSpecification delMachineSpecification = null;
		try {
			List<MachineSpecification> renMachineSpecifications = machineSpecificationRepository
					.findByIdAndIsDeletedFalseorIsDeletedIsNull(id);
			if (!renMachineSpecifications.isEmpty()) {
				for (MachineSpecification renMachineSpecification : renMachineSpecifications) {
					List<Machine> renmachineList = machineRepository
							.findMachineBymachineSpecIdAndIsDeletedFalseorIsDeletedIsNull(
									renMachineSpecification.getId());
					if (renmachineList.isEmpty()) {
						MetaDataUtils.setDeleteMetaData(renMachineSpecification);
						delMachineSpecification = machineSpecificationRepository.update(renMachineSpecification);
					} else {
						throw new MasterDataServiceException(
								MachineSpecificationErrorCode.MACHINE_DELETE_DEPENDENCY_EXCEPTION.getErrorCode(),
								MachineSpecificationErrorCode.MACHINE_DELETE_DEPENDENCY_EXCEPTION.getErrorMessage());
					}
				}
			} else {
				throw new RequestException(
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_DELETE_EXCEPTION.getErrorCode(),
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_DELETE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		IdResponseDto idResponseDto = new IdResponseDto();
		MapperUtils.map(delMachineSpecification, idResponseDto);
		return idResponseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#
	 * getAllMachineSpecfication(int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<MachineSpecificationExtnDto> getAllMachineSpecfication(int pageNumber, int pageSize, String sortBy,
			String orderBy) {
		List<MachineSpecificationExtnDto> machineSpecs = null;
		PageDto<MachineSpecificationExtnDto> machineSpecificationPages = null;
		try {
			Page<MachineSpecification> pageData = machineSpecificationRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				machineSpecs = MapperUtils.mapAll(pageData.getContent(), MachineSpecificationExtnDto.class);
				machineSpecificationPages = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(),
						pageData.getTotalElements(), machineSpecs);
			} else {
				throw new DataNotFoundException(
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_FETCH_EXCEPTION.getErrorCode(),
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return machineSpecificationPages;
	}

}
