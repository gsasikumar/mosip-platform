package io.mosip.registration.processor.core.abstractverticle;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public abstract class MosipVerticleAPIManager extends MosipVerticleManager{

	public Router postUrl(Vertx vertx) {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		return router;
	}
	
	public void createServer(Router router, int port) {
		vertx.createHttpServer().requestHandler(router::accept).listen(port);
	}
	
	public void setResponse(RoutingContext ctx, Object object) {
		ctx.response().putHeader("content-type", "text/plain")
					  .putHeader("Access-Control-Allow-Origin", "*")
					  .putHeader("Access-Control-Allow-Methods","GET, POST") 
					  .setStatusCode(200)
					  .end(object.toString());
	};
	
	public void setResponse(RoutingContext ctx, Object object, String contentType) {
		ctx.response().putHeader("content-type", contentType)
					  .putHeader("Access-Control-Allow-Origin", "*")
					  .putHeader("Access-Control-Allow-Methods","GET, POST") 
					  .setStatusCode(200)
					  .end(object.toString());
	};
}
