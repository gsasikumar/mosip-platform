package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.BiometricType;

/**
 * @author Neha
 * @since 1.0.0
 *
 */

@Repository
public interface BiometricTypeRepository extends BaseRepository<BiometricType, String> {

	/**
	 * Method to find list of BiometricType created , updated or deleted time is
	 * greater than lastUpdated timeStamp.
	 * 
	 * @param lastUpdated
	 *            timeStamp - last updated time
	 * @param currentTimeStamp
	 *            - currentTimestamp
	 * @return list of {@link BiometricType}
	 */
	@Query("FROM BiometricType WHERE (createdDateTime > ?1 and createdDateTime <=?2) OR (updatedDateTime > ?1 and updatedDateTime <=?2)  OR (deletedDateTime > ?1 and deletedDateTime <=?2)")
	List<BiometricType> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated, LocalDateTime currentTimeStamp);
}
