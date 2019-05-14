package io.mosip.kernel.core.idobjectvalidator.spi;

import io.mosip.kernel.core.idobjectvalidator.exception.FileIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectSchemaIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationProcessingException;

/**
 * Interface JSON validation against the schema.
 * 
 * @author Manoj SP
 * @author Swati Raj
 * @since 1.0.0
 * 
 */
public interface IdObjectValidator {
	
	/**
	 * Validates a JSON object passed as string with the schema provided.
	 *
	 * @param identityObject the identity object
	 * @return true, if successful
	 * @throws IdObjectValidationProcessingException the id object validation processing exception
	 * @throws IdObjectIOException the id object IO exception
	 * @throws IdObjectSchemaIOException the id object schema IO exception
	 * @throws FileIOException the file IO exception
	 */

	public boolean validateIdObject(Object identityObject)
			throws IdObjectValidationProcessingException, IdObjectIOException, IdObjectSchemaIOException, FileIOException;

}
