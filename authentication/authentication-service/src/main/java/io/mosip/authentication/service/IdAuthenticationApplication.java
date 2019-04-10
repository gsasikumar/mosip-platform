package io.mosip.authentication.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.authentication.common.config.IDAMappingConfig;
import io.mosip.authentication.common.factory.AuditRequestFactory;
import io.mosip.authentication.common.factory.BiometricProviderFactory;
import io.mosip.authentication.common.factory.RestRequestFactory;
import io.mosip.authentication.common.helper.AuditHelper;
import io.mosip.authentication.common.helper.IdInfoHelper;
import io.mosip.authentication.common.helper.RestHelper;
import io.mosip.authentication.common.impl.indauth.service.IdInfoFetcherImpl;
import io.mosip.authentication.common.impl.notification.service.NotificationServiceImpl;
import io.mosip.authentication.common.integration.IdRepoManager;
import io.mosip.authentication.common.integration.IdTemplateManager;
import io.mosip.authentication.common.integration.KeyManager;
import io.mosip.authentication.common.integration.MasterDataManager;
import io.mosip.authentication.common.integration.NotificationManager;
import io.mosip.authentication.common.integration.OTPManager;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.impl.indauth.service.OTPAuthServiceImpl;
import io.mosip.authentication.common.service.impl.indauth.service.PinAuthServiceImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.idgenerator.vid.impl.VidGeneratorImpl;
import io.mosip.kernel.idgenerator.vid.util.VidFilterUtils;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication
@Import(value = { HibernateDaoConfig.class, UinValidatorImpl.class, VidValidatorImpl.class, IDAMappingConfig.class,
		PDFGeneratorImpl.class, DecryptorImpl.class, CbeffImpl.class, VidGeneratorImpl.class, VidFilterUtils.class,
		RestHelper.class, RestRequestFactory.class, AuditRequestFactory.class, AuditRequestFactory.class,
		IdRepoManager.class, NotificationManager.class, NotificationServiceImpl.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class, IdAuthExceptionHandler.class, IdInfoFetcherImpl.class,
		BiometricProviderFactory.class, OTPManager.class, MasterDataManager.class, IdInfoHelper.class,
		OTPAuthServiceImpl.class, AuditHelper.class, PinAuthServiceImpl.class, KeyManager.class })

public class IdAuthenticationApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(IdAuthenticationApplication.class, args);
	}

}
