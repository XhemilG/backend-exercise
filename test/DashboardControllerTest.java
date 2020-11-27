import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.typesafe.config.Config;
import models.Dashboard;
import models.Role;
import models.User;
import mongo.IMongoDB;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class DashboardControllerTest extends WithApplication {

    @Inject
    IMongoDB mongoDB;

    @Before
    @Override
    public void startPlay() {
        super.startPlay();
        System.out.println(mongoDB);
        MongoCollection<User> userCollection = mongoDB.getMongoDatabase()
                .getCollection("users-exercise", User.class);

        User user = new User(new ObjectId("5fb5088c320e8fa591d403bd"), "user1", "password",
                        Arrays.asList(new Role(new ObjectId("5fb5088c320e8fa591d403be"), "admin")));

        User user2 = new User(new ObjectId("5fb524a9320e8fa591d40c60"), "Guido", "password2",
                Arrays.asList(new Role(new ObjectId("5fb5088c320e8fa591d403bf"), "support"),
                              new Role(new ObjectId("5fb5088c320e8fa591d403ab"), "marketing")));

        userCollection.insertOne(user);
        userCollection.insertOne(user2);

        MongoCollection<Dashboard> dashboardCollection = mongoDB.getMongoDatabase()
                .getCollection("dashboards", Dashboard.class);

        Dashboard dashboard = new Dashboard(new ObjectId("5fb52536320e8fa591d40c9c"), "Retail cockpit", "Retail cockpit (overview)", null,
                1111L, Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")), Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")),
                new ArrayList<>(), new ArrayList<>());

        Dashboard dashboard2 = new Dashboard(new ObjectId("5fb5258e320e8fa591d40cc7"), "Assortment optimization", "Assortment optimization (overview)", new ObjectId("5fb52536320e8fa591d40c9c"),
                1111L, Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")), Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")),
                new ArrayList<>(), new ArrayList<>());

        dashboardCollection.insertOne(dashboard);
        dashboardCollection.insertOne(dashboard2);
    }

    @Test
    public void dashboardOkCase() {
        final Http.RequestBuilder dashboardUpdateRequest = new Http.RequestBuilder().method("PUT").uri("/api/dashboard/5fb52536320e8fa591d40c9c");
        dashboardUpdateRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");

        Dashboard updatedDashboard = new Dashboard(new ObjectId("5fb52536320e8fa591d40c9c"), "Updated", "Retail cockpit (overview)", null,
                1111L, Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")), Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")),
                new ArrayList<>(), new ArrayList<>());

        dashboardUpdateRequest.bodyJson(Json.toJson(updatedDashboard));

        final Result result = route(app, dashboardUpdateRequest);
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains(Json.toJson(updatedDashboard).asText()));
    }

}
