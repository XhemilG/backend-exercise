package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mongo.serializers.ListObjectIdDeSerializer;
import mongo.serializers.ListObjectIdSerializer;
import mongo.serializers.ObjectIdDeSerializer;
import mongo.serializers.ObjectIdStringSerializer;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoom {

    @BsonId
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    ObjectId id;

    @JsonSerialize(using = ListObjectIdSerializer.class)
    @JsonDeserialize(using = ListObjectIdDeSerializer.class)
    List<ObjectId> readACL;

    @JsonSerialize(using = ListObjectIdSerializer.class)
    @JsonDeserialize(using = ListObjectIdDeSerializer.class)
    List<ObjectId> writeACL;
}
