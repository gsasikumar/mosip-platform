package io.mosip.kernel.idgenerator.tsp.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "tsp_id", schema = "ids")
@Data
public class Tsp {

	@Id
	@Column(name = "id", nullable = false)
	private int id;
	
	@Column(name = "tsp_id", nullable = false)
	private int tspId;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

}
