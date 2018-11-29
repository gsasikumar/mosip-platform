package io.mosip.kernel.synchandler.dto;

import java.util.List;

import io.mosip.kernel.synchandler.entity.CodeAndLanguageCodeId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
	private List<CodeAndLanguageCodeId> results;
}
