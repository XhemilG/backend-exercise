package models.Contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.Contents.charts.BarChart;
import models.Contents.charts.LineChart;
import models.Contents.charts.PieChart;
import models.Contents.charts.TreeMapChart;
import mongo.serializers.ObjectIdDeSerializer;
import mongo.serializers.ObjectIdStringSerializer;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, property="type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value= BarChart.class, name = "Bar"),
        @JsonSubTypes.Type(value= PieChart.class, name = "Pie"),
        @JsonSubTypes.Type(value= LineChart.class, name = "Line"),
        @JsonSubTypes.Type(value= TreeMapChart.class, name = "Treemap"),
        @JsonSubTypes.Type(value= EmailContent.class, name = "EMAIL"),
        @JsonSubTypes.Type(value= TextContent.class, name = "TEXT"),
        @JsonSubTypes.Type(value= ImageContent.class, name = "IMAGE")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BasicContent implements Content{

    @BsonId
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    ObjectId id;
    ObjectId dashboardId;
    List<ObjectId> readACL;
    List<ObjectId> writeACL;
    Type type = ContentType.NONE;
}
