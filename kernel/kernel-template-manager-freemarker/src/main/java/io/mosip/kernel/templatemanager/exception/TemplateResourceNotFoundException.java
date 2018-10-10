package io.mosip.kernel.templatemanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
/**
 *  this exception thrown when a resource of any type
 *  isn't found by the template manager.
 *  <br>
 *  When this exception is thrown, a best effort will be made to have
 *  useful information in the exception's message.  For complete
 *  information, consult the runtime log.
 *  
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 2018-10-4
 */
public class TemplateResourceNotFoundException extends BaseUncheckedException{

	private static final long serialVersionUID = 3070414901455295210L;
	/**
	 * constructor for setting error code and message
	 * @param errorCode
	 * @param errorMessage
	 */
	public TemplateResourceNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
