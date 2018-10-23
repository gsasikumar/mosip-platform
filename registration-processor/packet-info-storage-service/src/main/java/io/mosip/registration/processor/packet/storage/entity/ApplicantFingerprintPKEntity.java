package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the applicant_fingerprint database table.
 * 
 * @author Horteppa M1048399
 */
@Embeddable
public class ApplicantFingerprintPKEntity implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name = "reg_id", nullable = false)
	private String regId;

	@Column(name = "typ", nullable = false)
	private String typ;

	public ApplicantFingerprintPKEntity() {
		super();
	}

	public String getRegId() {
		return this.regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getTyp() {
		return this.typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ApplicantFingerprintPKEntity)) {
			return false;
		}
		ApplicantFingerprintPKEntity castOther = (ApplicantFingerprintPKEntity) other;
		return this.regId.equals(castOther.regId) && this.typ.equals(castOther.typ);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.typ.hashCode();

		return hash;
	}
}