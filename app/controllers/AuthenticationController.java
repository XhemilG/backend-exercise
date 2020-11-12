package controllers;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;

public class AuthenticationController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> authenticate(Http.Request request) {
        //TODO
        return CompletableFuture.completedFuture(ok());
    }
}
