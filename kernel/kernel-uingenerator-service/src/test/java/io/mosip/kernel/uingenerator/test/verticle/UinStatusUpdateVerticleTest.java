package io.mosip.kernel.uingenerator.test.verticle;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.uingenerator.config.UinGeneratorConfiguration;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.dto.UinStatusUpdateReponseDto;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorServerVerticle;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UinStatusUpdateVerticleTest {

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
		Verticle[] verticles = { new UinGeneratorVerticle(context), new UinGeneratorServerVerticle(context) };
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
	public void updateVerticle(TestContext context) throws JsonProcessingException {
		Async async = context.async();
		ObjectMapper mapper = new ObjectMapper();

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(
				Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM }));

		RestTemplate restTemplate = new RestTemplateBuilder().defaultMessageConverters()
				.additionalMessageConverters(converter).build();

		ResponseWrapper<?> uinResp = restTemplate.getForObject("http://localhost:" + port + "/v1/uingenerator/uin",
				ResponseWrapper.class);
		UinResponseDto dto = mapper.convertValue(uinResp.getResponse(), UinResponseDto.class);

		UinStatusUpdateReponseDto requestDto = new UinStatusUpdateReponseDto();
		requestDto.setUin(dto.getUin());
		requestDto.setStatus("ASSIGNED");

		RequestWrapper<UinStatusUpdateReponseDto> requestWrp = new RequestWrapper<>();
		requestWrp.setId("mosip.kernel.uinservice");
		requestWrp.setVersion("1.0");
		requestWrp.setRequest(requestDto);

		String reqJson = mapper.writeValueAsString(requestWrp);

		final String length = Integer.toString(reqJson.length());
		vertx.createHttpClient().put(port, "localhost", "/v1/uingenerator/uin")
				.putHeader("content-type", "application/json").putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 200);
					response.bodyHandler(body -> {
						JsonObject json = body.toJsonObject();
						async.complete();
					});
				}).write(reqJson).end();

	}

}
