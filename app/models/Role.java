package models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mongo.serializers.ObjectIdDeSerializer;
import mongo.serializers.ObjectIdStringSerializer;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @BsonId
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    public ObjectId id;
    String name;
}
