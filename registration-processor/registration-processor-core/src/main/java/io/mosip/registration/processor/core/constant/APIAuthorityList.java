package io.mosip.registration.processor.core.constant;

public enum APIAuthorityList {

	PACKETRECEIVER(new String[] { "REGISTRATION_ ADMIN", "REGISTRATION_PROCESSOR", "REGISTRATION_OFFICER",
			"REGISTRATION_SUPERVISOR" }),

	PACKETUPLOADER(new String[] { "REGISTRATION_PROCESSOR" }),

	PACKETSYNC(new String[] { "REGISTRATION_ADMIN", "REGISTRATION_PROCESSOR", "REGISTRATION_OFFICER",
			"REGISTRATION_SUPERVISOR" }),

	REGISTRATIONSTATUS(new String[] { "REGISTRATION_ADMIN", "REGISTRATION_OFFICER", "REGISTRATION_SUPERVISOR" }),

	MANUALVERIFICTION(new String[] { "REGISTRATION_ADMIN" }),

	PRINTSTAGE(new String[] { "REGISTRATION_PROCESSOR" }),

	BIODEDUPE(new String[] { "REGISTRATION_PROCESSOR" }),

	CONNECTORSTAGE(new String[] { "REGISTRATION_PROCESSOR" }),

	BIO(new String[] { "REGISTRATION_PROCESSOR" }),

	ABIS(new String[] { "REGISTRATION_PROCESSOR" }),

	PACKETGENERATOR(new String[] { "REGISTRATION_ADMIN", "REGISTRATION_PROCESSOR" });

	private final String[] list;

	private APIAuthorityList(String[] list) {
		this.list = list;
	}

	public String[] getList() {
		return this.list;
	}
}