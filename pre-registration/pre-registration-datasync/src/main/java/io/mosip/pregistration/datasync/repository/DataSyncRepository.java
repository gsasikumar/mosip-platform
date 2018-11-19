package io.mosip.pregistration.datasync.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.pregistration.datasync.entity.DocumentEntity;
import io.mosip.pregistration.datasync.entity.PreRegistrationEntity;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;

@Repository("dataSyncRepository")
@Transactional
public interface DataSyncRepository extends BaseRepository<ReverseDataSyncEntity, String> {

	@Query("SELECT r FROM PreRegistrationEntity r WHERE r.preRegistrationId=:preRegId")
	public PreRegistrationEntity findDemographyByPreId(@Param("preRegId") String preid);

	@Query("SELECT d FROM DocumentEntity d WHERE d.preregId = :preRegId")
	public List<DocumentEntity> findDocumentByPreId(@Param("preRegId") String preid);
	
	public void saveAll(List<PreRegistrationProcessedEntity> processedEntityList);

	
	
}
