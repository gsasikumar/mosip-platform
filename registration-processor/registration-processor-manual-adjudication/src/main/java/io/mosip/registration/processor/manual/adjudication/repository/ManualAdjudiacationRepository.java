package io.mosip.registration.processor.manual.adjudication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationPKEntity;

/**
 * @author Suchita
 *
 * @param <T>
 *            The Entity class {@link ManualVerificationEntity}
 * @param <E>
 *            The Primary key class {@link ManualVerificationPKEntity}
 */
@Repository
public interface ManualAdjudiacationRepository<T extends ManualVerificationEntity, E extends ManualVerificationPKEntity>
		extends BaseRepository<T, E> {

	/**
	 * This method gets the first created registration record
	 * {@link ManualVerificationEntity} with the specified status
	 * 
	 * @param statusCode
	 *            The statusCode
	 * @return {@link ManualVerificationEntity}
	 */
	@Query("SELECT mve FROM ManualVerificationEntity mve WHERE mve.crDtimes in "
			+ "(SELECT min(mve2.crDtimes) FROM ManualVerificationEntity mve2)" + " and mve.statusCode=:statusCode")
	public List<ManualVerificationEntity> getFirstApplicantDetails(@Param("statusCode") String statusCode);

	/**
	 * This method returns {@link ManualVerificationEntity} corresponding to
	 * specified registration Id and manual verifier user Id
	 * 
	 * @param regId
	 *            The registration Id
	 * @param mvUserId
	 *            The manual verifier user Id
	 * @return {@link ManualVerificationEntity}
	 */
	@Query("SELECT mve FROM ManualVerificationEntity mve where mve.pkId.regId=:regId and mve.mvUsrId=:mvUserId")
	public ManualVerificationEntity getByRegId(@Param("regId") String regId, @Param("mvUserId") String mvUserId);

}
