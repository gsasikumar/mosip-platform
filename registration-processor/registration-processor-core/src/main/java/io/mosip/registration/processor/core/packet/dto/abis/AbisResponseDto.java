package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class AbisResponseDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String crBy;

	private LocalDateTime crDtimes;

	private LocalDateTime delDtimes;

	private Boolean isDeleted;

	private String langCode;

	private LocalDateTime respDtimes;

	private byte[] respText;

	private String statusCode;

	private String statusComment;

	private String updBy;

	private LocalDateTime updDtimes;

	private AbisRequestDto abisRequest;

	// bi-directional many-to-one association to AbisResponseDet
	private List<AbisResponseDetDto> abisResponseDets;
}
