package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import exceptions.RequestException;
import executors.MongoExecutionContext;
import models.Dashboard;
import models.NameValuePair;
import org.bson.types.ObjectId;
import play.libs.Json;

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
    DBservice dbService;

    @Inject
    Util helper;

    private static final String COLLECTION_NAME = "dashboards";

    public CompletableFuture<List<Dashboard>> all(List<ObjectId> objectIdList) {
        return CompletableFuture.supplyAsync(() ->
                    helper.lookup(true, objectIdList, "contents", "_id", "dashboardId"), mEC)
                .thenCompose(data -> dbService.all(Dashboard.class, data, COLLECTION_NAME));
    }

    public CompletableFuture<Dashboard> getDashboard(List<Dashboard> dashboardList, String id) {
        return CompletableFuture.supplyAsync(() -> {
            Dashboard board = new Dashboard();

            for(Dashboard d: dashboardList) {
                if(d.getId().toString().equals(id)) {
                    board = d;
                }
            }
            return hierarchyService.hierarchy(board, dashboardList);
        }, mEC);
    }

    public CompletableFuture<Dashboard> update(List<ObjectId> objectIdList, String id, Dashboard updated) {
        return CompletableFuture.supplyAsync(() ->
                helper.authorizationFilter(false, objectIdList, new NameValuePair("_id", id)), mEC)
                .thenCompose(filter -> dbService.update(Dashboard.class, updated, filter, COLLECTION_NAME))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(FORBIDDEN, "Unauthorized"));
                    }
                    return updated;
                });
    }

    public CompletableFuture<JsonNode> delete(List<ObjectId> objectIdList, String id) {
        return CompletableFuture.supplyAsync(() ->
                helper.authorizationFilter(false, objectIdList, new NameValuePair("_id", id)), mEC)
                .thenCompose(filter -> dbService.delete(Dashboard.class, filter, COLLECTION_NAME))
                .thenApply(result -> {
                    if(result == 0) {
                        throw new CompletionException(new RequestException(FORBIDDEN, "Unauthorized"));
                    }
                    return Json.toJson("Deleted successfully: " + id);
                });
    }

    public CompletableFuture<ObjectId> save(Dashboard dashboard) {
        return CompletableFuture.supplyAsync(() -> {
            dashboard.setTimestamp(System.currentTimeMillis());
            return dashboard;
        }, mEC)
                .thenCompose(data -> dbService.save(Dashboard.class, data, COLLECTION_NAME));
    }


    public CompletableFuture<Boolean> exists(String dashboardId) {
        return CompletableFuture.supplyAsync(() -> helper.getObjectIdIfValid(dashboardId), mEC)
                .thenCompose(data -> dbService.find(Dashboard.class, "_id", data, COLLECTION_NAME))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Dashboard doesn't exist!"));
                    }
                    return true;
                });
    }


}
