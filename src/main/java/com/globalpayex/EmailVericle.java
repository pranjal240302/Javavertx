package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailVericle extends AbstractVerticle {
    private static final Logger logger= LoggerFactory.getLogger(EmailVericle.class);
    private MongoClient mongoClient;
    private MailClient mailClient;

    @Override
    public void start() throws Exception {
        mongoClient=MongoClient.createShared(vertx,config());
        MailConfig mailConfig=new MailConfig()
                .setHostname(config().getString("emailHostName"))
                .setPort(config().getInteger("emailPort"))
                .setStarttls(StartTLSOptions.REQUIRED)
                .setUsername(config().getString("emailUsername"))
                .setPassword(config().getString("emailPassword"));

        mailClient=MailClient.create(vertx,mailConfig);
        vertx
                .eventBus()
                .consumer("new.student",this::handleNewStudentEmail);
    }

    private void handleNewStudentEmail(Message<JsonObject> message) {
        String newstudentId=message.body().getString("_id");
        JsonObject query=new JsonObject()
                .put("_id",newstudentId);
        this.mongoClient.findOne("students",query,null)
                .onSuccess(this::handleStudentJson)
                .onFailure(this::handleStudentFailure);
    }

    private void handleStudentJson(JsonObject studentDbJson) {


        if(studentDbJson != null){
//            logger.info("email sent to student {}",studentDbJson.getString("email"));
            MailMessage message=new MailMessage();
            message.setFrom(config().getString("emailUsername"));
            message.setTo(studentDbJson.getString("email"));
            message.setSubject("welcome to college poratal");
            message.setText(
                    String.format("Hey %s, Welcome to the college portal",
                            studentDbJson.getString("username"))
            );
            mailClient.sendMail(message)
                    .onSuccess(mailResult ->
                            logger.info("email sent to student {}",studentDbJson.getString("email")))
                    .onFailure(exception->
                            logger.error("error sending mail {}",exception.getMessage()));


        }

    }

    private void handleStudentFailure(Throwable throwable) {
        logger.error("error in getting student:{}",throwable.getMessage());
    }
}
