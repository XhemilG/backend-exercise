package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.Contents.BasicContent;
import mongo.serializers.ListObjectIdDeSerializer;
import mongo.serializers.ListObjectIdSerializer;
import mongo.serializers.ObjectIdDeSerializer;
import mongo.serializers.ObjectIdStringSerializer;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dashboard {

    @BsonId
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    ObjectId id;

    @NotEmpty
    String name;

    String description;

    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    ObjectId parentId;

    @Min(0)
    Long timestamp;

    @JsonSerialize(using = ListObjectIdSerializer.class)
    @JsonDeserialize(using = ListObjectIdDeSerializer.class)
    List<ObjectId> readACL;

    @JsonSerialize(using = ListObjectIdSerializer.class)
    @JsonDeserialize(using = ListObjectIdDeSerializer.class)
    List<ObjectId> writeACL;

    List<BasicContent> contents;

    @BsonIgnore
    List<Dashboard> children;
}
