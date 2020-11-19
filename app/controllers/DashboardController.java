package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import executors.MongoExecutionContext;
import models.Dashboard;
import mongo.IMongoDB;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.CRUDservice;
import services.DashboardService;
import services.SerializationService;
import services.UserService;
import utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DashboardController extends Controller {

    @Inject
    AuthenticationController auth;

    @Inject
    UserService userService;

    @Inject
    SerializationService serializationService;

    @Inject
    DashboardService dashboardService;

    @Inject
    MongoExecutionContext mEC;
    @Inject
    CRUDservice dbService;


    public CompletableFuture<Result> all(Http.Request request) {
        return auth.checkToken(request)
                .thenCompose(data -> userService.getRoles(data))
                .thenCompose(data -> dashboardService.all(data))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                ;//.exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> save(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode json = request.body().asJson();

            Dashboard d = Json.fromJson(json, Dashboard.class);

            d.setId(new ObjectId());
            dbService.save(Dashboard.class, d, "dashboards");

            return ok(Json.toJson(d));
        }, mEC);
    }


}
