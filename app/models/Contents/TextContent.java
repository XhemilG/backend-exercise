package models.Contents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextContent extends BasicContent{

    String text;

    @Override
    public ContentType getType() {
        return ContentType.TEXT;
    }
}
