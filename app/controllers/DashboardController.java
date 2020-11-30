package controllers;

import actions.Attributes;
import actions.Authenticated;
import actions.Validated;
import com.google.inject.Inject;
import models.Dashboard;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.DashboardService;
import services.HierarchyService;
import services.SerializationService;
import services.UserService;
import utils.DatabaseUtils;

import java.util.concurrent.CompletableFuture;

@Authenticated
public class DashboardController extends Controller {

    @Inject
    UserService userService;

    @Inject
    SerializationService serializationService;

    @Inject
    DashboardService dashboardService;

    @Inject
    HierarchyService hierarchyService;

    public CompletableFuture<Result> all(Http.Request request) {
        return userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY))
                .thenCompose(userACL -> dashboardService.all(userACL))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }


    public CompletableFuture<Result> hierarchy(Http.Request request) {
        return userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY))
                .thenCompose(userACL -> dashboardService.all(userACL))
                .thenCompose(dashboards -> hierarchyService.hierarchy(dashboards))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> getDashboard(Http.Request request, String id) {
        return dashboardService.exists(id)
                .thenCompose(data -> userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(userACL -> dashboardService.all(userACL))
                .thenCompose(dashboards -> dashboardService.getDashboard(dashboards, id))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    @Validated(value = Dashboard.class)
    public CompletableFuture<Result> update(Http.Request request, String id) {
        return dashboardService.exists(id)
                .thenCompose(data -> userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(userACL -> dashboardService.update(userACL, id, (Dashboard) request.attrs().get(Attributes.TYPED_KEY)))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> delete(Http.Request request, String id) {
        return dashboardService.exists(id)
                .thenCompose(data -> userService.getUserACL(request.attrs().get(Attributes.AUTHENTICATION_TYPED_KEY)))
                .thenCompose(userACL -> dashboardService.delete(userACL, id))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    @Validated(value = Dashboard.class)
    public CompletableFuture<Result> save(Http.Request request) {
        return dashboardService.save((Dashboard) request.attrs().get(Attributes.TYPED_KEY))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }


}
