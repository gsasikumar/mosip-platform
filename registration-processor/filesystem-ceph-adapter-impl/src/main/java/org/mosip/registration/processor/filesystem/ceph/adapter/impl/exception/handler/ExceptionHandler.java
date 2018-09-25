package org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.handler;

import org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.ConnectionUnavailableException;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.InvalidConnectionParameters;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.utils.ExceptionMessages;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

/**
 * Global Exception handler
 * 
 * @author Pranav Kumar
 *
 */
public class ExceptionHandler {
	
	private ExceptionHandler() {
		
	}
	
	public static void exceptionHandler(AmazonS3Exception e) {
		if(e.getStatusCode() == 403) {
			throw new InvalidConnectionParameters(ExceptionMessages.INVALID_CONNECTION_CREDENTIALS.name());
		}
		else if(e.getStatusCode() == 404) {
			throw new PacketNotFoundException(ExceptionMessages.INVALID_PACKET_FILE_NAME.name());
		}
	}
	public static void exceptionHandler(SdkClientException e) {
		throw new ConnectionUnavailableException(ExceptionMessages.INVALID_CONNECTION_PATH.name());
	}

}
