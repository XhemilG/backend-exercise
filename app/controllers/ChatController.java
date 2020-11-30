package controllers;

import actors.ChatActor;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import org.bson.types.ObjectId;
import play.libs.F;
import play.libs.Json;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.AuthenticationService;
import services.ChatRoomService;
import services.UserService;
import services.Util;
import utils.DatabaseUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChatController extends Controller {
    @Inject
    private ActorSystem actorSystem;

    @Inject
    private Materializer materializer;

    @com.google.inject.Inject
    AuthenticationService authService;

    @com.google.inject.Inject
    UserService userService;

    @com.google.inject.Inject
    ChatRoomService chatRoomService;

    @com.google.inject.Inject
    Util helper;

    public WebSocket chat (String room) {
        return WebSocket.Text.acceptOrResult(requestHeader -> helper.getToken(requestHeader)
                .thenCompose(username -> userService.getUserACL(username))
                .thenCompose(userRoles -> chatRoomService.typeOfAccess(new ObjectId(room), userRoles))
                        .handle((res, ex) -> {
                             if(ex != null) {
                                 Result result = DatabaseUtils.throwableToResult(ex);
                                 return F.Either.Left(result);
                             }

                            String username = authService.parseToken(requestHeader.getHeaders().get("token").get());

                             switch (res) {
                                 case NULL: return F.Either.Left(forbidden("You don't have the required ACL to join this room!"));
                                 case READ: return F.Either.Right(ActorFlow.actorRef((out) -> ChatActor.props(out, room, username, false), actorSystem, materializer));
                                 case WRITE: return F.Either.Right(ActorFlow.actorRef((out) -> ChatActor.props(out, room, username, true), actorSystem, materializer));
                                 case ROOM_NOT_FOUND: return F.Either.Left(notFound("Room not found!"));
                             }
                            return F.Either.Left(internalServerError(Json.toJson("Something went wrong")));
                    })
            );
    }

}