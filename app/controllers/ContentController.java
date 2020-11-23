package controllers;

import actions.Attributes;
import actions.Authenticate;
import actions.Validated;
import com.google.inject.Inject;
import models.Contents.BasicContent;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.CRUDservice;
import services.ContentService;
import services.SerializationService;
import services.UserService;
import utils.DatabaseUtils;

import java.util.concurrent.CompletableFuture;

@Authenticate
public class ContentController extends Controller {

    @Inject
    UserService userService;

    @Inject
    SerializationService serializationService;

    @Inject
    ContentService contentService;

    @Inject
    CRUDservice dbService;


    public CompletableFuture<Result> all(Http.Request request, String dashboardId) {
        return contentService.exists(dashboardId)
                .thenCompose(data -> userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, true))
                .thenCompose(data -> contentService.all(data, dashboardId))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> getContent(Http.Request request, String dashboardId, String contentId) {
        return contentService.exists(dashboardId, contentId)
                .thenCompose(data -> userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, true))
                .thenCompose(data -> contentService.getContent(data, dashboardId, contentId))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    @Validated(value = BasicContent.class)
    public CompletableFuture<Result> update(Http.Request request, String dashboardId, String contentId) {
        return contentService.exists(dashboardId, contentId)
                .thenCompose(data -> userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, false))
                .thenCompose(data -> contentService.update(data, dashboardId, contentId, (BasicContent) request.attrs().get(Attributes.TYPED_KEY)))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> delete(Http.Request request, String dashboardId, String contentId) {
        return contentService.exists(dashboardId, contentId)
                .thenCompose(data -> userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, false))
                .thenCompose(data -> contentService.delete(data, dashboardId, contentId))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    @Validated(value = BasicContent.class)
    public CompletableFuture<Result> save(Http.Request request, String dashboardId) {
        return //contentService.exists(dashboardId, null)
                userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY))
                .thenCompose(data -> contentService.hasAccessToDashboard(data, dashboardId, false))
                .thenCompose((data) -> dbService.save(BasicContent.class, (BasicContent) request.attrs().get(Attributes.TYPED_KEY), "contents"))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }
}
