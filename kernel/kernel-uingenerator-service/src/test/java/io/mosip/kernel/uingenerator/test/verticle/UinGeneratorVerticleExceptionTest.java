package io.mosip.kernel.uingenerator.test.verticle;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import io.mosip.kernel.uingenerator.config.UinGeneratorConfiguration;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorServerVerticle;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;


@RunWith(VertxUnitRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UinGeneratorVerticleExceptionTest {

	private Vertx vertx;
	private int port;
	
	AbstractApplicationContext context;

	@Before
	public void before(TestContext testContext) throws IOException {
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();

		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

		 context = new AnnotationConfigApplicationContext(UinGeneratorConfiguration.class);
		vertx = Vertx.vertx();
		Verticle[] verticles = {new UinGeneratorVerticle(context), new UinGeneratorServerVerticle(context) };
		Stream.of(verticles)
				.forEach(verticle -> vertx.deployVerticle(verticle, options, testContext.asyncAssertSuccess()));
	}

	@After
	public void after(TestContext testContext) {
		if (vertx != null && testContext != null)
			vertx.close(testContext.asyncAssertSuccess());
		if (context != null)
			context.close();
	}

	@Test
	public void getUinExceptionTest(TestContext context) {
		Async async = context.async();
		WebClient client = WebClient.create(vertx);
		client.get(port, "localhost", "/v1/uingenerator/uin").send(ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				context.assertEquals(200, response.statusCode());
				client.close();
				async.complete();
			} else {
				System.out.println("Something went wrong " + ar.cause().getMessage());
			}
		});
	}
}