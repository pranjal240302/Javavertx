package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiboSeriesVerticle extends AbstractVerticle {
    private static final Logger logger= LoggerFactory.getLogger(FiboSeriesVerticle.class);
    @Override
    public void start() throws Exception {
        JsonObject config=config();
        int n1=config.getInteger("n1");
        int n2=config.getInteger("n2");

        //fiboseries
        this.computeFibo(n1);
        this.computeFibo(n2);
    }

    private void computeFibo(int n) {
        int a=0;
        int b=1;
        logger.info("{} where n is {}",String.valueOf(a),n);
        logger.info("{} where n is {}",String.valueOf(b),n);

        for(int i=0;i<n-2;i++){
            int c=a+b;
            logger.info("{} where n is {}",String.valueOf(c),n);
            //delibrate blocking

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            a=b;
            b=c;

        }
    }

    public static void main(String[] args) {
        Vertx vertx=Vertx.vertx();
        JsonObject config=new JsonObject()
                .put("n1",100)
                .put("n2",500);

        DeploymentOptions options=new DeploymentOptions()
                .setConfig(config)
                        .setThreadingModel(ThreadingModel.WORKER);
        vertx.deployVerticle(new FiboSeriesVerticle(),options);
    }
}
