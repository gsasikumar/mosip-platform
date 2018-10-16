package io.mosip.registration.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.code.StatusCodes;
import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.entity.DocumentEntity;
import io.mosip.registration.exception.DocumentSizeExceedException;
import io.mosip.registration.repositary.DocumentRepository;
import io.mosip.registration.repositary.RegistrationRepositary;
import io.mosip.registration.service.DocumentUploadService;

@Component
@Qualifier("DocumentUploaderServiceImpl")
public class DocumentUploaderServiceImpl implements DocumentUploadService {

	private final Logger logger = LoggerFactory.getLogger(DocumentUploaderServiceImpl.class);

	// @Autowired
	// private DocumentEntity documentEntity;

	@Autowired
	@Qualifier("documentRepositoery")
	private DocumentRepository documentRepository;

	@Autowired
	@Qualifier("registrationRepository")
	private RegistrationRepositary registrationRepositary;

	@Value("${max.file.size}")
	private int maxFileSize;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mosip.practice.fileUploader.serviceImpl.DocumentUploadService#uploadFile(
	 * org.springframework.web.multipart.MultipartFile)
	 */
	@Override
	public Boolean uploadDoucment(MultipartFile file, DocumentDto documentDto) {

		boolean saveFlag = false;

		if (file.getSize() > getMaxFileSize()) {
			throw new DocumentSizeExceedException(StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		}

		DocumentEntity documentEntity = new DocumentEntity();
		if (documentDto.is_primary()) {

			documentEntity.setPreregId(documentDto.getPrereg_id());
			documentEntity.setDoc_name(file.getOriginalFilename());
			documentEntity.setDoc_cat_code(documentDto.getDoc_cat_code());
			documentEntity.setDoc_typ_code(documentDto.getDoc_typ_code());
			documentEntity.setDoc_file_format(documentDto.getDoc_file_format());
			try {
				documentEntity.setDoc_store(file.getBytes());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			documentEntity.setStatus_code(documentDto.getStatus_code());
			documentEntity.setLang_code(documentDto.getLang_code());
			documentEntity.setCr_by(documentDto.getCr_by());
			documentEntity.setCr_dtimesz(new Timestamp(System.currentTimeMillis()));
			documentEntity.setUpd_by(documentDto.getUpd_by());
			documentEntity.setUpd_dtimesz(new Timestamp(System.currentTimeMillis()));

			DocumentEntity entityr = documentRepository.save(documentEntity);
			System.out.println(entityr);

			List<String> preIdList = registrationRepositary.findBygroupIds(documentDto.getGroup_id());

			for (int counter = 0; counter < preIdList.size(); counter++) {
				if (preIdList.get(counter).equals(documentDto.getPrereg_id())) {
					preIdList.remove(counter);
				}
			}

			if (preIdList.size() > 0) {
				for (int counter = 0; counter < preIdList.size(); counter++) {

					List<DocumentEntity> entity = documentRepository.findBypreregId(preIdList.get(counter));

					for (int ecount = 0; ecount < entity.size(); ecount++) {
						if (entity.get(ecount).getDoc_cat_code().equalsIgnoreCase(documentDto.getDoc_cat_code())) {
							try {
								entity.get(ecount).setDoc_store(file.getBytes());
							} catch (IOException e) {
								logger.error(e.getMessage());
							}
						} else {
							entity.get(ecount).setPreregId(preIdList.get(counter));
							entity.get(ecount).setDoc_name(file.getOriginalFilename());
							entity.get(ecount).setDoc_cat_code(documentDto.getDoc_cat_code());
							entity.get(ecount).setDoc_typ_code(documentDto.getDoc_typ_code());
							entity.get(ecount).setDoc_file_format(documentDto.getDoc_file_format());
							try {
								entity.get(ecount).setDoc_store(file.getBytes());
							} catch (IOException e) {
								logger.error(e.getMessage());
							}
							entity.get(ecount).setStatus_code(documentDto.getStatus_code());
							entity.get(ecount).setLang_code(documentDto.getLang_code());
							entity.get(ecount).setCr_by(documentDto.getCr_by());
							entity.get(ecount).setCr_dtimesz(new Timestamp(System.currentTimeMillis()));
							entity.get(ecount).setUpd_by(documentDto.getUpd_by());
							entity.get(ecount).setUpd_dtimesz(new Timestamp(System.currentTimeMillis()));

							documentRepository.save(entity.get(ecount));
						}
					}

				}
			}

			saveFlag = true;

		}

		else if (!documentDto.is_primary()) {

			registrationRepositary.findBygroupIds(documentDto.getGroup_id());

			documentEntity.setPreregId(documentDto.getPrereg_id());
			documentEntity.setDoc_name(file.getOriginalFilename());
			documentEntity.setDoc_cat_code(documentDto.getDoc_cat_code());
			documentEntity.setDoc_typ_code(documentDto.getDoc_typ_code());
			documentEntity.setDoc_file_format(documentDto.getDoc_file_format());
			try {
				documentEntity.setDoc_store(file.getBytes());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			documentEntity.setStatus_code(documentDto.getStatus_code());
			documentEntity.setLang_code(documentDto.getLang_code());
			documentEntity.setCr_by(documentDto.getCr_by());
			documentEntity.setCr_dtimesz(new Timestamp(System.currentTimeMillis()));
			documentEntity.setUpd_by(documentDto.getUpd_by());
			documentEntity.setUpd_dtimesz(new Timestamp(System.currentTimeMillis()));

			documentRepository.save(documentEntity);

		}

		return saveFlag;

	}

	public long getMaxFileSize() {
		return (5 * 1024 * 1024);
	}

}
