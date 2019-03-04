package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.id.RegCenterUserId;
import lombok.Getter;
import lombok.Setter;

/**
 * RegCenterUser entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "reg_center_user")
@Getter
@Setter
public class RegCenterUser extends RegistrationCommonFields {

	@EmbeddedId
	private RegCenterUserId regCenterUserId;

	@OneToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private UserDetail userDetail;

	@OneToOne
	@JoinColumns({ @JoinColumn(name = "regcntr_id",referencedColumnName="id", insertable = false, updatable = false),
		@JoinColumn(name = "lang_code",referencedColumnName="lang_code", insertable = false, updatable = false) })
	private RegistrationCenter registrationCenter;

	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;
	@Column(name = "lang_code")
	private String langCode;

}
