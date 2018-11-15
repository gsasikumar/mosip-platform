package io.mosip.kernel.masterdata.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReasonCategoryDto {
	private String code;

	private String name;

	private String description;

	private String languageCode;

	private List<ReasonListDto> reasonList = new ArrayList<ReasonListDto>();
}
