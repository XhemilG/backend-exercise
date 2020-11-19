package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Contents.BasicContent;
import models.Contents.Content;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dashboard {

    @BsonId
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    ObjectId id;
    String name;
    String description;
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    ObjectId parentId;
    String timestamp;
    @JsonSerialize(using = ListObjectIdSerializer.class)
    @JsonDeserialize(using = ListObjectIdDeSerializer.class)
    List<ObjectId> readACL;
    @JsonSerialize(using = ListObjectIdSerializer.class)
    @JsonDeserialize(using = ListObjectIdDeSerializer.class)
    List<ObjectId> writeACL;
    List<Content> contents;
}
