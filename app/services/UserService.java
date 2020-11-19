package services;

import com.google.inject.Inject;
import executors.MongoExecutionContext;
import models.Role;
import models.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserService {

    @Inject
    CRUDservice dbService;

    @Inject
    MongoExecutionContext mEC;

    private static final String COLLECTION_NAME = "users-exercise";

    public CompletableFuture<List<ObjectId>> getRoles(String id) {
        return dbService.find(User.class, "username", id, COLLECTION_NAME)
                .thenApply(user -> {
                    List<Role> roles = user.getRoles();
                    //roles.add(new Role(user.getId(), "user"));
                    List<ObjectId> res = new ArrayList<>();
                    res.add(user.getId());
                    //roles.forEach(x -> res.add(x.getId().toString()));
                    System.out.println(user);
                    for(Role r: roles) {
                        System.out.println(r);
                        res.add(r.getId());
                    }
                    return res;
                });
    }
}
