package com.globalpayex;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class MultiOperation {
    private static final Logger logger= LoggerFactory.getLogger(MultiOperation.class);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("Enter a: ");
            int a = sc.nextInt();
            System.out.println("Enter b: ");
            int b = sc.nextInt();

            Vertx vertx=Vertx.vertx();
            vertx.setTimer(4000,id-> logger.info(String.format("Addition is:%s",a+b)));
            vertx.setTimer(3000,id-> logger.info(String.format("Multiplication is:%s",a*b)));

        }
    }
}
