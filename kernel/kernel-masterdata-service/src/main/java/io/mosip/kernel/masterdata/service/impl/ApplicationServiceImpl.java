package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ApplicationRepository;
import io.mosip.kernel.masterdata.service.ApplicationService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@Service
public class ApplicationServiceImpl implements ApplicationService {

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private ObjectMapperUtil objectMapperUtil;

	@Autowired
	private ModelMapper modelMapper;

	private List<Application> applicationList;

	private List<ApplicationDto> applicationDtoList;

	@Override
	public List<ApplicationDto> getAllApplication() {
		try {
			applicationList = applicationRepository.findAll(Application.class);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage());
		}
		if (!(applicationList.isEmpty())) {
			applicationDtoList = objectMapperUtil.mapAll(applicationList, ApplicationDto.class);
		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return applicationDtoList;
	}

	@Override
	public List<ApplicationDto> getAllApplicationByLanguageCode(String languageCode) {

		try {
			applicationList = applicationRepository.findAllByLanguageCode(languageCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage());
		}
		if (!(applicationList.isEmpty())) {
			applicationDtoList = objectMapperUtil.mapAll(applicationList, ApplicationDto.class);
		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return applicationDtoList;
	}

	@Override
	public ApplicationDto getApplicationByCodeAndLanguageCode(String code, String languageCode) {
		Application application;
		ApplicationDto applicationDto;
		try {
			application = applicationRepository.findByCodeAndLanguageCode(code, languageCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage());
		}
		if (application != null) {
			applicationDto = modelMapper.map(application, ApplicationDto.class);

		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return applicationDto;
	}

}
