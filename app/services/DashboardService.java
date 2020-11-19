package services;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Variable;
import executors.MongoExecutionContext;
import models.Dashboard;
import mongo.IMongoDB;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DashboardService {

    @Inject
    IMongoDB mongoDB;

    @Inject
    MongoExecutionContext mEC;

    public CompletableFuture<List<Dashboard>> all(List<ObjectId> objectIdList) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> collection = mongoDB.getMongoDatabase()
                    .getCollection("dashboards", Dashboard.class);

            List<Bson> pipeline = new ArrayList<>();

            pipeline.add(
                    Aggregates.match(
                            Filters.or(
                                    Filters.in("readACL", objectIdList),
                                    Filters.in("writeACL", objectIdList)
                            )
                    )
            );

            List<Bson> contentsPipeline = new ArrayList<>();

            contentsPipeline.add(
                    Aggregates.match(
                            new Document("$expr",
                                    new Document("$eq",
                                            Arrays.asList("$dashboardId", "$$id")))
                    )
            );

            contentsPipeline.add(
                    Aggregates.match(
                            Filters.or(
                                    Filters.in("readACL", objectIdList),
                                    Filters.in("writeACL", objectIdList)
                            )
                    )
            );

            List<Variable<String>> variables = new ArrayList<>();
            variables.add(new Variable<>("id", "$_id"));

            pipeline.add(
                    Aggregates.lookup("contents", variables , contentsPipeline, "contents")
            );

            List<Dashboard> aggregated = collection
                    .aggregate(pipeline, Dashboard.class)
                    .into(new ArrayList<>());

            return aggregated;
        }, mEC);
    }

}
