package io.mosip.registration.processor.camel.bridge;

import java.io.IOException;

import org.apache.camel.CamelContext;
import org.apache.camel.component.vertx.VertxComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.camel.bridge.util.BridgeUtil;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.vertx.camel.CamelBridge;
import io.vertx.camel.CamelBridgeOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * This class starts Vertx camel bridge.
 *
 * @author Mukul Puspam
 * @author Pranav kumar
 * @since 0.0.1
 */
public class MosipBridgeFactory extends AbstractVerticle {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MosipBridgeFactory.class);

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public static void getEventBus() {
		String clusterFileName = BridgeUtil.getPropertyFromConfigServer("cluster.manager.file.name");
		String clusterUrl = BridgeUtil.getCloudConfigUri();
		clusterUrl = clusterUrl + "/*/" + BridgeUtil.getActiveProfile() + "/" + BridgeUtil.getCloudConfigLabel() + "/"
				+ clusterFileName;
		Config config = null;
		try {
			config = new UrlXmlConfig(clusterUrl);
		} catch (IOException e) {
			regProcLogger.error("", "", "", e.getMessage());
		}
		ClusterManager clusterManager = new HazelcastClusterManager(config);
		VertxOptions options = new VertxOptions().setClusterManager(clusterManager).setHAEnabled(true)
				.setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(MosipBridgeFactory.class.getName(),
						new DeploymentOptions().setHa(true).setWorker(true));
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(), "failed : ", vertx.cause().toString());
			}
		});
	}

	@Override
	public void start() throws Exception {
		CamelContext camelContext = new DefaultCamelContext();
		VertxComponent vertxComponent = new VertxComponent();
		vertxComponent.setVertx(vertx);
		RestTemplate restTemplate = new RestTemplate();
		String camelRoutesFileName = BridgeUtil.getPropertyFromConfigServer("camel.routes.file.name");
		String camelRoutesUrl = BridgeUtil.getCloudConfigUri();
		camelRoutesUrl = camelRoutesUrl + "/*/" + BridgeUtil.getActiveProfile() + "/" + BridgeUtil.getCloudConfigLabel()
				+ "/" + camelRoutesFileName;
		ResponseEntity<Resource> responseEntity = restTemplate.exchange(camelRoutesUrl, HttpMethod.GET, null,
				Resource.class);
		RoutesDefinition routes = camelContext.loadRoutesDefinition(responseEntity.getBody().getInputStream());
		camelContext.addRouteDefinitions(routes.getRoutes());
		camelContext.addComponent("vertx", vertxComponent);
		camelContext.start();
		CamelBridge.create(vertx, new CamelBridgeOptions(camelContext)).start();
	}
}
