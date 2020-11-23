package controllers;

import actions.Attributes;
import actions.Authenticate;
import actions.Validated;
import com.google.inject.Inject;
import models.Dashboard;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.*;
import utils.DatabaseUtils;

import java.util.concurrent.CompletableFuture;

@Authenticate
public class DashboardController extends Controller {

    @Inject
    UserService userService;

    @Inject
    SerializationService serializationService;

    @Inject
    DashboardService dashboardService;

    @Inject
    HierarchyService hierarchyService;

    @Inject
    CRUDservice dbService;

    public CompletableFuture<Result> all(Http.Request request) {
        return userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY))
                .thenCompose(data -> dashboardService.all(data))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }


    public CompletableFuture<Result> hierarchy(Http.Request request) {
        return userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY))
                .thenCompose(data -> dashboardService.all(data))
                .thenCompose(dashboards -> hierarchyService.hierarchy(dashboards))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> getDashboard(Http.Request request, String id) {
        return userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY))
                .thenCompose(data -> dashboardService.all(data))
                .thenCompose(dashboards -> dashboardService.getDashboard(dashboards, id))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    @Validated(value = Dashboard.class)
    public CompletableFuture<Result> update(Http.Request request, String id) {
        return dashboardService.exists(id)
                .thenCompose(data -> userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> dashboardService.update(data, id, (Dashboard) request.attrs().get(Attributes.TYPED_KEY)))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> delete(Http.Request request, String id) {
        return dashboardService.exists(id)
                .thenCompose(data -> userService.getRoles(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(data -> dashboardService.delete(data, id))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    @Validated(value = Dashboard.class)
    public CompletableFuture<Result> save(Http.Request request) {
        return dbService.save(Dashboard.class, (Dashboard) request.attrs().get(Attributes.TYPED_KEY), "dashboards")
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }


}
