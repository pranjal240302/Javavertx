package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WinnerGreeterVerticle extends AbstractVerticle {


    private static final Logger logger= LoggerFactory.getLogger(WinnerGreeterVerticle.class);

    public static final List<String> students= Arrays.asList(
            "pranjal","kajal","kaveri","nisha"
    );

    @Override
    public void start() throws Exception {
        logger.info("verticle start");
//        super.start();
        vertx.setTimer(1000,id-> logger.info("and"));
        vertx.setTimer(2000,id-> logger.info("the"));
        vertx.setTimer(3000,id-> logger.info("winner"));
        vertx.setTimer(6000,id-> logger.info("is"));
        vertx.setTimer(14000,this::handleWinner);

        logger.info("timers intilaized in the verticle");

    }
    private void handleWinner(Long aLong) {
        var random=new Random();
        String winner=students.get(random.nextInt(students.size()));
        logger.info(winner);
    }

    public static void main(String[] args) {
        Vertx vertx=Vertx.vertx();
        //vertx.deployVerticle(new WinnergreeterVerticle()); //deploy 1 intsance of the veriable
        DeploymentOptions options=new DeploymentOptions().setInstances(2);
        vertx.deployVerticle("com.globalpayex.WinnergreeterVerticle",options);


    }

}
