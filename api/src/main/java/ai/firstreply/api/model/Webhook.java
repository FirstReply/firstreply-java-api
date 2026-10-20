package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Webhook {
    private String id;
    private String organizationId;
    private String target;
    private String event;
    private Long lastUpdate;
    private Long created;
}
