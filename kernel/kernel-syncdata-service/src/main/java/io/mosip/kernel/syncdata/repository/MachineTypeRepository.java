package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.MachineType;

/**
 * Repository function to fetching Machine Type details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
public interface MachineTypeRepository extends BaseRepository<MachineType, String> {
	/**
	 * Method to fetch the Machine Type by machine id.
	 * 
	 * @param machineId
	 *            id of the machine
	 * @return {@link MachineType}
	 */
	@Query(value = "SELECT mt.code, mt.name, mt.descr, mt.lang_code, mt.is_active, mt.cr_by, mt.cr_dtimes, mt.upd_by, mt.upd_dtimes, mt.is_deleted, mt.del_dtimes FROM master.machine_type mt,master.machine_spec ms, master.machine_master mm WHERE mt.code= ms.mtyp_code and ms.id= mm.mspec_id and mm.id=?1", nativeQuery = true)
	List<MachineType> findAllByMachineId(String machineId);

	/**
	 * Method to fetch the recently created,updated,deleted Machine Type by
	 * machine id and lastUpdated timeStamp.
	 * 
	 * @param machineId
	 *            id of the machine
	 * @param lastUpdated
	 *            timeStamp
	 * @return {@link MachineType}
	 */
	@Query(value = "SELECT mt.code, mt.name, mt.descr, mt.lang_code, mt.is_active, mt.cr_by, mt.cr_dtimes, mt.upd_by, mt.upd_dtimes, mt.is_deleted, mt.del_dtimes from master.machine_type mt where mt.code in	(select distinct ms.mtyp_code from master.machine_spec ms where ms.id in( select distinct mm.mspec_id from master.machine_master mm inner join master.reg_center_machine rcm on mm.id=rcm.machine_id and rcm.regcntr_id in (select distinct rcm1.regcntr_id from master.reg_center_machine rcm1 where rcm1.machine_id = ?1 ))) and ((mt.cr_dtimes > ?2 and mt.cr_dtimes <=?3) or (mt.upd_dtimes >?2 and mt.upd_dtimes<=?3) or (mt.del_dtimes >?2 and mt.del_dtimes <=?3))", nativeQuery = true)
	List<MachineType> findLatestByMachineId(String machineId, LocalDateTime lastUpdated,LocalDateTime currentTimeStamp);
}
