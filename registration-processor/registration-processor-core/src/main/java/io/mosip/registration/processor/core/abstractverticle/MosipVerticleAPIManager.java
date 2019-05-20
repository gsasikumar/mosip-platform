package io.mosip.registration.processor.core.abstractverticle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author Mukul Puspam
 *
 */
public abstract class MosipVerticleAPIManager extends MosipVerticleManager{

	@Value("${registration.processor.signature.isEnabled}")
	Boolean isEnabled;

	@Autowired
	DigitalSignatureUtility digitalSignatureUtility;

	/**
	 * This method creates a body handler for the routes
	 * @param vertx
	 * @return
	 */
	public Router postUrl(Vertx vertx) {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		return router;
	}

	/**
	 * This method creates server for vertx web application
	 * @param router
	 * @param port
	 */
	public void createServer(Router router, int port) {
		vertx.createHttpServer().requestHandler(router::accept).listen(port);
	}

	/**
	 * This method returns a response to the routing context
	 * @param ctx
	 * @param object
	 */
	public void setResponse(RoutingContext ctx, Object object) {
		ctx.response().putHeader("content-type", "text/plain")
		.putHeader("Access-Control-Allow-Origin", "*")
		.putHeader("Access-Control-Allow-Methods","GET, POST") 
		.setStatusCode(200)
		.end(object.toString());
	};

	/**
	 * This method returns a response to the routing context
	 * @param ctx
	 * @param object
	 * @param contentType
	 */
	public void setResponseWithDigitalSignature(RoutingContext ctx, Object object, String contentType) {
		HttpServerResponse response = ctx.response();
		if(isEnabled)
			response.putHeader("Response-Signature", digitalSignatureUtility.getDigitalSignature(object.toString()));		
		response.putHeader("content-type", contentType)
		.putHeader("Access-Control-Allow-Origin", "*")
		.putHeader("Access-Control-Allow-Methods","GET, POST") 
		.setStatusCode(200)
		.end(object.toString());

	};
}
