package controllers;

import actors.ChatActor;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import exceptions.RequestException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.bson.types.ObjectId;
import play.libs.F;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.WebSocket;
import services.AuthenticationService;
import services.ChatRoomService;
import services.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Singleton
public class ChatController extends Controller {
    @Inject
    private ActorSystem actorSystem;

    @Inject
    private Materializer materializer;

    @com.google.inject.Inject
    AuthenticationService authService;

    @com.google.inject.Inject
    HttpExecutionContext ec;

    @com.google.inject.Inject
    UserService userService;

    @com.google.inject.Inject
    ChatRoomService chatRoomService;

    public WebSocket chat (String room) {
        return WebSocket.Text.acceptOrResult(requestHeader ->
                CompletableFuture.supplyAsync(() -> {
                    try{
                        String token = requestHeader.getHeaders().get("token").get();
                        return authService.parseToken(token);
                    } catch (NoSuchElementException | SignatureException | ExpiredJwtException ex) {
                        throw new CompletionException(new RequestException(FORBIDDEN, ex.getMessage()));
                    }
                }, ec.current())
                .thenCompose(username -> userService.getRoles(username))
                .thenCompose(userRoles -> chatRoomService.typeOfAccess(new ObjectId(room), userRoles))
                        .handle((res, ex) -> {
                            System.out.println(res + " kkkk");
                             if(ex != null)
                                 return F.Either.Left(forbidden(ex.getMessage()));
                            System.out.println(res + " kkkk2");
                            String username = authService.parseToken(requestHeader.getHeaders().get("token").get());

                             switch (res) {
                                 case NULL: return F.Either.Left(forbidden("You don't have the required ACL to join this room!"));
                                 case READ: return F.Either.Right(ActorFlow.actorRef((out) -> ChatActor.props(out, room, username, false), actorSystem, materializer));
                                 case WRITE: return F.Either.Right(ActorFlow.actorRef((out) -> ChatActor.props(out, room, username, true), actorSystem, materializer));
                                 case ROOM_NOT_FOUND: return F.Either.Left(notFound("Room not found!"));
                             }

                            return F.Either.Left(internalServerError("Something went wrong"));
                    })
            );
    }

}