package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.LoggerFactory;

public class MachineVerticle extends AbstractVerticle {
    private static final org.slf4j.Logger logger= LoggerFactory.getLogger(MachineVerticle.class);
    @Override
    public void start() throws Exception {

        DeploymentOptions options=new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("port",8083)
                        .put("connection_string","mongodb+srv://admin:admin123@cluster0.j93kash.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0")
                        .put("db_name","college_db")
                        .put("useObjectId",true)
                        .put("emailHostName", "smtp.gmail.com")
                        .put("emailPort", 587)
                        .put("emailUsername", "pranjalsc24@gmail.com")
                        .put("emailPassword", "jkta vfpv qbsu ult")

                );
        vertx.deployVerticle(new FirstHttpServer(),options);

        logger.info("Verticles machine 1 deployed");

    }

    public static void main(String[] args) {
//        Vertx vertx=Vertx.vertx();
//        vertx.deployVerticle(new MachineVerticle());
        Vertx.clusteredVertx(new VertxOptions())
                .onSuccess(vertex->{
                    vertex.deployVerticle(new MachineVerticle());
                })
                .onFailure(
                        exception->logger.error("error handling {}",exception.getMessage())
                );

}
}
