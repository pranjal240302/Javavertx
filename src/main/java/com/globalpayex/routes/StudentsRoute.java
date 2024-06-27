package com.globalpayex.routes;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class StudentsRoute {
    private static final Logger logger= LoggerFactory.getLogger(StudentsRoute.class);
    private static MongoClient mongoClient;

    public static Router init(Router router, Vertx vertx, JsonObject config){
        logger.info("config {}", config);
        mongoClient=MongoClient.createShared(vertx,config);
        router.get("/students").handler(StudentsRoute::getAllStudents);
        router.get("/students/:studentId").handler(StudentsRoute::getStudent);
        router.post("/students")
                .handler(routingContext->newStudent(routingContext,vertx));   //new student

        return router;

    }
    private static void newStudent(RoutingContext routingContext,Vertx vertx) {
         JsonObject requestJson=routingContext.body().asJsonObject();
         Future<String> future=mongoClient.insert("students",requestJson);
         future.onSuccess(studentId->{
             requestJson.put("_id",studentId);
             vertx
                     .eventBus()
                             .publish("new.student",
                                     new JsonObject().put("_id",studentId));
            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(201)
                    .end(requestJson.encode());
         });

         future.onFailure(exception->{
             logger.info("error {}",exception.getMessage());

         });

    }

    private static void getStudent(RoutingContext routingContext) {
        String studentId=routingContext.pathParam("studentId");
        JsonObject query=new JsonObject()
                .put("_id",new JsonObject().put("$oid", studentId));
        Future<JsonObject> future=mongoClient.findOne("students",query,null);
        future.onSuccess(studentObject->{
            if(studentObject==null){
                routingContext
                        .response()
                        .setStatusCode(484)
                        .end("Student not foound");
            }
            else {
                JsonObject responseJson = mapDbToResponseJson(studentObject);
                routingContext
                        .response()
                        .putHeader("Content-Type", "application/json")
                        .end(responseJson.encode());
            }
        });

        future.onFailure(exception->{
            logger.error("error in fetching students {}",exception.getCause());
            routingContext
                    .response()
                    .setStatusCode(508)
                    .end("server error");
        });
    }

    private static void getAllStudents(RoutingContext routingContext) {
        List<String> genderQp=routingContext.queryParam("gender");
        List<String> countryQp = routingContext.queryParam("country");

        JsonObject query=new JsonObject();
        JsonArray orCondition=new JsonArray();
//        if(genderQp.size() > 0){
//            query.put("gender",genderQp.get(0));
//        }
//        if(countryQp.size()> 0){
//            query.put("address.country",countryQp.get(0));
//        }
        if (genderQp.size() > 0) {
            orCondition.add(new JsonObject().put("gender", genderQp.get(0)));
        }
        if (countryQp.size() > 0) {
            orCondition.add(new JsonObject().put("address.country", countryQp.get(0)));
        }
        query.put("$or", orCondition);


        Future<List<JsonObject>> future=mongoClient
//                .find("students",new JsonObject());
                  .find("students",query);
        future.onSuccess(studentJsonObjects->{
            logger.info("students {}",studentJsonObjects);
            List<JsonObject> responseJson=studentJsonObjects
                    .stream()
                    .map(StudentsRoute::mapDbToResponseJson)
                    .collect(Collectors.toList());

            JsonArray responseData=new JsonArray(responseJson);

            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json")
                    .end(responseData.encode());
        });
        future.onFailure(exception->{
            logger.error("error in fetching students {}",exception.getCause());
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end("server error");
        });

    }

    private static JsonObject mapDbToResponseJson(JsonObject dbJsonObject) {
        JsonObject responseJson=new JsonObject();
//        responseJson.put("_id",dbJsonObject
//                .getJsonObject("_id")
//                .getString("$oid"));
        responseJson.put("_id",dbJsonObject.getString("_id"));
        responseJson.put("username",dbJsonObject.getString("username"));
        responseJson.put("gender",dbJsonObject.getString("gender"));
        responseJson.put("email",dbJsonObject.getString("email"));

        return  responseJson;
    }
}
