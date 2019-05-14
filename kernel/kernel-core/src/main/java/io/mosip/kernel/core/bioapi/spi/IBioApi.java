package io.mosip.kernel.core.bioapi.spi;

import io.mosip.kernel.core.bioapi.model.BiometricRecord;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.KeyValuePair;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.model.Score;

/**
 * The Interface IBioApi.
 * 
 * @author Sanjay Murali
 */
public interface IBioApi {

	/**
	 * It checks the quality of the provided biometric image and render the respective quality score.
	 *
	 * @param sample the sample
	 * @param flags the flags
	 * @return the quality score
	 */
	QualityScore checkQuality(BiometricRecord sample, KeyValuePair[] flags);
	
	/**
	 * It compares the biometrics and provide the respective matching scores.
	 *
	 * @param sample the sample
	 * @param gallery the gallery
	 * @param flags the flags
	 * @return the score[]
	 */
	Score[] match(BiometricRecord sample, BiometricRecord[] gallery, KeyValuePair[] flags);

	/**
	 * It uses the composite logic while comparing the biometrics and provide the composite matching score. 
	 *
	 * @param sampleList the sample list
	 * @param recordList the record list
	 * @param flags the flags
	 * @return the composite score
	 */
	CompositeScore compositeMatch ( BiometricRecord [] sampleList ,BiometricRecord [] recordList , KeyValuePair [] flags );

	/**
	 * Extract template.
	 *
	 * @param sample the sample
	 * @param flags the flags
	 * @return the biometric record
	 */
	BiometricRecord extractTemplate(BiometricRecord sample, KeyValuePair[] flags);

	/**
	 * It segment the single biometric image into multiple biometric images.
	 * Eg: Split the thumb slab into multiple fingers
	 *
	 * @param sample the sample
	 * @param flags the flags
	 * @return the biometric record[]
	 */
	BiometricRecord[] segment(BiometricRecord sample, KeyValuePair[] flags);
}
