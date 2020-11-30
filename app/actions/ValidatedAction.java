package actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import models.validators.HibernateValidator;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ValidatedAction extends Action<Validated> {

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        try{
            JsonNode node = request.body().asJson();
            Object user = Json.fromJson(node, configuration.value());

            String errors = HibernateValidator.validate(user);
            if (!Strings.isNullOrEmpty(errors)) {
                return CompletableFuture.completedFuture(badRequest(errors));
            }
            request = request.addAttr(Attributes.TYPED_KEY, user);

            return delegate.call(request);
        } catch (RuntimeException ex){
            return CompletableFuture.completedFuture(badRequest(Json.toJson(ex.getMessage())));
        }
    }
}