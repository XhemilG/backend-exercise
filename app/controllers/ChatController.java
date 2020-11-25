package controllers;

import actors.ChatActor;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import play.libs.F;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.WebSocket;
import services.AuthenticationService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

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

    public WebSocket chat (String room) {
        return WebSocket.Text.acceptOrResult((requestHeader ->
                CompletableFuture.supplyAsync(() -> {
                   try{
                       String token = requestHeader.getHeaders().get("token").get();

                       String username = authService.parseToken(token);

                       return F.Either.Right(ActorFlow.actorRef((out) -> ChatActor.props(out, room, username), actorSystem, materializer));
                   } catch (NoSuchElementException | SignatureException | ExpiredJwtException ex) {
                       return F.Either.Left(forbidden(Json.toJson("You need to login first!")));
                   }
                }, ec.current())
        ));
    }

}