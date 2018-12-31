package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineHistoryResponseDto {
	private List<MachineHistoryDto> machineHistoryDetails;
}
