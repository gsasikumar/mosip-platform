package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Factory class for {@link PersonalIdentityDTO}, {@link PersonalAddressDTO} and
 * {@link PersonalFullAddressDTO}
 *
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemoDTO {

	/**
	 * Primary language.
	 */
	//private String langPri;

	/**
	 * Secondary language.
	 */
	//private String langSec;

	/** PersonalIdentityDTO gives individual identity details */
	private PersonalIdentityDTO pi;

	/** PersonalAddressDTO gives individual address details */
	private PersonalAddressDTO ad;

	/** PersonalFullAddressDTO gives individual full address details */
	private PersonalFullAddressDTO fad;

}
