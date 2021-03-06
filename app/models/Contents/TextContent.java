package models.Contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "TEXT")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextContent extends Content {

    @NotEmpty
    String text;

    @Override
    public ContentType getType() {
        return ContentType.TEXT;
    }
}
