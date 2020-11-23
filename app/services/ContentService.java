package services;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import exceptions.RequestException;
import executors.MongoExecutionContext;
import models.Contents.BasicContent;
import models.Dashboard;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.NOT_FOUND;

public class ContentService {

    @Inject
    MongoExecutionContext mEC;

    @Inject
    CRUDservice dbService;

    public CompletableFuture<List<BasicContent>> all(List<ObjectId> objectIdList, String dashboardId) {
        return CompletableFuture.supplyAsync(() ->
                    Filters.and(
                            Filters.or(
                                    Filters.and(
                                            Filters.size("readACL", 0),
                                            Filters.size("writeACL", 0)
                                    ),
                                    Filters.or(
                                            Filters.in("readACL", objectIdList),
                                            Filters.in("writeACL", objectIdList)
                                    )
                            ),
                            Filters.eq("dashboardId", new ObjectId(dashboardId))
                    ), mEC)
                .thenCompose(filter -> dbService.all(BasicContent.class, filter, "contents"))
                .thenApply(result -> {
                    if(result == null || result.isEmpty()) {
                        throw new CompletionException(new RequestException(FORBIDDEN, Json.toJson("Unauthorized")));
                    }
                    return result;
                });
    }

    public CompletableFuture<BasicContent> getContent(List<ObjectId> objectIdList, String dashboardId, String contentId) {
        return CompletableFuture.supplyAsync(() ->
                    Filters.and(
                            Filters.or(
                                    Filters.and(
                                            Filters.size("readACL", 0),
                                            Filters.size("writeACL", 0)
                                    ),
                                    Filters.or(
                                            Filters.in("readACL", objectIdList),
                                            Filters.in("writeACL", objectIdList)
                                    )
                            ),
                        Filters.eq("dashboardId", new ObjectId(dashboardId)),
                        Filters.eq("_id", new ObjectId(contentId))
                    ), mEC)
                .thenCompose(filter -> dbService.find(BasicContent.class, filter, "contents"))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(FORBIDDEN, Json.toJson("Unauthorized")));
                    }
                    return result;
                });
    }

    public CompletableFuture<BasicContent> update(List<ObjectId> objectIdList, String dashboardId, String contentId, BasicContent updated) {
        return write(objectIdList, dashboardId, contentId)
                .thenCompose(filter -> dbService.update(BasicContent.class, updated, filter, "contents"))
                .thenApply(result -> {
                    if(!result) {
                        throw new CompletionException(new RequestException(FORBIDDEN, Json.toJson("Unauthorized")));
                    }
                    return updated;
                });
    }

    public CompletableFuture<String> delete(List<ObjectId> objectIdList, String dashboardId, String contentId) {
        return write(objectIdList, dashboardId, contentId)
                .thenCompose(filter -> dbService.delete(BasicContent.class, filter, "contents"))
                .thenApply(result -> {
                    if(result == false) {
                        throw new CompletionException(new RequestException(FORBIDDEN, Json.toJson("Unauthorized")));
                    }
                    return "Deleted successfully: " + contentId;
                });
    }

    public CompletableFuture<Bson> write(List<ObjectId> objectIdList, String dashboardId, String contentId) {
        return CompletableFuture.supplyAsync(() ->
                Filters.and(
                        Filters.or(
                                Filters.and(
                                        Filters.size("readACL", 0),
                                        Filters.size("writeACL", 0)
                                ),
                                Filters.in("writeACL", objectIdList)
                        ),
                        Filters.eq("dashboardId", new ObjectId(dashboardId)),
                        Filters.eq("_id", new ObjectId(contentId))
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
                .thenCompose(data -> dbService.find(Dashboard.class, "_id", data, "dashboards"))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Dashboard doesn't exist!"));
                    }
                    else return true;
                }).thenCompose(result -> dbService.all(BasicContent.class, Filters.eq("dashboardId", new ObjectId(dashboardId)), "contents"))
                .thenApply(result -> {
                    if(result == null || result.isEmpty()) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Widget doesn't exist!"));
                    }
                    else return true;
                });
    }

    public CompletableFuture<Boolean> exists(String dashboardId, String contentId) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                return new ObjectId(contentId);
            } catch (IllegalArgumentException ex) {
                throw new CompletionException(new RequestException(NOT_FOUND, "Widget doesn't exist!"));
            }
        }, mEC)
                .thenCompose(data -> dbService.find(BasicContent.class, "_id", data, "contents"))
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Widget doesn't exist!"));
                    }
                    else return true;
                })
                .thenCompose(data -> exists(dashboardId));
    }


    public CompletableFuture<List<ObjectId>> hasAccessToDashboard(List<ObjectId> objectIdList, String dashboardId, boolean read) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Bson> readWriteFilters = new ArrayList<>();
                readWriteFilters.add(Filters.in("writeACL", objectIdList));

                if(read) {
                    readWriteFilters.add(Filters.in("readACL", objectIdList));
                }

                Bson authorizationFilter = Filters.or(readWriteFilters);

                Bson filter =
                        Filters.and(
                                authorizationFilter,
                                Filters.eq("_id", new ObjectId(dashboardId))
                        );

                return filter;
            } catch (IllegalArgumentException ex) {
                throw new CompletionException(new RequestException(NOT_FOUND, "Unauthorized"));
            }
        }, mEC)
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
