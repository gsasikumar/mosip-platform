package io.mosip.preregistration.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.booking.entity.AvailibityEntity;

/**
 * Registration Repository
 * 
 * @author M1037462
 *
 */
@Repository("BookingRepository")
@Transactional
public interface BookingAvailabilityRepository extends BaseRepository<AvailibityEntity, String> {
	public static final String distDate = "SELECT DISTINCT availability_date  FROM prereg.reg_available_slot where regcntr_id=:regcntrId order by availability_date ASC";

	public List<AvailibityEntity> findByRegcntrIdAndRegDate(String regcntrId, LocalDate regDate);

	@Query(value = distDate, nativeQuery = true)
	public List<String> findDate(@Param("regcntrId") String regcntrId);
	
	/**
	 * @param slot_from_time
	 * @param slot_to_time
	 * @param reg_date
	 * @param regcntr_id
	 * @return Availability entity
	 */
	public AvailibityEntity findByFromTimeAndToTimeAndRegDateAndRegcntrId(
			@Param("slot_from_time") LocalTime slot_from_time, @Param("slot_to_time") LocalTime slot_to_time,
			@Param("availability_date") LocalDate reg_date, @Param("regcntr_id") String regcntr_id);

}
