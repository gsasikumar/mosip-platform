/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.repository;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.application.entity.DemographicEntity;

/**
 * Registration Repository
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 * 
 */
@Repository("registrationRepository")
@Transactional
public interface DemographicRepository extends BaseRepository<DemographicEntity, String> {

	/**
	 * @param userId
	 * @return list of pregistration data
	 */
	public List<DemographicEntity> findByCreatedBy(@Param("userId") String userId);

	/**
	 * @param userId
	 * @return list of group ids
	 */
	public List<String> noOfGroupIds(@Param("userId") String userId);

	/**
	 * @param preRegId
	 * @return
	 */
	public DemographicEntity findBypreRegistrationId(@Param("preRegId") String preRegId);

	/**
	 * @param preId
	 * @return the number of rows deleted
	 */
	public int deleteByPreRegistrationId(String preId);

	/**
	 * @param start
	 * @param end
	 * @return list of pregistration data
	 */
	public List<DemographicEntity> findBycreateDateTimeBetween(Timestamp start, Timestamp end);

}
