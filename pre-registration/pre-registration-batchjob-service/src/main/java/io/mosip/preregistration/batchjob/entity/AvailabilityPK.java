package io.mosip.preregistration.batchjob.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AvailabilityPK implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4022968783477038513L;
	
	@Column(name = "regcntr_id")
	private String regcntrId;
	
	@Column(name = "reg_date")
	private String regDate;
	
	@Column(name = "slot_from_time")
	private LocalTime fromTime;
	
	@Column(name = "slot_to_time")
	private LocalTime toTime;
	

}
