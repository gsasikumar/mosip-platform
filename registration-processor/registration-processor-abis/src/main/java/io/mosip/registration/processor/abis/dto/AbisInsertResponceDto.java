package io.mosip.registration.processor.abis.dto;

public class AbisInsertResponceDto {
	private String id;
	private String requestId;
	private String timestamp;
	private int returnValue;
	private Integer failureReason;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(int i) {
		this.returnValue = i;
	}

	public Integer getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(Integer failureReason) {
		this.failureReason = failureReason;
	}

}
