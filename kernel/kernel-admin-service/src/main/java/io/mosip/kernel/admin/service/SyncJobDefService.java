package io.mosip.kernel.admin.service;

import java.time.LocalDateTime;

import io.mosip.kernel.admin.dto.response.SyncJobDefResponseDto;


/**
 * Interface SyncJobDefService.
 */
public interface SyncJobDefService {

	/**
	 * Gets the latest sync job def details.
	 *
	 * @param lastUpdatedTimeStamp the last updated time stamp
	 * @param currentTimeStamp the current time stamp
	 * @return {@link SyncJobDefResponseDto}
	 */
	public SyncJobDefResponseDto getLatestSyncJobDefDetails(LocalDateTime lastUpdatedTimeStamp,LocalDateTime currentTimeStamp);
}
