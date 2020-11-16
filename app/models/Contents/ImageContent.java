package models.Contents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageContent extends BasicContent{

    String url;

    @Override
    public ContentType getType() {
        return ContentType.IMAGE;
    }
}
