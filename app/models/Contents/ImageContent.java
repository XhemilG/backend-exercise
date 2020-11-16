package models.Contents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
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
