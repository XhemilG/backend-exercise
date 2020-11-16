package services;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import exceptions.RequestException;
import executors.MongoExecutionContext;
import models.LoginRequest;
import mongo.IMongoDB;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.mongodb.client.model.Filters.eq;
import static play.mvc.Http.Status.NOT_FOUND;

public class CRUDservice {

    @Inject
    IMongoDB mongoDB;

    @Inject
    MongoExecutionContext mEC;

    public <T> CompletableFuture<List<T>> all(Class<T> type, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            return collection
                    .find()
                    .into(new ArrayList<>());
        }, mEC);
    }

    public <T> CompletableFuture<ObjectId> save(Class<T> type, T item, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            return collection.insertOne(item).getInsertedId().asObjectId().getValue();
        }, mEC);
    }

    public <T> CompletableFuture<T> update(Class<T> type, T item, String id, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            Bson filter = eq("_id", new ObjectId(id));
            collection.replaceOne(filter, item);
            return item;
        }, mEC);
    }

    public <T> CompletableFuture<ObjectId> delete(Class<T> type, String id, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            Bson filter = eq("_id", new ObjectId(id));
            collection.deleteOne(filter);
            return new ObjectId(id);
        }, mEC);
    }

    public <T> CompletableFuture<T> find(Class<T> type, String field, String value, String collectionName){
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            Bson filter = eq(field, value);
            List<T> temp = collection.find(filter).into(new ArrayList<>());

            T item;

            try {
                if (temp.size() == 1) {
                    item = temp.get(0);
                } else {
                    throw new RequestException(NOT_FOUND, "The required item doesn't exist!");
                }
            } catch (RequestException ex) {
                ex.printStackTrace();
                throw new CompletionException(ex);
            }

            return item;
        }, mEC);
    }

}
