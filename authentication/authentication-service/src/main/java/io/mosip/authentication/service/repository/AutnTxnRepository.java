package io.mosip.authentication.service.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;

/**
 * This is a repository class  for entity {@link AutnTxn}.
 * 
 * @author Rakesh Roshan
 */
@Repository
public interface AutnTxnRepository extends BaseRepository<AutnTxn, Integer> {

	
	/**
	 * Obtain all Authentication Transaction for particular TxnId and UIN.
	 * 
	 * @param TxnId
	 * @param UIN
	 * @return
	 * 
	 */
	public List<AutnTxn> findAllByRequestTxnIdAndUin(String TxnId, String UIN);

	/**
	 * Obtain the List of all request_dTtimes for particular UIN(uniqueId)
	 * 
	 * @param UIN
	 * @return
	 */
	/*@Query("Select txn.requestDTtimes from ida.autn_txn txn where txn.uin=:UIN")
	public List<AutnTxn> findAllRequestDTtimesByUIN(@Param("UIN") String UIN);*/

	/**
	 * Obtain the number of count of request_dTtimes for particular UIN(uniqueId)
	 * with within the otpRequestDTime and oneMinuteBeforeTime.
	 * 
	 * @param otpRequestDTime
	 * @param oneMinuteBeforeTime
	 * @param UIN
	 * @return
	 */
	// @Query("Select count(txn.requestDTtimes) from ida.autn_txn txn where
	// txn.responseDTimes>=:responseDTimes and txn.responseDTimes<=:nowTime and
	// txn.uin=:UIN")
	@Query("Select count(requestDTtimes) from AutnTxn  where requestDTtimes <= :otpRequestDTime and request_dtimes >= :oneMinuteBeforeTime and uin=:UIN")
	public int countRequestDTime(@Param("otpRequestDTime") Date otpRequestDTime,
			@Param("oneMinuteBeforeTime") Date oneMinuteBeforeTime, @Param("UIN") String UIN);

}
