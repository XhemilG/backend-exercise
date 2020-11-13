package controllers;

import com.google.inject.Inject;
import models.LoginRequest;
import models.User;
import play.http.HttpEntity;
import play.mvc.*;
import services.AuthenticationService;
import services.CRUDservice;
import services.SerializationService;
import utils.DatabaseUtils;

import java.util.concurrent.CompletableFuture;

public class AuthenticationController extends Controller {

    @Inject
    SerializationService service;

    @Inject
    AuthenticationService authService;

    @Inject
    CRUDservice mongoService;

    public CompletableFuture<Result> authenticate(Http.Request request) {
        return service.parseBodyOfType(request, LoginRequest.class)
                .thenCompose(data -> mongoService.find(User.class, "username", data.getUsername(), "users-exercise"))
                .thenCompose(data -> authService.generateToken(data))
                .thenApply(data -> new Result(new ResponseHeader(200, data), HttpEntity.NO_ENTITY))
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> checkToken(Http.Request request) {
        return service.getToken(request)
                .thenCompose(data -> authService.parseToken(data))
                .thenCompose((data) -> service.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }
}
