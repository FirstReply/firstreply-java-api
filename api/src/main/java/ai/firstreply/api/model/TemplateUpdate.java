package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateUpdate {
    private String name;
    private String language;
    private String parentId;
    private String plain;
    private String html;
}
