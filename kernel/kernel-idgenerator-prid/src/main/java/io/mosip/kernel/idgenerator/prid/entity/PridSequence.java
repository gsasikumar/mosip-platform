package io.mosip.kernel.idgenerator.prid.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "prid_seq", schema = "prereg")
public class PridSequence {
	/**
	 * The sequence number.
	 */
	@Id
	@Column(name = "seq_no", nullable = false)
	private String sequenceNumber;

	/**
	 * Created by.
	 */
	@Column(name = "cr_by", nullable = false)
	private String createdBy;

	/**
	 * Created date time.
	 */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdDateTime;

	/**
	 * Is deleted true or false.
	 */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/**
	 * Deleted date time.
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;
}
