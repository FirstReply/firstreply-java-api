package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadata of an organization API key. Never contains the key value itself;
 * {@code prefix} is a non-secret display fragment (e.g. {@code fr_ab12cd34}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {
    private String id;
    private String organizationId;
    private String name;
    private String prefix;
    private Long expiresAt;
    private Long lastUsed;
    private Long lastUpdate;
    private Long created;
}
