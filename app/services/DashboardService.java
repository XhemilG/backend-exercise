package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Variable;
import exceptions.RequestException;
import executors.MongoExecutionContext;
import models.Dashboard;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.libs.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.NOT_FOUND;

public class DashboardService {

    @Inject
    MongoExecutionContext mEC;

    @Inject
    HierarchyService hierarchyService;

    @Inject
    CRUDservice dbService;

    public CompletableFuture<List<Dashboard>> all(List<ObjectId> objectIdList) {
        return CompletableFuture.supplyAsync(() -> {
            List<Bson> pipeline = new ArrayList<>();

            pipeline.add(
                    Aggregates.match(
                            Filters.or(
                                    Filters.and(
                                            Filters.size("readACL", 0),
                                            Filters.size("writeACL", 0)
                                    ),
                                    Filters.or(
                                            Filters.in("readACL", objectIdList),
                                            Filters.in("writeACL", objectIdList)
                                    )
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
                                    Filters.and(
                                            Filters.size("readACL", 0),
                                            Filters.size("writeACL", 0)
                                    ),
                                    Filters.or(
                                            Filters.in("readACL", objectIdList),
                                            Filters.in("writeACL", objectIdList)
                                    )
                            )
                    )
            );

            List<Variable<String>> variables = new ArrayList<>();
            variables.add(new Variable<>("id", "$_id"));

            pipeline.add(
                    Aggregates.lookup("contents", variables , contentsPipeline, "contents")
            );

            return pipeline;
        }, mEC)
                .thenCompose(data -> dbService.all(Dashboard.class, data, "dashboards"));
    }

    public CompletableFuture<Dashboard> getDashboard(List<Dashboard> dashboardList, String id) {
        return CompletableFuture.supplyAsync(() -> {
            Dashboard board = new Dashboard();

            for(Dashboard d: dashboardList) {
                if(d.getId().toString().equals(id))
                    board = d;
            }

            return hierarchyService.hierarchy(board, dashboardList);
        }, mEC);
    }

    public CompletableFuture<Dashboard> update(List<ObjectId> objectIdList, String dashboardId, Dashboard updated) {
        return write(objectIdList, dashboardId)
                .thenCompose(filter -> dbService.update(Dashboard.class, updated, filter, "dashboards"))
                .thenApply(result -> {
                    if(result == 0) {
                        throw new CompletionException(new RequestException(FORBIDDEN, Json.toJson("Unauthorized")));
                    }
                    return updated;
                });
    }

    public CompletableFuture<JsonNode> delete(List<ObjectId> objectIdList, String dashboardId) {
        return write(objectIdList, dashboardId)
                .thenCompose(filter -> dbService.delete(Dashboard.class, filter, "dashboards"))
                .thenApply(result -> {
                    if(result == 0) {
                        throw new CompletionException(new RequestException(FORBIDDEN, Json.toJson("Unauthorized")));
                    }
                    return Json.toJson("Deleted successfully: " + dashboardId);
                });
    }

    public CompletableFuture<Bson> write(List<ObjectId> objectIdList, String id) {
        return CompletableFuture.supplyAsync(() ->
                Filters.and(
                        Filters.in("writeACL", objectIdList),
                        Filters.eq("_id", new ObjectId(id))
                ), mEC);
    }

    public CompletableFuture<Boolean> exists(String dashboardId) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                return new ObjectId(dashboardId);
            } catch (IllegalArgumentException ex) {
                throw new CompletionException(new RequestException(NOT_FOUND, "Dashboard doesn't exist!"));
            }
        }, mEC)
                .thenCompose((data) -> dbService.find(Dashboard.class, "_id", data, "dashboards"))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Dashboard doesn't exist!"));
                    }
                    else return true;
                });
    }


}
