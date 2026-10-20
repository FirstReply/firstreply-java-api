package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Template {
    private String id;
    private Long lastUpdate;
    private Long created;
    private String organizationId;
    private String name;
    private String language;
    private String parentId;
    private String parentName;
    private String plain;
    private String html;
}
