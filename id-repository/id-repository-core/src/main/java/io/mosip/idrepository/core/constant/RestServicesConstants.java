package io.mosip.idrepository.core.constant;

/**
 * The Enum RestServiceContants.
 *
 * @author Manoj SP
 */
public enum RestServicesConstants {

	/** The audit manager service. */
	AUDIT_MANAGER_SERVICE("mosip.idrepo.audit"),
	
	IDREPO_IDENTITY_SERVICE("mosip.idrepo.retrieve-by-uin"),

	CRYPTO_MANAGER_ENCRYPT("mosip.idrepo.encryptor"),

	CRYPTO_MANAGER_DECRYPT("mosip.idrepo.decryptor");

	/** The service name. */
	private final String serviceName;

	/**
	 * Instantiates a new rest service contants.
	 *
	 * @param serviceName the service name
	 */
	private RestServicesConstants(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}
}
