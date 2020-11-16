package models.Contents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailContent extends BasicContent{
    String text;
    String subject;
    String email;

    @Override
    public ContentType getType() {
        return ContentType.EMAIL;
    }
}
