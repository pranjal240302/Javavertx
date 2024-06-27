package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class StastisticsVericle extends AbstractVerticle {
    private static final Logger logger= LoggerFactory.getLogger(StastisticsVericle.class);
    @Override
    public void start() throws Exception {
        vertx
                .eventBus()
                .consumer("new.student",this::handleNewStudentMessage);

    }

    private void handleNewStudentMessage(Message<JsonObject> message) {
        String newStudentId=message.body().getString("_id");
        logger.info("new Student id {}",newStudentId);
        MongoClient mongoClient=MongoClient.createShared(vertx,config());
        mongoClient.find("students",new JsonObject())
                .onSuccess(this::handleStudentsDbJson)
                .onFailure(this::handleStudentsDbJsonFailure);

    }

    private void handleStudentsDbJson(List<JsonObject> studentsDbJson) {
        var result=studentsDbJson
                .stream()
                .collect(
                        Collectors.groupingBy(
                                studentDbJson->studentDbJson.getString("gender"),
                                Collectors.counting()
                        )
                );
        logger.info("Recomputerd String: {}",result);
    }

    private void handleStudentsDbJsonFailure(Throwable throwable) {
        logger.error("error fetching studnet {}",throwable.getMessage());
    }
}
