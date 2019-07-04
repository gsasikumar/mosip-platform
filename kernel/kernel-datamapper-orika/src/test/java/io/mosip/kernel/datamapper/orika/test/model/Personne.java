package io.mosip.kernel.datamapper.orika.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Neha
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Personne {

	private String nom;
	private String surnom;
	private int age;

}
