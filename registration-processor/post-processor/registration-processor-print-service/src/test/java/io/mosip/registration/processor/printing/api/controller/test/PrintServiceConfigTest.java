package io.mosip.registration.processor.printing.api.controller.test;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtils;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.printing.config.PrintServiceBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "io.mosip.registration.processor.printing.api.*"}, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				PrintServiceBeanConfig.class, RestConfigBean.class }))
public class PrintServiceConfigTest {

	@MockBean
	public PacketManager filesystemAdapter;

	@MockBean
	public ConnectionUtils connectionUtil;

	@MockBean
	public TokenValidator tokenValidator;

	@MockBean
	public RidValidator<?> ridValidator;

	@MockBean
	public UinValidator<?> uinValidator;
}
