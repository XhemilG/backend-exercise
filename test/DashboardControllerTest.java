import com.mongodb.client.MongoCollection;
import models.Dashboard;
import models.Role;
import models.User;
import mongo.InMemoryMongoDB;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;


public class DashboardControllerTest extends WithApplication {

    InMemoryMongoDB mongoDB;

    private final String AUTHORIZED_USER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo";
    private final String UNAUTHORIZED_USER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoiR3VpZG8ifQ.ha-BmuT9yLsHRjtgt2qIjVWQ2DisiyZO_N31rdKHz5Y";

    Dashboard dashboard = new Dashboard(new ObjectId("5fb52536320e8fa591d40c9c"), "Retail cockpit", "Retail cockpit (overview)", null,
            System.currentTimeMillis(), Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")), Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")),
            new ArrayList<>(), new ArrayList<>());

    @Before
    @Override
    public void startPlay() {
        super.startPlay();
        mongoDB = app.injector().instanceOf(InMemoryMongoDB.class);

        MongoCollection<User> userCollection = mongoDB.getMongoDatabase()
                .getCollection("users-exercise", User.class);

        Logger.of(Constants.CLASS).debug("Mongo Collection {} ", userCollection.countDocuments());
        User user = new User(new ObjectId("5fb5088c320e8fa591d403bd"), "user1", "password",
                Arrays.asList(new Role(new ObjectId("5fb5088c320e8fa591d403be"), "admin")));

        User user2 = new User(new ObjectId("5fb524a9320e8fa591d40c60"), "Guido", "password2",
                Arrays.asList(new Role(new ObjectId("5fb5088c320e8fa591d403bf"), "support"),
                        new Role(new ObjectId("5fb5088c320e8fa591d403ab"), "marketing")));

        userCollection.insertOne(user);
        userCollection.insertOne(user2);

        final Http.RequestBuilder dashboardInsertRequest = new Http.RequestBuilder().method("POST").uri("/api/dashboard");
        dashboardInsertRequest.header("token", AUTHORIZED_USER_TOKEN);
        dashboardInsertRequest.bodyJson(Json.toJson(dashboard));

        final Result insertResult = route(app, dashboardInsertRequest);
        assertEquals(OK, insertResult.status());
    }

    @Test
    public void dashboardUpdateTest() {
        final Http.RequestBuilder dashboardUpdateRequest = new Http.RequestBuilder().method("PUT").uri("/api/dashboard/5fb52536320e8fa591d40c9c");
        dashboardUpdateRequest.header("token", AUTHORIZED_USER_TOKEN);

        dashboard.setName("updated");
        dashboardUpdateRequest.bodyJson(Json.toJson(dashboard));

        final Result result = route(app, dashboardUpdateRequest);
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains(Json.toJson(dashboard).asText()));
    }

    @Test
    public void dashboardDeleteForbiddenTest() {
        final Http.RequestBuilder dashboardDeleteRequest = new Http.RequestBuilder().method("DELETE").uri("/api/dashboard/5fb52536320e8fa591d40c9c");
        dashboardDeleteRequest.header("token", UNAUTHORIZED_USER_TOKEN);
        dashboardDeleteRequest.header("Content-Type", "application/json");

        final Result result = route(app, dashboardDeleteRequest);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void dashboardSaveBadRequestTest() {
        final Http.RequestBuilder dashboardSaveRequest = new Http.RequestBuilder().method("POST").uri("/api/dashboard");
        dashboardSaveRequest.header("token", AUTHORIZED_USER_TOKEN);

        dashboard.setId(new ObjectId("5fb52536320e8fa591d40c94"));
        dashboard.setName(null);

        dashboardSaveRequest.bodyJson(Json.toJson(dashboard));

        final Result result = route(app, dashboardSaveRequest);
        assertEquals(BAD_REQUEST, result.status());
        assertTrue(contentAsString(result).contains(Json.toJson(dashboard).asText()));
    }

    @Test
    public void dashboardGetNotFoundTest() {
        final Http.RequestBuilder dashboardGetRequest = new Http.RequestBuilder().method("GET").uri("/api/dashboard/5fb52536320e8fa591d40c94");
        dashboardGetRequest.header("token", AUTHORIZED_USER_TOKEN);

        final Result result = route(app, dashboardGetRequest);
        assertEquals(NOT_FOUND, result.status());
    }

    @After
    @Override
    public void stopPlay() {
        super.stopPlay();
        mongoDB.getMongoDatabase().drop();
        mongoDB.disconnect();
    }


}
