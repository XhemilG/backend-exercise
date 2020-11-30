package models.Contents.charts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.Contents.ContentType;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@BsonDiscriminator(key = "type", value = "Pie")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PieChart extends Chart {

    @Override
    public ContentType getType() {
        return ContentType.Pie;
    }
}
