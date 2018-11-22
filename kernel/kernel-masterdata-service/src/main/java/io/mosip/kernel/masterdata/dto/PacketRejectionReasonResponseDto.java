package io.mosip.kernel.masterdata.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.mosip.kernel.masterdata.entity.ReasonCategoryId;
import io.mosip.kernel.masterdata.entity.CodeLangCodeAndRsnCatCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketRejectionReasonResponseDto {
	
	private List<ReasonCategoryDto> reasonCategories;
	

}
