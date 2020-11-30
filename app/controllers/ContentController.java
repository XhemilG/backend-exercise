package controllers;

import actions.Attributes;
import actions.Authenticated;
import actions.Validated;
import com.google.inject.Inject;
import models.Contents.Content;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.DBservice;
import services.ContentService;
import services.SerializationService;
import services.UserService;
import utils.DatabaseUtils;

import java.util.concurrent.CompletableFuture;

@Authenticated
public class ContentController extends Controller {

    @Inject
    UserService userService;

    @Inject
    SerializationService serializationService;

    @Inject
    ContentService contentService;

    @Inject
    DBservice dbService;


    public CompletableFuture<Result> all(Http.Request request, String dashboardId) {
        return contentService.exists(dashboardId)
                .thenCompose(data -> userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, true))
                .thenCompose(data -> contentService.all(data, dashboardId))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> getContent(Http.Request request, String dashboardId, String contentId) {
        return contentService.exists(dashboardId, contentId)
                .thenCompose(data -> userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, true))
                .thenCompose(data -> contentService.getContent(data, dashboardId, contentId))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    @Validated(value = Content.class)
    public CompletableFuture<Result> update(Http.Request request, String dashboardId, String contentId) {
        return contentService.exists(dashboardId, contentId)
                .thenCompose(data -> userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, false))
                .thenCompose(data -> contentService.update(data, dashboardId, contentId, (Content) request.attrs().get(Attributes.TYPED_KEY)))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> delete(Http.Request request, String dashboardId, String contentId) {
        return contentService.exists(dashboardId, contentId)
                .thenCompose(data -> userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, false))
                .thenCompose(data -> contentService.delete(data, dashboardId, contentId))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    @Validated(value = Content.class)
    public CompletableFuture<Result> save(Http.Request request, String dashboardId) {
        return contentService.exists(dashboardId)
                .thenCompose(data -> userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, false))
                .thenCompose((data) -> dbService.save(Content.class, (Content) request.attrs().get(Attributes.TYPED_KEY), "contents"))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }
}
