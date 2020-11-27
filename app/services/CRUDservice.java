package services;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import executors.MongoExecutionContext;
import mongo.IMongoDB;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class CRUDservice {

    @Inject
    IMongoDB mongoDB;

    @Inject
    MongoExecutionContext mEC;

    public <T> CompletableFuture<List<T>> all(Class<T> type, List<Bson> pipeline, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            return collection
                    .aggregate(pipeline, type)
                    .into(new ArrayList<>());
        }, mEC);
    }

    public <T> CompletableFuture<List<T>> all(Class<T> type, Bson filter, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            return collection
                    .find(filter)
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

    public <T> CompletableFuture<Long> update(Class<T> type, T item, Bson filter, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            UpdateResult result = collection.replaceOne(filter, item);
            return result.getMatchedCount();
        }, mEC);
    }

    public <T> CompletableFuture<Long> delete(Class<T> type, Bson filter, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            DeleteResult result = collection.deleteOne(filter);
            return result.getDeletedCount();
        }, mEC);
    }

    public <T> CompletableFuture<T> find(Class<T> type, String field, String value, String collectionName){
        return find(type, eq(field, value), collectionName);
    }

    public <T> CompletableFuture<T> find(Class<T> type, String field, ObjectId value, String collectionName){
        return find(type, eq(field, value), collectionName);
    }

    public <T> CompletableFuture<T> find(Class<T> type, Bson filter, String collectionName){
        return CompletableFuture.supplyAsync(() -> {
                MongoCollection<T> collection = mongoDB.getMongoDatabase()
                        .getCollection(collectionName, type);

            return collection.find(filter).first();
        }, mEC);
    }

}
