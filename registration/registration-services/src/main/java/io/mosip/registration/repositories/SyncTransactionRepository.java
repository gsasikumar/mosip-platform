package io.mosip.registration.repositories;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.SyncTransaction;

/**
 * To save the sync transaction details
 * @author Dinesh Ashokan
 *
 */
public interface SyncTransactionRepository extends BaseRepository<SyncTransaction, String>{

	/**
	 * Get All sync Transaction
	 * @param request time
	 * @param syncJobId id
	 * @return list of sync transaction
	 */
	List<SyncTransaction> findByCrDtimeAfterAndSyncJobIdNotOrderByCrDtimeDesc(Timestamp req,String syncJobId);

	/**
	 * Get All Transactions
	 * @param syncJobId
	 * @param previousFiredTime transaction created after
	 * @param currentFiredTime transaction created before
	 * @return
	 */
	List<SyncTransaction> findBySyncJobIdAndCrDtimeBetween(String syncJobId,Timestamp previousFiredTime, Timestamp currentFiredTime);
	
}
