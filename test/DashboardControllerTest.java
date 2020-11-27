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

        Dashboard dashboard = new Dashboard(new ObjectId("5fb52536320e8fa591d40c9c"), "Retail cockpit", "Retail cockpit (overview)", null,
                System.currentTimeMillis(), Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")), Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")),
                new ArrayList<>(), new ArrayList<>());

        Dashboard dashboard2 = new Dashboard(new ObjectId("5fb5258e320e8fa591d40cc7"), "Assortment optimization", "Assortment optimization (overview)", new ObjectId("5fb52536320e8fa591d40c9c"),
                System.currentTimeMillis(), Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")), Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")),
                new ArrayList<>(), new ArrayList<>());

        Http.RequestBuilder dashboardInsertRequest = new Http.RequestBuilder().method("POST").uri("/api/dashboard");
        dashboardInsertRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");
        dashboardInsertRequest.bodyJson(Json.toJson(dashboard));

        Result insertResult = route(app, dashboardInsertRequest);
        assertEquals(OK, insertResult.status());

        dashboardInsertRequest = new Http.RequestBuilder().method("POST").uri("/api/dashboard");
        dashboardInsertRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");
        dashboardInsertRequest.bodyJson(Json.toJson(dashboard2));

        insertResult = route(app, dashboardInsertRequest);
        assertEquals(OK, insertResult.status());
    }

    @Test
    public void dashboardUpdateTest() {
        final Http.RequestBuilder dashboardUpdateRequest = new Http.RequestBuilder().method("PUT").uri("/api/dashboard/5fb52536320e8fa591d40c9c");
        dashboardUpdateRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");

        Dashboard updatedDashboard = new Dashboard(new ObjectId("5fb52536320e8fa591d40c9c"), "Updated", "Retail cockpit (overview)", null,
                System.currentTimeMillis(), Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")), Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")),
                new ArrayList<>(), new ArrayList<>());

        dashboardUpdateRequest.bodyJson(Json.toJson(updatedDashboard));

        final Result result = route(app, dashboardUpdateRequest);
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains(Json.toJson(updatedDashboard).asText()));
    }

    @Test
    public void dashboardDeleteForbiddenTest() {
        final Http.RequestBuilder dashboardDeleteRequest = new Http.RequestBuilder().method("DELETE").uri("/api/dashboard/5fb52536320e8fa591d40c9c");
        dashboardDeleteRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoiR3VpZG8ifQ.ha-BmuT9yLsHRjtgt2qIjVWQ2DisiyZO_N31rdKHz5Y");

        final Result result = route(app, dashboardDeleteRequest);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void dashboardSaveBadRequestTest() {
        final Http.RequestBuilder dashboardSaveRequest = new Http.RequestBuilder().method("POST").uri("/api/dashboard");
        dashboardSaveRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");

        Dashboard updatedDashboard = new Dashboard(new ObjectId("5fb52536320e8fa591d40c94"), null, "Retail cockpit (overview)", null,
                System.currentTimeMillis(), Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")), Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")),
                new ArrayList<>(), new ArrayList<>());

        dashboardSaveRequest.bodyJson(Json.toJson(updatedDashboard));

        final Result result = route(app, dashboardSaveRequest);
        assertEquals(BAD_REQUEST, result.status());
        assertTrue(contentAsString(result).contains(Json.toJson(updatedDashboard).asText()));
    }

    @Test
    public void dashboardGetNotFoundTest() {
        final Http.RequestBuilder dashboardGetRequest = new Http.RequestBuilder().method("GET").uri("/api/dashboard/5fb52536320e8fa591d40c94");
        dashboardGetRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");

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
