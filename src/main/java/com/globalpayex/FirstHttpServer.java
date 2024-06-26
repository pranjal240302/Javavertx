package com.globalpayex;

import com.globalpayex.entities.Book;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;


public class FirstHttpServer extends AbstractVerticle {
    private static final Logger logger= LoggerFactory.getLogger(FirstHttpServer.class);


    @Override
    public void start() throws Exception {
        //dummy database
        List<Book> books= Arrays.asList(
                new Book(1,"book 1",900,1000),
                new Book(2,"book 2",500,500),
                new Book(3,"book 3",600,800),
                new Book(4,"book 4",200,400)
        );

        Router router=Router.router(vertx);
        router.get("/books").handler(routingContext -> {
            JsonArray data=new JsonArray(books);
            routingContext
                    .response()
                    .putHeader("Content-Type","application/json")  //MINE TYPE
                    .end(data.encode());
        });

        Future<HttpServer> serverFuture=vertx.createHttpServer()
//                .requestHandler(request->request.response().end("hello world"))
                .requestHandler(router)
                .listen(config().getInteger("port"));

        serverFuture.onSuccess(
                httpServer -> logger.info("Server running on port {}",httpServer.actualPort())
        );
        serverFuture.onFailure(
                exception->logger.error("cannot start server because {}",exception.getMessage())
        );
    }

    public static void main(String[] args) {
        Vertx vertx=Vertx.vertx();
        vertx.deployVerticle(new FirstHttpServer());
        DeploymentOptions options=new DeploymentOptions()
                .setConfig(new JsonObject().put("port",8083));
        vertx.deployVerticle(new FirstHttpServer(),options);

    }
}
