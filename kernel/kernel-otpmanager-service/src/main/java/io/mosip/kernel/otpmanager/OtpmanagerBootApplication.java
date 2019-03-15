package io.mosip.kernel.otpmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * -------------------------------------------------------------------------
 * OTP-MANAGER-SERVICE APPLICATION
 * -------------------------------------------------------------------------
 * This service serves the functionality of OTP Generation, OTP Validation.
 * -------------------------------------------------------------------------
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootApplication
public class OtpmanagerBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 *            the argument
	 */
	public static void main(String[] args) {
		SpringApplication.run(OtpmanagerBootApplication.class, args);
	}
}
