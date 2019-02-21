package io.mosip.preregistration.batchjobservices.repository;


import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjobservices.entity.DemographicEntity;

/**
 * @author M1043008
 *
 */
@Repository("demographicRepository")
public interface DemographicRepository extends BaseRepository<DemographicEntity, String> {

	DemographicEntity findBypreRegistrationId(@Param(value = "preRegId") String preRegId);
}
