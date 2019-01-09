package io.mosip.registration.constants;

public enum ProcessNames {
	LOGIN("login authentication"), 
	PACKET("packet authentication"),
	EOD("eod authentication"),
	EXCEPTION("exception authentication"),
	ONBOARD("onboard authentication");	
	
	
	/**
	 * @param type
	 */
	private ProcessNames(String type) {
		this.type=type;
	}
	
	private final String type;
	
	/**
	 * 
	 * @return the type 
	 */
	public String getType() {
		return type;
	}

}
