package com.globalpayex;

import io.vertx.core.*;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.OpenOption;

public class MultiOperationsVerticle4 extends AbstractVerticle {
    private static final Logger logger= LoggerFactory.getLogger(MultiOperationsVerticle4.class);

    private void performAddition(int a, int b) {
        logger.info("addition is:{}",a+b);
    }
    private void performMultiplication(int a, int b) {
        logger.info("Multiplication is:{}",a*b);
    }

    private int computeFibo(int n) {
        int a=0;
        int b=1;
        int c=0;
        logger.info("{} where n is {}",String.valueOf(a),n);
        logger.info("{} where n is {}",String.valueOf(b),n);

        for(int i=0;i<n-2;i++){
            c=a+b;
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
        return c;
    }

    private void readFile(String filePath){
        //non blocking IO
        OpenOptions options=new OpenOptions()
                .setRead(true);
        Future<AsyncFile> readFileFuture = vertx.fileSystem().open(filePath,options);
        readFileFuture.onSuccess(asyncFile -> {
            asyncFile.handler(System.out::println)
                    .exceptionHandler(exception->logger.error("error opening file{}",exception.getMessage()));
        });
        readFileFuture.onFailure(exception->logger.error("error opening file{}",exception.getMessage()));

    }

    @Override
    public void start() throws Exception {
        int a=config().getInteger("a");
        int b=config().getInteger("b");
        vertx.setTimer(1000,id->this.performAddition(a,b));
        vertx.setTimer(1000,id->this.performMultiplication(a,b));

        //        //scdule blocking operation on worker thread
        vertx.executeBlocking(
                ()->this.computeFibo(a),
                ar->{
                    // ar--> AsyncResult
                    if(ar.succeeded()){
                        int r=ar.result();
                        logger.info("blocking operation result is:{}",r);
                    }
                });
        vertx.executeBlocking(
                ()->this.computeFibo(b),
                ar->{
                    // ar--> AsyncResult
                    if(ar.succeeded()){
                        int r=ar.result();
                        logger.info("blocking operstion result is:{}",r);
                    }
                });

//        vertx.executeBlocking(()-> {
//            this.computeFibo(a);
//            return 0;
//        });
//        vertx.executeBlocking(()-> {
//            this.computeFibo(b);
//            return 0;
//        });

        vertx.setTimer(1000,id->this.readFile("build.gradle"));
    }

    public static void main(String[] args) {
        Vertx vertx=Vertx.vertx();
        DeploymentOptions options=new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("a",10)
                        .put("b",5));
        vertx.deployVerticle(new MultiOperationsVerticle4(),options);
    }
}
