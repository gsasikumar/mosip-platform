package io.mosip.kernel.idrepo.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class Uin.
 *
 * @author Manoj SP
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "uin")
public class Uin {

	/** The uin ref id. */
	@Id
	private String uinRefId;
	
	/** The uin. */
	private String uin;
	
	/** The status code. */
	private String statusCode;
	
	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy;
	
	/** The created date time. */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdDateTime;
	
	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;
	
	/** The updated date time. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;
	
	/** The is deleted. */
	private Boolean isDeleted;
	
	/** The deleted date time. */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;
	
	/** The uin detail. */
	@PrimaryKeyJoinColumn
	@OneToOne
	private UinDetail uinDetail;
}
