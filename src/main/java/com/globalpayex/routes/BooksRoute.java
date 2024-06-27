package com.globalpayex.routes;

import com.globalpayex.FirstHttpServer;
import com.globalpayex.entities.Book;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BooksRoute {
    private static final Logger logger= LoggerFactory.getLogger(BooksRoute.class);
    private static ArrayList<Book> books ;
    private static int lastUsedId=3;
    private static MongoClient mongoClient;

    public static Router init(Router router, Vertx vertx,JsonObject config){
        logger.info("config {}", config);
        mongoClient= MongoClient.createShared(vertx,config);

//        router.get("/books").handler(routingContext -> {
//            JsonArray data = new JsonArray(books);
//            routingContext
//                    .response()
//                    .putHeader("Content-Type", "application/json")  //MINE TYPE
//                    .end(data.encode());
//        });
//        books=new ArrayList<>();
//        books.add( new Book(1, "book 1", 900, 1000));
//        books.add( new Book(2, "book 2", 500, 500));
//        books.add(new Book(3, "book 3", 600, 800));
//
//        router.get("/books").handler(BooksRoute::getAllBooks);
//        router.get("/books/:bookId").handler(BooksRoute::getBook);
//        router.post("/books").handler(BooksRoute::newBook);

        router.get("/books").handler(BooksRoute::getAllBooks);
        return router;
    }

    private static void getAllBooks(RoutingContext routingContext){
        JsonObject query=buildGetAllBooksQuery(routingContext);
        mongoClient.find("book",query)
                .onSuccess(dbJson->handleGetAllBooks(dbJson,routingContext))
                .onFailure(BooksRoute::handleGetAllBooksFailure);


    }



    private static void handleGetAllBooks(List<JsonObject> dbJson, RoutingContext routingContext) {
        logger.info("db json {]",dbJson);
        List<JsonObject> responseJson=dbJson
                .stream()
                .map(BooksRoute::mapDbJsonToResponseJson)
                .collect(Collectors.toList());
        routingContext
                .response()
                .putHeader("Content-Type","application/json")
                .end(new JsonArray(responseJson).encode());

    }

    private static JsonObject mapDbJsonToResponseJson(JsonObject dbJson) {
        return new JsonObject()
                .put("_id",dbJson.getString("_id"))
                .put("title",dbJson.getString("title"))
                .put("price",dbJson.getJsonObject("details").getInteger("price"))
                .put("pages",dbJson.getJsonObject("details").getInteger("price"));

    }

    private static void handleGetAllBooksFailure(Throwable throwable) {
        logger.info("error");

    }

    private static JsonObject buildGetAllBooksQuery(RoutingContext routingContext) {
        List<String> priceQp=routingContext.queryParam("price");
        List<String> pagesQp=routingContext.queryParam("pages");

        JsonArray orConditions=new JsonArray();
        if(!priceQp.isEmpty()){
            orConditions.add(
                    new JsonObject().put(
                            "details.price",
                            new JsonObject().put("$gt",Integer.parseInt(priceQp.get(0)))
                    )
            );
        }
        if(!pagesQp.isEmpty()){
            orConditions.add(
                    new JsonObject().put(
                            "details.pages",
                            new JsonObject().put("$gt",Integer.parseInt(pagesQp.get(0)))
                    )
            );
        }
        if(orConditions.isEmpty()){
            return new JsonObject();
        }
        return new JsonObject().put("$or",orConditions);
    }

    private static void newBook(RoutingContext routingContext) {
        Book book=routingContext.body().asPojo(Book.class);
//        logger.info("new book {}",book);
        book.setId(++lastUsedId);
        books.add(book);

        routingContext.response()
                .setStatusCode(201)
                .putHeader("Content-Type", "application/json")
                .end(JsonObject.mapFrom(book).encode());

    }

    private static void getBook(RoutingContext routingContext) {
        int bookId=Integer.parseInt(routingContext.pathParam("bookId"));
        List<Book> foundBookList=books.stream()
                .filter(book->book.getId()==bookId)
                .collect(Collectors.toList());

        if(!foundBookList.isEmpty()){
            Book book=foundBookList.get(0);

            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json")
                    .end(JsonObject.mapFrom(book).encode());
        }
        else {
            JsonObject data=new JsonObject()
                    .put("message",String.format("Book with id %s not found",bookId));
            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(404)
                    .end(data.encode());

        }
    }



    private static Object mapDbToResponseJson(JsonObject dbJsonObject) {
        JsonObject responseJson=new JsonObject();
        responseJson.put("_id",dbJsonObject.getString("_id"));
        responseJson.put("title",dbJsonObject.getString("title"));
        responseJson.put("price",dbJsonObject.getString("details.price"));
        responseJson.put("pages",dbJsonObject.getString("details.pages"));

        return responseJson;
    }

//    private static void getAllBooks(RoutingContext routingContext) {
//        JsonArray data = new JsonArray(books);
//            routingContext
//                    .response()
//                    .putHeader("Content-Type", "application/json")  //MINE TYPE
//                    .end(data.encode());
//    }
}
