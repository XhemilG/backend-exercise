package controllers;

import com.google.inject.Inject;
import models.LoginRequest;
import play.http.HttpEntity;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.ResponseHeader;
import play.mvc.Result;
import services.AuthenticationService;
import services.SerializationService;
import services.UserService;
import utils.DatabaseUtils;

import java.util.concurrent.CompletableFuture;

public class AuthenticationController extends Controller {

    @Inject
    SerializationService service;

    @Inject
    AuthenticationService authService;

    @Inject
    UserService userService;

    public CompletableFuture<Result> authenticate(Http.Request request) {
        return service.parseBodyOfType(request, LoginRequest.class)
                .thenCompose(data -> userService.find(data))
                .thenCompose(data -> authService.generateToken(data))
                .thenApply(data -> new Result(new ResponseHeader(200, data), HttpEntity.NO_ENTITY))
                .exceptionally(DatabaseUtils::throwableToResult);
    }

}
