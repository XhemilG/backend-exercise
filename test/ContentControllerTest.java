import com.mongodb.client.MongoCollection;
import models.Contents.TextContent;
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


public class ContentControllerTest extends WithApplication {

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

        TextContent content = new TextContent();
        content.setId(new ObjectId("5fb52536320e8fa591d40abc"));
        content.setDashboardId(new ObjectId("5fb52536320e8fa591d40c9c"));
        content.setReadACL(Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")));
        content.setWriteACL(Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")));
        content.setText("first");

        Http.RequestBuilder contentInsertRequest = new Http.RequestBuilder().method("POST").uri("/api/dashboard/5fb52536320e8fa591d40c9c/content");
        contentInsertRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");
        contentInsertRequest.bodyJson(Json.toJson(content));

        Result contentInsertResult = route(app, contentInsertRequest);
        assertEquals(OK, contentInsertResult.status());
    }

    @Test
    public void contentUpdateTest() {
        final Http.RequestBuilder contentUpdateRequest = new Http.RequestBuilder().method("PUT").uri("/api/dashboard/5fb52536320e8fa591d40c9c/content/5fb52536320e8fa591d40abc");
        contentUpdateRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");

        TextContent updatedContent = new TextContent();
        updatedContent.setId(new ObjectId("5fb52536320e8fa591d40abc"));
        updatedContent.setDashboardId(new ObjectId("5fb52536320e8fa591d40c9c"));
        updatedContent.setReadACL(Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")));
        updatedContent.setWriteACL(Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")));
        updatedContent.setText("updated");
        contentUpdateRequest.bodyJson(Json.toJson(updatedContent));

        final Result result = route(app, contentUpdateRequest);
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains(Json.toJson(updatedContent).asText()));
    }

    @Test
    public void contentForbiddenTest() {
        final Http.RequestBuilder contentDeleteRequest = new Http.RequestBuilder().method("DELETE").uri("/api/dashboard/5fb52536320e8fa591d40c9c/content/5fb52536320e8fa591d40abc");
        contentDeleteRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoiR3VpZG8ifQ.ha-BmuT9yLsHRjtgt2qIjVWQ2DisiyZO_N31rdKHz5Y");

        final Result result = route(app, contentDeleteRequest);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void contentSaveBadRequestTest() {
        final Http.RequestBuilder contentSaveRequest = new Http.RequestBuilder().method("POST").uri("/api/dashboard/5fb52536320e8fa591d40c9c/content");
        contentSaveRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");

        TextContent content = new TextContent();
        content.setId(new ObjectId("5fb52536320e8fa591d40abc"));
        content.setDashboardId(new ObjectId("5fb52536320e8fa591d40c9c"));
        content.setReadACL(Arrays.asList(new ObjectId("5fb524a9320e8fa591d40c60")));
        content.setWriteACL(Arrays.asList(new ObjectId("5fb5088c320e8fa591d403bd")));
        content.setText(null);

        contentSaveRequest.bodyJson(Json.toJson(content));

        final Result result = route(app, contentSaveRequest);
        assertEquals(BAD_REQUEST, result.status());
        assertTrue(contentAsString(result).contains(Json.toJson(content).asText()));
    }

    @Test
    public void contentGetNotFoundTest() {
        final Http.RequestBuilder contentGetRequest = new Http.RequestBuilder().method("GET").uri("/api/dashboard/5fb52536320e8fa591d40c9c/content/5fb52536320e8fa591d4bbb");
        contentGetRequest.header("token", "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZW50IjoidXNlcjEifQ.CsnbZHxAI8yEogLOPiKzOmV9YlE9LiZyT9Fx3IGejDo");

        final Result result = route(app, contentGetRequest);
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
