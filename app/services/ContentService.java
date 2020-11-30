package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import exceptions.RequestException;
import executors.MongoExecutionContext;
import models.Contents.Content;
import models.Dashboard;
import models.NameValuePair;
import org.bson.types.ObjectId;
import play.libs.Json;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.NOT_FOUND;

public class ContentService {

    @Inject
    MongoExecutionContext mEC;

    @Inject
    DBservice dbService;

    @Inject
    Util helper;

    private static final String COLLECTION_NAME = "contents";

    public CompletableFuture<List<Content>> all(List<ObjectId> objectIdList, String dashboardId) {
        return CompletableFuture.supplyAsync(() ->
                    helper.authorizationFilter(true, objectIdList, new NameValuePair("dashboardId", dashboardId)), mEC)
                .thenCompose(filter -> dbService.all(Content.class, filter, COLLECTION_NAME))
                .thenApply(result -> {
                    if(result == null || result.isEmpty()) {
                        throw new CompletionException(new RequestException(FORBIDDEN, "Unauthorized"));
                    }
                    return result;
                });
    }

    public CompletableFuture<Content> getContent(List<ObjectId> objectIdList, String dashboardId, String contentId) {
        return CompletableFuture.supplyAsync(() ->
                        helper.authorizationFilter(true, objectIdList, new NameValuePair("dashboardId", dashboardId),
                                new NameValuePair("_id", contentId)), mEC)
                .thenCompose(filter -> dbService.find(Content.class, filter, COLLECTION_NAME))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(FORBIDDEN, "Unauthorized"));
                    }
                    return result;
                });
    }

    public CompletableFuture<Content> update(List<ObjectId> objectIdList, String dashboardId, String contentId, Content updated) {
        return CompletableFuture.supplyAsync(() ->
                        helper.authorizationFilter(false, objectIdList, new NameValuePair("_id", contentId),
                                new NameValuePair("dashboardId", dashboardId)), mEC)
                .thenCompose(filter -> dbService.update(Content.class, updated, filter, COLLECTION_NAME))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(FORBIDDEN, "Unauthorized"));
                    }
                    return result;
                });
    }

    public CompletableFuture<JsonNode> delete(List<ObjectId> objectIdList, String dashboardId, String contentId) {
        return CompletableFuture.supplyAsync(() ->
                    helper.authorizationFilter(false, objectIdList, new NameValuePair("_id", contentId),
                            new NameValuePair("dashboardId", dashboardId)), mEC)
                .thenCompose(filter -> dbService.delete(Content.class, filter, COLLECTION_NAME))
                .thenApply(result -> {
                    if(result == 0) {
                        throw new CompletionException(new RequestException(FORBIDDEN, "Unauthorized"));
                    }
                    return Json.toJson("Deleted successfully: " + contentId);
                });
    }

    public CompletableFuture<Boolean> exists(String dashboardId) {
        return CompletableFuture.supplyAsync(() -> helper.getObjectIdIfValid(dashboardId), mEC)
                .thenCompose(data -> dbService.find(Dashboard.class, "_id", data, "dashboards"))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Dashboard doesn't exist!"));
                    }
                    else return true;
                });
    }

    public CompletableFuture<Boolean> exists(String dashboardId, String contentId) {
        return CompletableFuture.supplyAsync(() -> helper.getObjectIdIfValid(contentId), mEC)
                .thenCompose(data -> dbService.find(Content.class, "_id", data, COLLECTION_NAME))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Widget doesn't exist!"));
                    }
                    else return true;
                })
                .thenCompose(data -> exists(dashboardId));
    }


    public CompletableFuture<List<ObjectId>> hasAccessToDashboard(List<ObjectId> objectIdList, String dashboardId, boolean read) {
        return CompletableFuture.supplyAsync(() ->
                helper.authorizationFilter(read, objectIdList, new NameValuePair("_id", dashboardId)), mEC)
                .thenCompose(filter ->  dbService.find(Dashboard.class, filter, "dashboards"))
                .thenApply(data -> {
                        if(data == null) {
                            throw new CompletionException(new RequestException(FORBIDDEN, "Unauthorized"));
                        }
                        return data;
                })
                .thenApply((data) -> objectIdList);
    }

}
