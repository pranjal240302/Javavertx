package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationVerticle extends AbstractVerticle {
    int a=10;
    int b=5;

    private static final Logger logger= LoggerFactory.getLogger(MultiOperationVerticle.class);

    @Override
    public void start() throws Exception {

        vertx.setTimer(5000, id1 -> {
            int result = a + b;
            logger.info("addition is:{}", result);

            vertx.setTimer(3000, id2 -> {
                int mulresult = (a * b) + result;
                logger.info("Multiplication is:{}", mulresult);

            });
        });
    }

    public static void main(String[] args) {
        Vertx vertx=Vertx.vertx();
        vertx.deployVerticle(new MultiOperationVerticle());
    }


    }

