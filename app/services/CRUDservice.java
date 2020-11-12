package services;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import executors.MongoExecutionContext;
import models.LoginRequest;
import mongo.IMongoDB;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CRUDservice {

    @Inject
    IMongoDB mongoDB;

    @Inject
    MongoExecutionContext mEC;

    public <T> CompletableFuture<List<T>> all(Class<T> type, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            List<T> users = collection
                    .find()
                    .into(new ArrayList<>());
            return users;
        }, mEC);
    }

    public <T> CompletableFuture<T> save(Class<T> type, T item, String collectionName)
    {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            InsertOneResult result = collection.insertOne(item);


            return item;
        }, mEC);
    }

    public <T> CompletableFuture<Boolean> find(Class<T> type, LoginRequest loginRequest, String collectionName)
    {
        return CompletableFuture.supplyAsync(() -> {
            return true;
        }, mEC);
    }
}
