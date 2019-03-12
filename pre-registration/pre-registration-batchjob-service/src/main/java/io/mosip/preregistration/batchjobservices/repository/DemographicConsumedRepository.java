package io.mosip.preregistration.batchjobservices.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjobservices.entity.DemographicEntityConsumed;

@Repository("demographicConsumedRepository")
public interface DemographicConsumedRepository extends BaseRepository<DemographicEntityConsumed, String>{

}
