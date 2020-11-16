package models.Contents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
