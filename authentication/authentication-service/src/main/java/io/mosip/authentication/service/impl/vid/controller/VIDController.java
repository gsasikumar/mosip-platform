package io.mosip.authentication.service.impl.vid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.spin.facade.StaticPinFacade;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * The Class VIDController,it is an REST Api to generate the VID.
 * 
 * @author Arun Bose S
 */
@RestController
public class VIDController {
	
	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";
	

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(VIDController.class);
	
	/** The uin validator. */
	@Autowired
	private UinValidatorImpl uinValidator;
	
	
	/** The static pin facade. */
	@Autowired
	private StaticPinFacade staticPinFacade;
	
	
	/**
	 * Generate VID.
	 *
	 * @param uin the uin
	 * @return the VID response DTO
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@GetMapping(path = "identity/vid/v1.0/{uin}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "VID Generation Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "VID generated successfully"),
			@ApiResponse(code = 400, message = "VID generation failed") })
	public  VIDResponseDTO generateVID(@PathVariable String uin ) throws IdAuthenticationAppException {
		VIDResponseDTO vidResponse=null;
		try {
			uinValidator.validateId(uin);
			vidResponse=staticPinFacade.generateVID(uin);
		} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(SESSION_ID, "", "", e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				throw new IdAuthenticationAppException(e.getErrorCode(), e.getMessage(), e);
		}
		return vidResponse;
		
	}

}
