package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prompt {
    private String organizationId;
    private String id;
    private Long lastUpdate;
    private Long created;
    private String name;
    private String text;
}
