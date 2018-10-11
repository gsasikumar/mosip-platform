package io.mosip.kernel.auditmanager.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Base class for {@link Audit} with {@link #uuid} and {@link #timestamp}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@MappedSuperclass
@Data
@AllArgsConstructor
public class BaseAudit {

	/**
	 * Field for immutable universally unique identifier (UUID)
	 */
	@Id
	@Column(name = "log_id", nullable = false, updatable = false)
	private String uuid;

	@Column(name = "log_dtimesz", nullable = false, updatable = false)
	private OffsetDateTime createdAt; // ,columnDefinition= "TIMESTAMP WITH TIME ZONE"

	/**
	 * Constructor to initialize {@link BaseAudit} with uuid and timestamp
	 */
	public BaseAudit() {
		uuid = UUID.randomUUID().toString();
		createdAt = OffsetDateTime.now();
	}

}
