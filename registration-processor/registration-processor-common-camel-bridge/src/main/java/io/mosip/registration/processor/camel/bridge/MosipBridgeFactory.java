package io.mosip.registration.processor.camel.bridge;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.component.vertx.VertxComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.RoutesDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.camel.bridge.util.BridgeUtil;
import io.mosip.registration.processor.camel.bridge.util.PropertyFileUtil;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.vertx.camel.CamelBridge;
import io.vertx.camel.CamelBridgeOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
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
	private static String camelRoutesFileName;

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public static void getEventBus() {
		String clusterFileName;
		String zone = BridgeUtil.getZone();
		if (zone.equalsIgnoreCase("dmz")) {
			clusterFileName = BridgeUtil.getPropertyFromConfigServer("dmz.cluster.manager.file.name");
			camelRoutesFileName = BridgeUtil.getPropertyFromConfigServer("camel.routes.dmz.file.name");
		} else {
			clusterFileName = BridgeUtil.getPropertyFromConfigServer("cluster.manager.file.name");
			camelRoutesFileName = BridgeUtil.getPropertyFromConfigServer("camel.routes.secure.file.name");
		}
		String clusterUrl = BridgeUtil.getCloudConfigUri();
		String eventBusPort = PropertyFileUtil.getProperty(MosipBridgeFactory.class, "bootstrap.properties",
				"eventbus.port");
		clusterUrl = clusterUrl + "/*/" + BridgeUtil.getActiveProfile() + "/" + BridgeUtil.getCloudConfigLabel() + "/"
				+ clusterFileName;
		Config config = null;
		try {
			config = new UrlXmlConfig(clusterUrl);
		} catch (IOException e) {
			regProcLogger.error("", "", "", e.getMessage());
		}
		String address = null;
		try {
			address = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ClusterManager clusterManager = new HazelcastClusterManager(config);
		VertxOptions options = new VertxOptions().setClusterManager(clusterManager).setHAEnabled(true)
				.setClustered(true)
				.setEventBusOptions(new EventBusOptions().setHost(address).setPort(Integer.parseInt(eventBusPort)));

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
		ApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("spring.xml");
		JndiRegistry registry=null;
		    String[] beanNames=classPathXmlApplicationContext.getBeanDefinitionNames();
		    if (beanNames != null) {
		      Map<String,String> enviroment= new HashMap<>();
		      enviroment.put("java.naming.factory.initial", "org.apache.camel.util.jndi.CamelInitialContextFactory");
		      registry= new JndiRegistry(enviroment);
		      for (String name : beanNames) {
		            registry.bind(name,classPathXmlApplicationContext.getBean(name));
		      }
		    }
		CamelContext camelContext = new DefaultCamelContext(registry);
		camelContext.setStreamCaching(true);
		VertxComponent vertxComponent = new VertxComponent();
		vertxComponent.setVertx(vertx);
//        RestTemplate restTemplate = new RestTemplate();
//        String camelRoutesUrl = BridgeUtil.getCloudConfigUri();
//        camelRoutesUrl = camelRoutesUrl + "/*/" + BridgeUtil.getActiveProfile() + "/" + BridgeUtil.getCloudConfigLabel()
//                + "/" + camelRoutesFileName;
//        ResponseEntity<Resource> responseEntity = restTemplate.exchange(camelRoutesUrl, HttpMethod.GET, null,
//                Resource.class);
//        RoutesDefinition routes = camelContext.loadRoutesDefinition(responseEntity.getBody().getInputStream());
		RoutesDefinition routes = camelContext.loadRoutesDefinition(
				ClassLoader.getSystemResourceAsStream("registration-processor-camel-routes-dmz-dev.xml"));
		camelContext.addRouteDefinitions(routes.getRoutes());
		camelContext.addRouteDefinitions(routes.getRoutes());
		camelContext.addComponent("vertx", vertxComponent);
		camelContext.start();
		CamelBridge.create(vertx, new CamelBridgeOptions(camelContext)).start();
	}
}
