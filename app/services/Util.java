package services;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Variable;
import exceptions.RequestException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import models.NameValuePair;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.NOT_FOUND;

public class Util {

    @com.google.inject.Inject
    AuthenticationService authService;

    @com.google.inject.Inject
    HttpExecutionContext ec;

    private static final String READ_ACL_FIELD_NAME = "readACL";
    private static final String WRITE_ACL_FIELD_NAME = "writeACL";

    public Bson authorizationFilter(boolean isForReading, List<ObjectId> authIndicatorIds, NameValuePair ... fieldsToBeCheckedForEquality) {
        Bson sizeFilter =
                Filters.and(
                        Filters.size(READ_ACL_FIELD_NAME, 0),
                        Filters.size(WRITE_ACL_FIELD_NAME, 0)
                );

        List<Bson> readWriteFilters = new ArrayList<>();
        readWriteFilters.add(Filters.in(WRITE_ACL_FIELD_NAME, authIndicatorIds));

        if(isForReading) {
            readWriteFilters.add(Filters.in(READ_ACL_FIELD_NAME, authIndicatorIds));
        }

        Bson authorizationFilter = Filters.or(readWriteFilters);

        Bson basicFilter = Filters.or(
                sizeFilter,
                authorizationFilter
        );

        Bson result = basicFilter;

        if(fieldsToBeCheckedForEquality != null && fieldsToBeCheckedForEquality.length > 0) {
            List<Bson> extraConditions = new ArrayList<>();
            for (NameValuePair pair : fieldsToBeCheckedForEquality) {
                extraConditions.add(Filters.eq(pair.getName(), new ObjectId(pair.getValue())));
            }

            Bson extraFilter = Filters.and(
                    extraConditions
            );

            result = Filters.and(
                    basicFilter,
                    extraFilter
            );
        }
        return result;
    }

    public List<Bson> lookup(boolean isToRead, List<ObjectId> authIndicatorIds, String fromCollection, String localField, String externalField) {
        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Aggregates.match(authorizationFilter(isToRead, authIndicatorIds)));

        List<Bson> secondaryPipeline = new ArrayList<>();
        secondaryPipeline.add(
                Aggregates.match(
                        new Document("$expr",
                                new Document("$eq",
                                        Arrays.asList("$" + externalField, "$$id")))
                )
        );
        secondaryPipeline.add(Aggregates.match(authorizationFilter(isToRead, authIndicatorIds)));

        List<Variable<String>> variables = new ArrayList<>();
        variables.add(new Variable<>("id", "$" + localField));

        pipeline.add(
                Aggregates.lookup(fromCollection, variables, secondaryPipeline, fromCollection)
        );

        return pipeline;
    }

    public ObjectId getObjectIdIfValid(String id) {
        try{
            return new ObjectId(id);
        } catch (IllegalArgumentException ex) {
            throw new CompletionException(new RequestException(NOT_FOUND, "Item doesn't exist!"));
        }
    }

    public CompletableFuture<String> getToken(Http.RequestHeader request) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                String token = request.getHeaders().get("token").get();
                return authService.parseToken(token);
            } catch (NoSuchElementException | SignatureException | ExpiredJwtException ex) {
                throw new CompletionException(new RequestException(FORBIDDEN, ex.getMessage()));
            }
        }, ec.current());
    }
}
