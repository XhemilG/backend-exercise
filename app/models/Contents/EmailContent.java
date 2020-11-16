package models.Contents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
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
