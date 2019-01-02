package io.mosip.kernel.masterdata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;

/**
 * Repository class for user machine mapping
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterMachineUserRepository
		extends BaseRepository<RegistrationCenterUserMachine, RegistrationCenterMachineUserID> {

	@Query("FROM RegistrationCenterUserMachine a WHERE a.cntrId=?1 AND a.machineId=?2 AND a.usrId=?3 and (a.isDeleted is null or a.isDeleted =false)")
	RegistrationCenterUserMachine findAllNondeletedMappings(String cntrId, String machineId, String usrId);

}
