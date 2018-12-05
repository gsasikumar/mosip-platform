package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for response to user for user machine mappings
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */
@Data


public class RegistrationCenterUserMachineMappingHistoryResponseDto {
	/**
	 *  List of registration centers
	 */
	private List<RegistrationCenterUserMachineMappingHistoryDto> registrationCenters;
}
