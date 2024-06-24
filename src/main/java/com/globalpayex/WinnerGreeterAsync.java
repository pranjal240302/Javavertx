package com.globalpayex;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WinnerGreeterAsync {


    private static final Logger logger= LoggerFactory.getLogger(WinnerGreeterAsync.class);
    public static final List<String> students= Arrays.asList(
            "pranjal","kajal","kaveri","nisha"
    );



    public static void main(String[] args) {
        Vertx vertx=Vertx.vertx();
//        var random=new Random();
//        String winner=students.get(random.nextInt(students.size()));

//        vertx.setTimer(1000,id-> System.out.println("and"));
//        vertx.setTimer(2000,id-> System.out.println("the"));
//        vertx.setTimer(3000,id-> System.out.println("winner"));
//        vertx.setTimer(6000,id-> System.out.println("is"));
//        vertx.setTimer(12000,id-> System.out.println(winner));
        vertx.setTimer(1000,id-> logger.info("and"));
        vertx.setTimer(2000,id-> logger.info("the"));
        vertx.setTimer(3000,id-> logger.info("winner"));
        vertx.setTimer(6000,id-> logger.info("is"));

//        vertx.setTimer(14000, id->{
//            var random=new Random();
//            String winner=students.get(random.nextInt(students.size()));
//            logger.info(winner);
//        });

        // both below ways is right
//        vertx.setTimer(14000,id->WinnerGreeterAsync.findRandom());
          vertx.setTimer(14000,WinnerGreeterAsync::findRandom);

    }

    private static void findRandom(Long aLong) {
        var random=new Random();
        String winner=students.get(random.nextInt(students.size()));
        logger.info(winner);
    }

//    private static String findRandom() {
//        var random=new Random();
//            String winner=students.get(random.nextInt(students.size()));
//            return winner;
//    }
}
