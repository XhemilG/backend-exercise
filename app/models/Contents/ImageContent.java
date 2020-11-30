package models.Contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "IMAGE")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageContent extends Content {

    @NotEmpty
    @URL
    String url;

    @Override
    public ContentType getType() {
        return ContentType.IMAGE;
    }
}
