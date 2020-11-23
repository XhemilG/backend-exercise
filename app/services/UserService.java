package services;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import exceptions.RequestException;
import models.LoginRequest;
import models.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static play.mvc.Http.Status.NOT_FOUND;

public class UserService {

    @Inject
    CRUDservice dbService;

    private static final String COLLECTION_NAME = "users-exercise";

    public CompletableFuture<User> find(LoginRequest request) {
        return dbService.find(User.class, Filters.and(Filters.eq("username", request.getUsername()), Filters.eq("password", request.getPassword())), COLLECTION_NAME)
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Check credentials!"));
                    }
                    return result;
                });
    }

    public CompletableFuture<List<ObjectId>> getRoles(String username) {
        return dbService.find(User.class, "username", username, COLLECTION_NAME)
                .thenApply(user -> {
                    try {
                        List<ObjectId> res = new ArrayList<>();
                        res.add(user.getId());
                        user.getRoles().forEach(x -> res.add(x.getId()));
                        return res;
                    } catch (NullPointerException ex) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Credentials are incorrect or the user doesn't exist!"));
                    }
                });
    }
}
