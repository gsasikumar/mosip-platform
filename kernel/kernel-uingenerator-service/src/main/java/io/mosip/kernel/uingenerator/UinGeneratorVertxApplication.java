package io.mosip.kernel.uingenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.mosip.kernel.uingenerator.config.UinGeneratorConfiguration;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.util.ConfigUtil;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorServerVerticle;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Uin Generator Vertx Application
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @author Megha Tanga
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootApplication
@PropertySource({ "classpath:application.properties", "classpath:bootstrap.properties" })
public class UinGeneratorVertxApplication {

	/**
	 * The field for Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UinGeneratorVertxApplication.class);

	/**
	 * Server context path.
	 */
	@Value("${server.servlet.path}")
	private String contextPath;

	/**
	 * This method create or update swagger json for swagger ui after service start.
	 */
	@PostConstruct
	private void swaggerJSONFileUpdate() {

		try {
			TemplateManager templateManager;
			URL url = this.getClass().getClassLoader().getResource(UinGeneratorConstant.SWAGGER_UI_JSON_PATH);
			templateManager = new TemplateManagerBuilderImpl().build();
			Map<String, Object> map = new HashMap<>();
			map.put("servletpath", contextPath);
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream(UinGeneratorConstant.SWAGGER_JSON_TEMPLATE);
			InputStream out = templateManager.merge(is, map);
			String merged = IOUtils.toString(out, StandardCharsets.UTF_8.name());
			FileUtils.writeStringToFile(new File(url.toString()), merged, StandardCharsets.UTF_8.name());

		} catch (IOException | io.mosip.kernel.core.exception.IOException e) {
		}

	}

	/**
	 * main method for the application
	 * 
	 * @param args
	 *            the argument
	 */
	public static void main(String[] args) {
		loadPropertiesFromConfigServer();
	}

	/**
	 * This method retrieves and loads the configuration fetched from
	 * spring-config-server. If retrievation succeeds, then the local properties
	 * present are over-ridden. If retrievation fails, the local properties are used
	 * for running the application.
	 */
	private static void loadPropertiesFromConfigServer() {
		try {
			Vertx vertx = Vertx.vertx();
			List<ConfigStoreOptions> configStores = new ArrayList<>();
			List<String> configUrls = ConfigUtil.getURLs();
			configUrls.forEach(url -> configStores
					.add(new ConfigStoreOptions().setType(UinGeneratorConstant.CONFIG_STORE_OPTIONS_TYPE)
							.setConfig(new JsonObject().put(UinGeneratorConstant.URL, url).put(
									UinGeneratorConstant.TIME_OUT,
									Long.parseLong(UinGeneratorConstant.CONFIG_SERVER_FETCH_TIME_OUT)))));
			ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
			configStores.forEach(configRetrieverOptions::addStore);
			ConfigRetriever retriever = ConfigRetriever.create(vertx, configRetrieverOptions.setScanPeriod(0));
			LOGGER.info("Retrieving configuration from Spring-Config-Server");
			retriever.getConfig(json -> {
				if (json.succeeded()) {
					JsonObject jsonObject = json.result();
					if (jsonObject != null) {
						jsonObject.iterator().forEachRemaining(sourceValue -> System.setProperty(sourceValue.getKey(),
								sourceValue.getValue().toString()));
					}
					json.mapEmpty();
					retriever.close();
					vertx.close();
					startApplication();
				} else {
					LOGGER.warn(json.cause().getMessage() + "\n");
					json.otherwiseEmpty();
					retriever.close();
					vertx.close();
					startApplication();
				}
			});
		} catch (Exception exception) {
			LOGGER.warn(exception.getMessage() + "\n");
			startApplication();
		}
	}

	/**
	 * This method sets the Application Context, deploys the verticles.
	 */
	private static void startApplication() {
		ApplicationContext context = new AnnotationConfigApplicationContext(UinGeneratorConfiguration.class);
		VertxOptions options = new VertxOptions();
		Vertx vertx = Vertx.vertx(options);
		Verticle[] verticles = { new UinGeneratorVerticle(context), new UinGeneratorServerVerticle(context) };
		Stream.of(verticles).forEach(verticle -> vertx.deployVerticle(verticle, stringAsyncResult -> {
			if (stringAsyncResult.succeeded()) {
				LOGGER.info("Successfully deployed: " + verticle.getClass().getSimpleName());
			} else {
				LOGGER.info("Failed to deploy:" + verticle.getClass().getSimpleName() + "\nCause: "
						+ stringAsyncResult.cause());
			}
		}));
	}
}