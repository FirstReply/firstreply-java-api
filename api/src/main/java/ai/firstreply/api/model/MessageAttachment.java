package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAttachment {
    private String id;
    private String name;
    private int size;
    private String mime;
    private String file;
    private String contentId;
}
