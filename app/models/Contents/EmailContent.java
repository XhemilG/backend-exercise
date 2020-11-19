package models.Contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "EMAIL")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailContent extends BasicContent{
    String text;
    String subject;
    String email;

    @Override
    public ContentType getType() {
        return ContentType.EMAIL;
    }
}
