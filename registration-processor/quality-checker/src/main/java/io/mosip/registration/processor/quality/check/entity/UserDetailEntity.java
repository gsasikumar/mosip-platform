package io.mosip.registration.processor.quality.check.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author M1048399
 * The persistent class for the user_detail database table.
 */
@Entity
@Table(name="user_detail", schema = "regprc")
public class UserDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name="del_dtimesz")
	@CreationTimestamp
	private LocalDateTime delDtimesz;

	private String email;

	@Column(name="is_active")
	private Boolean isActive;

	@Column(name="is_deleted")
	private Boolean isDeleted;

	@Column(name="lang_code")
	private String langCode;

	private String mobile;

	private String name;

	@Column(name="uin_ref_id")
	private String uinRefId;

	@Column(name="upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name="upd_dtimesz")
	@CreationTimestamp
	private LocalDateTime updDtimesz;

	@Column(name="user_status")
	private String userStatus;

	public UserDetailEntity() {
		super();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrBy() {
		return this.crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public LocalDateTime getCrDtimesz() {
		return this.crDtimesz;
	}

	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	public LocalDateTime getDelDtimesz() {
		return this.delDtimesz;
	}

	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getLangCode() {
		return this.langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUinRefId() {
		return this.uinRefId;
	}

	public void setUinRefId(String uinRefId) {
		this.uinRefId = uinRefId;
	}

	public String getUpdBy() {
		return this.updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public LocalDateTime getUpdDtimesz() {
		return this.updDtimesz;
	}

	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

	public String getUserStatus() {
		return this.userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

}