package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseDataSyncDTO implements Serializable {

	private static final long serialVersionUID = 6402670047109104959L;

	private String transactionId;
	private List<String> preRegistrationIds;
}
