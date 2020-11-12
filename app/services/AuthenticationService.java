package services;

import com.google.inject.Inject;
import models.LoginRequest;
import models.User;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;

public class AuthenticationService {

    @Inject
    CRUDservice service;

    public CompletableFuture<Result> authenticate(LoginRequest loginRequest) {
        return service.find(User.class, loginRequest, "")
                .
    }
}
